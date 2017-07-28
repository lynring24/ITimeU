package com.itti7.itimeu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itti7.itimeu.data.ItemContract.ItemEntry;
/**
 * Created by hyemin on 17. 7. 28.
 */

public class ItemDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "iTimeU.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE list
        // (id INTEGER PRIMARY KEY, name TEXT, quantity TEXT, date Date, status INTEGER);
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_LIST_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + "("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " TEXT, "
                + ItemEntry.COLUMN_ITEM_DATE + " TEXT, "
                + ItemEntry.COLUMN_ITEM_STATUS + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
