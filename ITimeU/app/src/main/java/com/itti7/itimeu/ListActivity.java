package com.itti7.itimeu;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader
     */
    private static final int ITEM_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    ItemCursorAdapter mCursorAdapter;

    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentItemUri;

    private ItemDbHelper mDbHelper;

    //edit text for add item
    /**
     * EditText field to enter the To do item's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the To do item's quantity
     */
    private EditText mQuantityEditText;

    /**
     * TextView field to enter the To do item's Total unit
     */
    private TextView mTotalUnitTextView;

    /**
     * Date when the item is created
     */
    private String mDate;

    /**
     * status of item. The possible valid values are in the ItemContract.java file:
     * {@link ItemEntry#STATUS_TODO},
     * {@link ItemEntry#STATUS_DO},
     * {@link ItemEntry#STATUS_DONE}.
     */
    private int mStatus = ItemEntry.STATUS_TODO;

    //dialog var
    private int mTotalUnitNumber = 0;
    private ImageButton mUnitPlusImageButton;
    private ImageButton mUnitMinusImageButton;

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
        ListView itemListView = (ListView) findViewById(R.id.item_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_relative_view);
        itemListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of item data in the Cursor.
        // There is no item data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                //TODO: create modify/delete dialog list
                // Form the content URI that represents the specific item that was long clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ItemEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.itti7.itimeu/itimeu/2"
                // if the pet with ID 2 was long clicked on.
                mCurrentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                return true;
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Show database list
     */

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        ItemDbHelper mDbHelper = new ItemDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_DATE,
                ItemEntry.COLUMN_ITEM_TOTAL_UNIT,
                ItemEntry.COLUMN_ITEM_UNIT,
                ItemEntry.COLUMN_ITEM_STATUS
        };

        // Perform a query on the list table
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
        int totalUnit = mTotalUnitNumber;

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
        values.put(ItemEntry.COLUMN_ITEM_TOTAL_UNIT, totalUnit);

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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_TOTAL_UNIT
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ItemEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
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
    public String getDate() {
        String today = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        return today;
    }




    /**
     * Edit Dialog main function
     * <p>
     * This function open the dialog window to add the item for TdDo list.
     */
    private void showAddDialog() {
        LayoutInflater dialog = LayoutInflater.from(this);

        // call Dialog
        final View dialogLayout = dialog.inflate(R.layout.editor_dialog, null);
        final Dialog addDialog = new Dialog(this);

        addDialog.setContentView(dialogLayout);
        addDialog.show();

        getUnitNumber(dialogLayout);

        submit(dialogLayout, addDialog);
    }

    /**
     * Dialog submit function
     *
     * @param dialogLayout dialog Layout
     * @param addDialog    dialog
     */
    private void submit(final View dialogLayout, final Dialog addDialog) {
        // OK or Cancel Button
        Button mOkButton = dialogLayout.findViewById(R.id.add_ok_btn);
        Button mCancelButton = dialogLayout.findViewById(R.id.add_cancel_btn);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameEditText = dialogLayout.findViewById(R.id.name_edit_txt);
                mQuantityEditText = dialogLayout.findViewById(R.id.quantity_edit_txt);
                mDate = getDate();

                // success to insert item
                if (insertItem()) {
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
     * Dialog function
     * <p>
     * this function change unit number in unit text view according to plus/minus image button.
     * minimum number: 1 / maximum number: 20
     *
     * @param dialogLayout add dialog layout
     */
    private void getUnitNumber(View dialogLayout) {
        // Unit plus, minus image button
        mUnitPlusImageButton = dialogLayout.findViewById(R.id.unit_plus_btn);
        mUnitMinusImageButton = dialogLayout.findViewById(R.id.unit_minus_btn);

        // get number from unit textview
        // and whether to activate buttons according to numeric range
        mTotalUnitTextView = dialogLayout.findViewById(R.id.get_total_unit_txt_view);
        mTotalUnitNumber = Integer.parseInt(mTotalUnitTextView.getText().toString());

        // increase unit number
        mUnitPlusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTotalUnitNumber < 20) {
                    mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_true);
                    mTotalUnitNumber++;
                    mTotalUnitTextView.setText("" + mTotalUnitNumber);
                }
                getUnitImageButtonSrc();
            }
        });

        // decrease unit number
        mUnitMinusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTotalUnitNumber > 1) {
                    mTotalUnitNumber--;
                    mTotalUnitTextView.setText("" + mTotalUnitNumber);
                }
                getUnitImageButtonSrc();
            }
        });
    }

    /**
     * Dialog function
     * <p>
     * This function change plus/minus imageButton src according to Unit number range
     */
    private void getUnitImageButtonSrc() {
        if (mTotalUnitNumber <= 1) {
            mUnitMinusImageButton.setImageResource(R.drawable.ic_unit_minus_false);
            mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_true);
        } else if (mTotalUnitNumber < 20) {
            mUnitMinusImageButton.setImageResource(R.drawable.ic_unit_minus_true);
            mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_true);
        } else {
            mUnitMinusImageButton.setImageResource(R.drawable.ic_unit_minus_true);
            mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_false);
        }
    }

}
