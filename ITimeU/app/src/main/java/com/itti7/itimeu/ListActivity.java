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
import android.widget.Button;
import android.widget.EditText;
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
        addFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAddDialog();
            }
        });

        mDbHelper = new ItemDbHelper(this);
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

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the list table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + ItemEntry.TABLE_NAME, null);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.item_txt_view);
            displayView.setText("Number of rows in list database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * It is a function of today's date.
     * @return  Return the current month and day.
     */
    public String getDate(){
        String today = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        return today;
    }

    // insert a To do item in the list
    private void insertItem(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String dateString = mDate;
        int status = mStatus;

        ContentValues values = new ContentValues();

        values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);
        values.put(ItemEntry.COLUMN_ITEM_DATE, dateString);
        values.put(ItemEntry.COLUMN_ITEM_STATUS, status);

        long newRowId = db.insert(ItemEntry.TABLE_NAME, null, values);
    }

    /**
     * This function open the dialog window to add the item for TdDo list.
     */
    private void showAddDialog(){
        LayoutInflater dialog = LayoutInflater.from(this);

        //assign Dialog
        final View dialogLayout = dialog.inflate(R.layout.add_dialog, null);
        final Dialog addDialog = new Dialog(this);

        addDialog.setContentView(dialogLayout);
        addDialog.show();

        Button mOkButton = dialogLayout.findViewById(R.id.add_ok_btn);
        Button mCancelButton = dialogLayout.findViewById(R.id.add_cancel_btn);

        mOkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mNameEditText = dialogLayout.findViewById(R.id.name_edit_txt);
                mQuantityEditText = dialogLayout.findViewById(R.id.quantity_edit_txt);
                mDate = getDate();

                insertItem();
                displayDatabaseInfo();
                addDialog.dismiss();
                dialogLayout.setVisibility(View.INVISIBLE);
                //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addDialog.cancel();
            }
        });
    }
}
