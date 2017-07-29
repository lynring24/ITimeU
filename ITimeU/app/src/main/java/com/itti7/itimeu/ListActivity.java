package com.itti7.itimeu;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itti7.itimeu.data.ItemContract.ItemEntry;
import com.itti7.itimeu.data.ItemDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */

public class ListActivity extends AppCompatActivity {

    private ItemDbHelper mDbHelper;

    //edit text for add item
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private String mDate;
    private int mStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //show Date
        TextView textView = (TextView) findViewById(R.id.date_txt_view);
        textView.setText(getDate());

        //when user click add FloatingActionButton for add a item in the list.
        final FloatingActionButton addFab = (FloatingActionButton) this.findViewById(R.id.add_fab_btn);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        mDbHelper = new ItemDbHelper(this);

        // Find the ListView which will be populated with the item data
        ListView petListView = (ListView) findViewById(R.id.item_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_relative_view);
        petListView.setEmptyView(emptyView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextCiew about the state of
     * the list database
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        ItemDbHelper mDbHelper = new ItemDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_DATE,
                ItemEntry.COLUMN_ITEM_STATUS
        };

        Cursor cursor = db.query(
                ItemEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order


        // Find the ListView which will be populated with the pet data
        ListView itemListView = (ListView) findViewById(R.id.item_list_view);

        // Setup an Adapter to create a list item for each row of item data in the Cursor.
        ItemCursorAdapter adapter = new ItemCursorAdapter(this, cursor);

        // Attach the adapter to the ListView.
        itemListView.setAdapter(adapter);
    }

    /**
     * insert a To do item in the list
     *
     * @return success to insert or not
     */
    private boolean insertItem() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String dateString = mDate;
        int status = mStatus;

        //Check whether there is an empty space.
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (nameString.equals("")) {
            mNameEditText.startAnimation(shake);
            return false;
        }

        // put item value
        ContentValues values = new ContentValues();

        values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);
        values.put(ItemEntry.COLUMN_ITEM_DATE, dateString);
        values.put(ItemEntry.COLUMN_ITEM_STATUS, status);

        // get row id of inserted item
        long newRowId = db.insert(ItemEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Item saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    /**
     * This function open the dialog window to add the item for TdDo list.
     */
    private void showAddDialog() {
        LayoutInflater dialog = LayoutInflater.from(this);

        //assign Dialog
        final View dialogLayout = dialog.inflate(R.layout.add_dialog, null);
        final Dialog addDialog = new Dialog(this);

        addDialog.setContentView(dialogLayout);
        addDialog.show();

        Button mOkButton = dialogLayout.findViewById(R.id.add_ok_btn);
        Button mCancelButton = dialogLayout.findViewById(R.id.add_cancel_btn);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameEditText = dialogLayout.findViewById(R.id.name_edit_txt);
                mQuantityEditText = dialogLayout.findViewById(R.id.quantity_edit_txt);
                mDate = getDate();

                // success to insert item
                if (insertItem() == true) {
                    displayDatabaseInfo();
                    addDialog.dismiss();
                    dialogLayout.setVisibility(View.INVISIBLE);
                }
                //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.cancel();
            }
        });
    }

    /**
     * It is a function of today's date.
     *
     * @return Return the current month and day.
     */
    public String getDate() {
        String today = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        return today;
    }

}
