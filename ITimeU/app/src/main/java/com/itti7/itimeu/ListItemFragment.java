package com.itti7.itimeu;

import android.content.ClipData;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itti7.itimeu.data.ItemContract;
import com.itti7.itimeu.data.ItemDbHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ListItemFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

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

    // ListView
    ListView mListView;

    // Show date text
    Button mDateButton;

    // Simple date format
    public static final String DATE_FORMAT = "yyyy.MM.dd";

    // List's date
    private Date mListDate;

    // Date convert to String
    private String mDate;

    /**
     * Adapter for the ListView
     */
    ItemCursorAdapter mCursorAdapter;

    // Date year, month, day;
    private int mYear, mMonth, mDay;

    private String mItemName;
    private String mItemDate;
    private int mItemUnit;
    private int mItemStatus;

    // Sum total units, and units respectively.
    private int mSumOfTotalUnits, mSumOfUnits;
    private double mPercent;

    private String mPercentStr;
    private String mDetail;

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

        // list table db
        dbHelper = new ItemDbHelper(mListItemContext);
        db = dbHelper.getReadableDatabase();

        // Find the ListView which will be populated with the item data
        mListView = mListItemView.findViewById(R.id.item_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = mListItemView.findViewById(R.id.empty_relative_view);
        mListView.setEmptyView(emptyView);

        mCursorAdapter = new ItemCursorAdapter(mListItemContext, null);
        mListView.setAdapter(mCursorAdapter);

        // When click item, get the item's name and unit
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String[] idStr = {String.valueOf(id)};
                Cursor cursor = db.rawQuery("SELECT name, unit, status, date FROM list WHERE "
                        + BaseColumns._ID + " = ?", idStr);

                if (cursor.moveToFirst()) {
                    mItemName = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME));
                    mItemDate =  cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DATE));
                    mItemUnit = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT));
                    mItemStatus = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_STATUS));

                    Toast.makeText(mListItemContext, "name: " + cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME))
                                    + ", unit: " + cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT))
                                    + ", status: " + cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_STATUS))
                                    + ", date: " + cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DATE)),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // show today's date
        mListDate = new Date();
        mDate = getDate(mListDate);
        mDateButton = mListItemView.findViewById(R.id.date_btn);
        mDateButton.setText(mDate);

        setAchievementRate();

        // If click date TextView
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date;
                if (mDate != null) {
                    date = mListDate;
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

        //when user click add FloatingActionButton for add a item in the list.
        final FloatingActionButton addFab
                = mListItemView.findViewById(R.id.add_fab_btn);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mListItemContext, EditorActivity.class);
                intent.putExtra("date", mDate);
                startActivity(intent);
            }
        });

        // displayListByDate();
        // Touch and hold the item to display the context menu (modify/delete).
        registerForContextMenu(mListView);

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
        setAchievementRate();
        // Update List Date
        getLoaderManager().restartLoader(0, null, this);
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
                startActivity(intent);
                break;
            case R.id.action_delete:
                showDeleteConfirmationDialog(id);
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
                ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT,
                ItemContract.ItemEntry.COLUMN_ITEM_UNIT};

        String[] date = {mDate};

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
                onResume();
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
                Toast.makeText(mListItemContext, getString(R.string.delete_item_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(mListItemContext, getString(R.string.delete_item_success),
                        Toast.LENGTH_SHORT).show();
            }
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
     * It is a function of today's date.
     *
     * @return Return the current month and day.
     */
    public String getDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(date);
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
        mListDate = calendar.getTime();
        mDate = getDate(mListDate);
        mDateButton.setText(mDate);

        setAchievementRate();
        // Update List Date
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Calculate Percentage: sum of units / sum of total-units
     */
    void calculateAchievementRate() {
        mSumOfTotalUnits = 0;
        mSumOfUnits = 0;
        String[] date = {mDate};
        Cursor cursor = db.rawQuery("SELECT totalUnit, unit FROM list WHERE date = ?", date);

        if (cursor.moveToFirst()) {
            do {
                mSumOfTotalUnits += cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT));
                mSumOfUnits += cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT));
            } while (cursor.moveToNext());

            if (mSumOfTotalUnits != 0) {
                mPercent = Math.round(((double) mSumOfUnits / mSumOfTotalUnits) * 100);
            } else {
                mPercent = 0;
            }
        }
        cursor.close();

        mPercentStr = "  " + mPercent + " %";
        mDetail = "( " + mSumOfUnits + " / " + mSumOfTotalUnits + " )";
    }

    /**
     * Setting Achievement rate in TextView
     */
    void setAchievementRate() {
        calculateAchievementRate();
        // Find the TextView which will show sum of units / sum of total units in list's date
        mAchievementTextView = mListItemView.findViewById(R.id.achievement_rate_txt_view);
        mAchievementTextView.setText(mPercentStr);
        mDetailRateTextView = mListItemView.findViewById(R.id.rate_detail_txt_view);
        mDetailRateTextView.setText(mDetail);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
