package com.itti7.itimeu;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itti7.itimeu.data.ItemContract.ItemEntry;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Displays list of pets that were entered and stored in the app.
 */

public class ListActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader
     */
    private static final int ITEM_LOADER = 0;

    // ListView
    ListView itemListView;

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
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // show today's date
        mListDate = new Date();
        mDate = getDate(mListDate);
        mDateButton = (Button) findViewById(R.id.date_btn);
        mDateButton.setText(mDate);

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
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog
                        = DatePickerDialog.newInstance(ListActivity.this, year, month, day);
                datePickerDialog.show(getFragmentManager(), "DateFragment");
            }
        });

        //when user click add FloatingActionButton for add a item in the list.
        final FloatingActionButton addFab
                = (FloatingActionButton) this.findViewById(R.id.add_fab_btn);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, EditorActivity.class);
                intent.putExtra("date", mDate);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the item data
        itemListView = (ListView) findViewById(R.id.item_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_relative_view);
        itemListView.setEmptyView(emptyView);

        mCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        //displayListByDate();
        // Touch and hold the item to display the context menu (modify/delete).
        registerForContextMenu(itemListView);

        //Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    /**
     * create context menu for modification or deletion
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);

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

        Intent intent = new Intent(ListActivity.this, EditorActivity.class);
        Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
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
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_TOTAL_UNIT,
                ItemEntry.COLUMN_ITEM_UNIT};

        String[] date = { mDate };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ItemEntry.CONTENT_URI,  // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                "date = ?",             // Date selection clause
                date,                   // Date selection arguments
                null);                  // Default sort order
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog(final int index) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirm_msg));
        builder.setPositiveButton(getString(R.string.delete_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem(index);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
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
        Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

        // Only perform the delete if this is an existing pet.
        if (currentItemUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_item_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_item_success),
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
     *
     * @param view              DatePickerDialog
     * @param selectedYear      Year selected by the user
     * @param selectedMonth     Month selected by the user
     * @param selectedDay       Date selected by the user
     */
    @Override
    public void onDateSet(DatePickerDialog view, int selectedYear, int selectedMonth, int selectedDay) {
        Calendar calendar = Calendar.getInstance();

        // Assign Selected Date in DatePickerDialog
        year = selectedYear;
        month = selectedMonth;
        day = selectedDay;

        // Set Date in List
        calendar.set(year, month, day);
        mListDate = calendar.getTime();
        mDate = getDate(mListDate);
        mDateButton.setText(mDate);

        // Update List Date
        getLoaderManager().restartLoader(0, null, this);
    }
}