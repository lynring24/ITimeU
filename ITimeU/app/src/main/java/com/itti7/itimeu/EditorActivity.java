package com.itti7.itimeu;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itti7.itimeu.data.ItemContract.ItemEntry;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Allows user to create a new item or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the item data loader */
    private static final int EXISTING_ITEM_LOADER = 0;

    /** Content URI for the existing item (null if it's a new item) */
    private Uri mCurrentItemUri;

    /** EditText field to enter the item's name */
    private EditText mNameEditText;

    /** EditText field to enter the item's quantity */
    private EditText mQuantityEditText;

    /** TextView field to enter the item's total unit */
    private TextView mTotalUnitTextView;

    /** ImageButton to increase total unit number */
    private ImageButton mUnitPlusImageButton;

    /** ImageButton to decrease total unit number */
    private ImageButton mUnitMinusImageButton;

    /** Total unit convert integer value */
    private int mTotalUnitNumber;

    /** Creation date */
    private String mDate;

    /**
     * Status of the item. The possible valid values are in the ItemContract.java file:
     * {@link ItemEntry#STATUS_TODO}, {@link ItemEntry#STATUS_DO}, {@link ItemEntry#STATUS_DONE}
     * */
    private int mStatus = ItemEntry.STATUS_TODO;

    /** Boolean flag that keeps track of whether the item has been edited (true) or not (false) */
    private boolean mItemHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain a item content URI, then we know that we are
        // creating a new item.
        TextView titleTextView = (TextView) findViewById(R.id.editor_title_txt_view);
        if (mCurrentItemUri == null) {
            // This is a new item, so change the title in TextView "ADD"
            titleTextView.setText(R.string.editor_title_add);
        } else {
            // Otherwise this is an existing item, so change the title in TextView "EDIT"
            titleTextView.setText(R.string.editor_title_edit);

            // Initialize a loader to read the item data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.name_edit_txt);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit_txt);
        mTotalUnitTextView = (TextView) findViewById(R.id.get_total_unit_txt_view);
        mDate = getDate();
        mTotalUnitNumber = Integer.parseInt(mTotalUnitTextView.getText().toString().trim());

        mUnitMinusImageButton = (ImageButton) findViewById(R.id.unit_minus_btn);
        mUnitPlusImageButton = (ImageButton) findViewById(R.id.unit_plus_btn);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mUnitMinusImageButton.setOnTouchListener(mTouchListener);
        mUnitPlusImageButton.setOnTouchListener(mTouchListener);

        // change total unit number
        getTotalUnitNumber();

        // click ok
        submit();
    }

    /**
     * Get user input from editor and save item into database.
     */
    private boolean saveItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        // Check whether name edit text is empty.
        // if name is empty return false, else save the item than return true
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.startAnimation(shake);
            Toast.makeText(this, "input name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // Create a ContentValues object where column names are the keys,
            // and item attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);
            values.put(ItemEntry.COLUMN_ITEM_TOTAL_UNIT, mTotalUnitNumber);
            values.put(ItemEntry.COLUMN_ITEM_STATUS, mStatus);
            values.put(ItemEntry.COLUMN_ITEM_DATE, mDate);

            // Determine if this is a new or existing item by checking
            // if mCurrentItemUri is null or not
            if (mCurrentItemUri == null) {
                // This is a NEW item, so insert a new item into the provider,
                // returning the content URI for the new item.
                Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, "fail to create item", Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, "success to create item", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, "fail to update item data", Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, "success to update item data", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }

    /**
     * Save or cancel items.
     */
    private void submit() {
        Button okButton = (Button) findViewById(R.id.add_ok_btn);
        Button cancelButton = (Button) findViewById(R.id.add_cancel_btn);

        //click ok button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // success to insert item
                if (saveItem()) finish();
            }
        });

        //click cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemHasChanged) {
                    // if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    finish();
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                }
                else finish();
            }
        });
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the item table
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_DATE,
                ItemEntry.COLUMN_ITEM_TOTAL_UNIT,
                ItemEntry.COLUMN_ITEM_UNIT,
                ItemEntry.COLUMN_ITEM_STATUS
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int unitColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_UNIT);
            int totalUnitColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_TOTAL_UNIT);
            int statusColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_STATUS);
            int dateColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_DATE);

            String name = cursor.getString(nameColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            int unit = cursor.getInt(unitColumnIndex);
            int totalUnit = cursor.getInt(totalUnitColumnIndex);
            int status = cursor.getInt(statusColumnIndex);

            mNameEditText.setText(name);
            mQuantityEditText.setText(quantity);
            mTotalUnitNumber = totalUnit;
            mTotalUnitTextView.setText(Integer.toString(totalUnit));

            getTotalUnitNumber();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mTotalUnitTextView.setText("1");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unsaved change");
        builder.setPositiveButton("discard", discardButtonClickListener);
        builder.setNegativeButton("keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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
     * It is a function of today's date.
     *
     * @return Return the current month and day.
     */
    public String getDate() {
        String today = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        return today;
    }

    /**
     * this function change unit number in unit text view according to plus/minus image button.
     * minimum number: 1 / maximum number: 20
     * ut
     */
    private void getTotalUnitNumber() {
        getUnitImageButtonSrc();
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