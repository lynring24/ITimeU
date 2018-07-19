package com.itto3.itimeu;

import android.animation.ValueAnimator;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.itto3.itimeu.data.ItemContract;
import com.itto3.itimeu.data.ItemDbHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ListItemFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    // For access ITimeU database
    ItemDbHelper dbHelper;
    SQLiteDatabase db;

    /**
     * Identifier for the item data loader
     */
    private View mListItemView;
    private Activity mListItemActivity;
    private Context mListItemContext;

    private static final int ITEM_LOADER = 0;

    // TextView for showing achievement rate
    TextView mAchievementTextView;

    // TextView for showing detail for achievement rate
    TextView mDetailRateTextView;

    // List View
    ListView mTaskItemListView;

    // Empty view
    View mEmptyView;

    // Show date text
    TextView mDateTextView;

    // date image button
    ImageButton mPreviousDateImgBtn, mNextDateImgBtn;

    // Simple date format
    public static final String DATE_FORMAT = "yyyy.MM.dd";

    // List's date
    private Date mCurrentListDate;

    // Date convert to String
    private String mCurrentListDateStr;

    private boolean isOtherItemSelected = false;

    /**
     * Adapter for the ListView
     */
    ItemCursorAdapter mCursorAdapter;

    // Date year, month, day;
    private int mYear, mMonth, mDay;

    // Save selected item data
    private int mItemID;
    private String mItemName;
    private String mItemDate;
    private int mItemUnit;
    private int mItemTotalUnit;
    private int mItemStatus;

    private int mPercent;
    private String mDetail;

    // object for showing toast message
    private SimpleToast toast;

    final static int REQUEST_NUMBER = 0;

    public ListItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mListItemView = inflater.inflate(R.layout.fragment_list_item, container, false);
        mListItemActivity = getActivity();
        mListItemContext = mListItemView.getContext();

        // Create new SimpleToast object
        toast = new SimpleToast(mListItemContext);

        // list table db
        dbHelper = new ItemDbHelper(mListItemContext);
        db = dbHelper.getReadableDatabase();

        // Find previous / next date image button
        mPreviousDateImgBtn = mListItemView.findViewById(R.id.listitem_previous_date_imgbtn);
        mNextDateImgBtn = mListItemView.findViewById(R.id.listitem_next_date_imgbtn);

        loadingPreviousOrNextDateList();

        // Find the ListView which will be populated with the item data
        mTaskItemListView = mListItemView.findViewById(R.id.item_list_view);

        // When task list view is empty, than show this view
        setEmptyView();

        // When click task item, then check item's information.
        getTaskItemInfoAndCheck();

        // get List tag and set to ListTag
        ((MainActivity) getActivity()).setListTag(getTag());

        // show today's date
        mCurrentListDate = new Date();
        mCurrentListDateStr = getStringFromDate(mCurrentListDate);
        mDateTextView = mListItemView.findViewById(R.id.date_btn);
        mDateTextView.setText(mCurrentListDateStr+" (Today)");

        // set achievement rate in current date
        setAchievementRate();

        // If click date TextView
        showDialogForSelectDate();

        //when user click add FloatingActionButton for add a item in the list.
        final FloatingActionButton addFab
                = mListItemView.findViewById(R.id.add_fab_btn);
        clickAddFab(addFab);

        // displayListByDate();
        // Touch and hold the item to display the context menu (modify/delete).
        registerForContextMenu(mTaskItemListView);

        //Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

        return mListItemView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Perform a refresh when other activities are finished.
     */
    @Override
    public void onResume() {
        super.onResume();
        // refresh achievement rate
        setAchievementRate();
    }

    /**
     * create context menu for modification or deletion
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        mListItemActivity.getMenuInflater().inflate(R.menu.menu_editor, menu);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /**
     * show menu list
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // click item's index
        int id = (int) info.id;

        Intent intent = new Intent(mListItemContext, EditorActivity.class);
        Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);
        intent.setData(currentItemUri);

        switch (item.getItemId()) {
            case R.id.action_modify:
                if (!isThisTaskStarted(id)) {
                    startActivityForResult(intent, REQUEST_NUMBER);
                } else toast.showLongTimeToast(R.string.listitem_this_item_started);
                break;
            case R.id.action_delete:
                if (!isThisTaskStarted(id)) {
                    showDeleteConfirmationDialog(id);
                } else toast.showLongTimeToast(R.string.listitem_this_item_started);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_DETAIL,
                ItemContract.ItemEntry.COLUMN_ITEM_STATUS,
                ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT,
                ItemContract.ItemEntry.COLUMN_ITEM_UNIT};

        String[] date = {mCurrentListDateStr};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(mListItemContext,   // Parent activity context
                ItemContract.ItemEntry.CONTENT_URI,  // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                "date = ?",             // Date selection clause
                date,                   // Date selection arguments
                null);                  // Default sort order
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog(final int index) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(mListItemContext);
        builder.setMessage(getString(R.string.delete_confirm_msg));
        builder.setPositiveButton(getString(R.string.delete_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem(index);
                // Update List Date
                listUiUpdateFromDb();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Delete the item of the ID obtained by parameter.
     *
     * @param id item's id
     */
    private void deleteItem(int id) {
        Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);

        // Only perform the delete if this is an existing item.
        if (currentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentItemUri
            // content URI already identifies the item that we want.
            int rowsDeleted
                    = mListItemActivity.getContentResolver().delete(currentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                toast.showShortTimeToast(R.string.delete_item_fail);
                // Set item name text to job_txt_view in TimerFragment
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                toast.showShortTimeToast(R.string.delete_item_success);

                String tabOfTimerFragment = ((MainActivity) getActivity()).getTimerTag();
                TimerFragment timerFragment = (TimerFragment) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentByTag(tabOfTimerFragment);

                timerFragment.setDeleteItemDisable(id);
            }
        } else {
            // Can not delete because of error or selected item is already started.
            toast.showShortTimeToast(R.string.listitem_cannot_delete);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link ITemCursorAdapter} with this new cursor containing updated item data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Date -> String
     *
     * @return Date type date
     */
    public String getStringFromDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(date);
    }

    /**
     * String -> Date
     *
     * @return String type date
     */
    public Date getDateFromString(String date) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT, Locale.KOREA).parse(date);
    }

    /**
     * @param view          DatePickerDialog
     * @param selectedYear  Year selected by the user
     * @param selectedMonth Month selected by the user
     * @param selectedDay   Date selected by the user
     */
    @Override
    public void onDateSet(DatePickerDialog view, int selectedYear, int selectedMonth, int selectedDay) {
        Calendar calendar = Calendar.getInstance();

        // Assign Selected Date in DatePickerDialog
        mYear = selectedYear;
        mMonth = selectedMonth;
        mDay = selectedDay;

        // Set Date in List
        calendar.set(mYear, mMonth, mDay);
        mCurrentListDate = calendar.getTime();
        mCurrentListDateStr = getStringFromDate(mCurrentListDate);
        if (mCurrentListDateStr.equals(getStringFromDate(new Date()))) {
            mDateTextView.setText(mCurrentListDateStr+" (Today)");
        } else {
            mDateTextView.setText(mCurrentListDateStr);
        }

        // Update List Date
        listUiUpdateFromDb();
    }

    /**
     * Calculate Percentage: sum of units / sum of total-units
     */
    void calculateAchievementRate() {
        int mSumOfTotalUnits = 0;
        int mSumOfUnits = 0;
        String[] date = {mCurrentListDateStr};
        Cursor cursor = db.rawQuery("SELECT totalUnit, unit FROM list WHERE date = ?", date);

        if (cursor.moveToFirst()) {
            do {
                mSumOfTotalUnits += cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT));
                mSumOfUnits += cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (mSumOfTotalUnits != 0) {
            mPercent = Math.round(((float) mSumOfUnits / mSumOfTotalUnits) * 100);
        } else {
            mPercent = 0;
        }

        mDetail = "( " + mSumOfUnits + " / " + mSumOfTotalUnits + " )";
    }

    /**
     * Setting Achievement rate in TextView
     */
    void setAchievementRate() {
        calculateAchievementRate();
        // Find the TextView which will show sum of units / sum of total units in list's date
        mAchievementTextView = mListItemView.findViewById(R.id.achievement_rate_txt_view);

        // Show increasing percent animation
        ValueAnimator animator = ValueAnimator.ofInt(0, mPercent);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                String rate = animation.getAnimatedValue().toString() + " %";
                mAchievementTextView.setText(rate);
            }
        });
        animator.start();
        mDetailRateTextView = mListItemView.findViewById(R.id.rate_detail_txt_view);
        mDetailRateTextView.setText(mDetail);
    }

    /**
     * Check item's date
     *
     * @return when item's date == today then return true, but date != today then return false.
     */
    boolean checkDate() {
        if (mItemDate.equals(getStringFromDate(new Date()))) {
            return true;
        } else {
            toast.showShortTimeToast(R.string.not_today);
            return false;
        }
    }

    /**
     * Check item's status.
     * <p>
     * * status == To do then set item's info to Timer and change view List to Timer.
     * status == Do then just change view List to Timer.
     * status == Done then show toast message.
     */
    void checkStatus() {
        // Get MainActivity
        MainActivity mainActivity = (MainActivity) getActivity();

        // If selected item's status == Done, then stop action
        if (mItemStatus == ItemContract.ItemEntry.STATUS_DONE) {
            toast.showShortTimeToast(R.string.already_done);
            return;
        } else if (mItemStatus == ItemContract.ItemEntry.STATUS_TODO) {
            // Is other task is started?
            if (isOtherItemSelected) {
                // re-initialize
                isOtherItemSelected = false;
                return;
            }

            // Set item name text to job_txt_view in TimerFragment
            String tabOfTimerFragment = mainActivity.getTimerTag();
            TimerFragment timerFragment = (TimerFragment) getActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tabOfTimerFragment);

            // Set selected item info
            //public void  setTimerFrag(int mId,int mStatus,int mUnit,int mTotalUnit,String mName)
            timerFragment.setTimerFragment(mItemID, mItemStatus, mItemUnit, mItemTotalUnit, mItemName);

        }

        // Change Fragment ListItemFragment -> TimerFragment
        (mainActivity).getViewPager().setCurrentItem(1);
    }

    /**
     * update UI in list view from database.
     */
    public void listUiUpdateFromDb() {
        getLoaderManager().restartLoader(0, null, this);
        setAchievementRate();
        updateStatisticsGraph();
    }

    /**
     * Get previous date from current date in list fragment view
     *
     * @param simpleCurrentDate The date selected by user in list view
     * @return previous date string via simple date format
     */
    String getPreviousDateFromCurrentDate(String simpleCurrentDate) throws ParseException {
        Date currentDate = getDateFromString(simpleCurrentDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -1);

        mCurrentListDate = calendar.getTime();

        return getStringFromDate(mCurrentListDate);
    }

    /**
     * Get next date from current date in list fragment view
     *
     * @param simpleCurrentDate The date selected by user in list view
     * @return next date string via simple date format
     */
    String getNextDateFromCurrentDate(String simpleCurrentDate) throws ParseException {
        Date currentDate = getDateFromString(simpleCurrentDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 1);

        mCurrentListDate = calendar.getTime();

        return getStringFromDate(mCurrentListDate);
    }

    /**
     * Update list date in list view
     */
    void loadingPreviousOrNextDateList() {
        mPreviousDateImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String previousDate = getPreviousDateFromCurrentDate(mCurrentListDateStr);
                    mCurrentListDateStr = previousDate;
                    if (mCurrentListDateStr.equals(getStringFromDate(new Date()))) {
                        mDateTextView.setText(mCurrentListDateStr+" (Today)");
                    } else {
                        mDateTextView.setText(mCurrentListDateStr);
                    }
                    listUiUpdateFromDb();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        mNextDateImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String nextDate = getNextDateFromCurrentDate(mCurrentListDateStr);
                    mCurrentListDateStr = nextDate;
                    if (mCurrentListDateStr.equals(getStringFromDate(new Date()))) {
                        mDateTextView.setText(mCurrentListDateStr+" (Today)");
                    } else {
                        mDateTextView.setText(mCurrentListDateStr);
                    }
                    listUiUpdateFromDb();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Find and set empty view on the ListView, so that it only shows when the list has 0 items.
     */
    void setEmptyView() {
        mEmptyView = mListItemView.findViewById(R.id.empty_relative_view);
        mTaskItemListView.setEmptyView(mEmptyView);
    }

    /**
     * When click task item, than get information from the item.
     */
    void getTaskItemInfoAndCheck() {
        mCursorAdapter = new ItemCursorAdapter(mListItemContext, null);
        mTaskItemListView.setAdapter(mCursorAdapter);

        // When click item, access to the list table in DB
        mTaskItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Get item's primary id
                mItemID = (int) id;
                String[] idStr = {String.valueOf(mItemID)};
                Cursor cursor = db.rawQuery("SELECT name, unit, totalUnit, status, date FROM list WHERE "
                        + BaseColumns._ID + " = ?", idStr);

                // Check the timer is started
                if (isAnotherTaskStarted(mItemID)) {
                    timerIsAlreadyStarted();
                    return;
                }

                // Get current item's info
                if (cursor.moveToFirst()) {
                    mItemName = cursor.getString(
                            cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME));
                    mItemDate = cursor.getString(
                            cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DATE));
                    mItemUnit = cursor.getInt(
                            cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT));
                    mItemTotalUnit = cursor.getInt(
                            cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT));
                    mItemStatus = cursor.getInt(
                            cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_STATUS));

                    // Check selected item's date is today
                    if (checkDate()) {
                        // Check selected item's status
                        checkStatus();
                    }
                }

                cursor.close();
            }
        });
    }

    private void timerIsAlreadyStarted() {
        toast.showShortTimeToast(R.string.already_start);
        isOtherItemSelected = true;
    }

    /**
     * Show data picker dialog, then user can change current list date.
     */
    void showDialogForSelectDate() {
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date;
                if (mCurrentListDateStr != null) {
                    date = mCurrentListDate;
                } else {
                    date = new Date();
                }

                // Setting calender -> list's date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog
                        = DatePickerDialog.newInstance(ListItemFragment.this, mYear, mMonth, mDay);
                datePickerDialog.show(mListItemActivity.getFragmentManager(), "DateFragment");
            }
        });
    }

    /**
     * When click add floating action button, then start EditorActivity.
     *
     * @param addFab floating action button for add task item.
     */
    void clickAddFab(FloatingActionButton addFab) {
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mListItemContext, EditorActivity.class);
                intent.putExtra("date", mCurrentListDateStr);
                startActivityForResult(intent, REQUEST_NUMBER);
            }
        });
    }

    /**
     * This function check that another task item is already started.
     *
     * @param id selected item's id
     * @return if selected item's id is same with the task in execution
     * , or there is nothing in execution, return true. Otherwise return false.
     */
    boolean isAnotherTaskStarted(int id) {
        String[] date = {getStringFromDate(new Date())};
        Cursor cursor = db.rawQuery("SELECT status, " + BaseColumns._ID +
                " FROM list WHERE date = ?", date);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_STATUS))
                        == ItemContract.ItemEntry.STATUS_DO) {
                    return cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry._ID)) != id;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return false;
    }

    /**
     * When click an item long, then check the item is started task.
     *
     * @param id selected item's id
     * @return if the item's status == DO than return true, else return false.
     */
    boolean isThisTaskStarted(int id) {
        String[] strId = {String.valueOf(id)};
        Cursor cursor =
                db.rawQuery("SELECT status FROM list WHERE " + BaseColumns._ID + " =  ?", strId);

        if (cursor.moveToFirst()) {
            if (cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_STATUS))
                    == ItemContract.ItemEntry.STATUS_DO) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public void updateStatisticsGraph() {
        String tabOfStatisticsFragment = ((MainActivity) getActivity()).getStatisticsTag();
        if (tabOfStatisticsFragment != null) {
            StatisticsFragment statisticsFragment = (StatisticsFragment) getActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tabOfStatisticsFragment);

            statisticsFragment.updateChartGraph();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateStatisticsGraph();
    }
}
