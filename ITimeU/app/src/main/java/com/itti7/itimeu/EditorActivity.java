package com.itti7.itimeu;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.app.NavUtils;
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

import com.itti7.itimeu.data.ItemContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;

    private Uri mCurrentItemUri;

    private EditText mNameEditText;

    private EditText mQuantityEditText;

    private TextView mTotalUnitTextView;

    private ImageButton mUnitPlusImageButton;

    private ImageButton mUnitMinusImageButton;

    private int mTotalUnitNumber;

    private String mDate;

    private int mStatus = ItemContract.ItemEntry.STATUS_TODO;

    private boolean mItemHasChanged = false;

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

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        TextView titleTextView = (TextView) findViewById(R.id.editor_title_txt_view);
        if (mCurrentItemUri == null) {
            titleTextView.setText(R.string.editor_title_add);

            invalidateOptionsMenu();
        } else {
            titleTextView.setText(R.string.editor_title_edit);

            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.name_edit_txt);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit_txt);
        mTotalUnitTextView = (TextView) findViewById(R.id.get_total_unit_txt_view);
        mDate = getDate();

        mUnitMinusImageButton = (ImageButton) findViewById(R.id.unit_minus_btn);
        mUnitPlusImageButton = (ImageButton) findViewById(R.id.unit_plus_btn);

        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mUnitMinusImageButton.setOnTouchListener(mTouchListener);
        mUnitPlusImageButton.setOnTouchListener(mTouchListener);

        submit();
    }

    /**
     * Get user input from editor and save item into database.
     */
    private boolean savaItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        //Check whether there is an empty space.
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.startAnimation(shake);
            Toast.makeText(this, "input name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, nameString);
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT, mTotalUnitNumber);

            if (mCurrentItemUri == null) {
                Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, "fail to create item", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "success to create item", Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, "fail to update item data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "success to update item data", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }

    private void submit() {
        Button okButton = (Button) findViewById(R.id.add_ok_btn);
        Button cancelButton = (Button) findViewById(R.id.add_cancel_btn);

        //click ok button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // success to insert item
                if (savaItem()) finish();
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
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_DATE,
                ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT,
                ItemContract.ItemEntry.COLUMN_ITEM_UNIT,
                ItemContract.ItemEntry.COLUMN_ITEM_STATUS
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
            int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            int unitColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT);
            int totalUnitColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT);
            int statusColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_STATUS);
            int dateColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DATE);

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

            getUnitNumber();
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
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("do you want to delete");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "fail to delete",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "success to delete",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
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
    private void getUnitNumber() {
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