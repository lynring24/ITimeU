package com.itto3.itimeu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.itto3.itimeu.ListItemFragment;
import com.itto3.itimeu.MainActivity;

/**
 * Created by Admin on 2017-09-22.
 */

public class TimerDbUtil {

    /** Database helper object */
    private static ItemDbHelper dbHelper;

    public static void update(Context context, int value, int mId, boolean isUnit) {
        if(dbHelper==null)
            dbHelper = new ItemDbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String query=null;
        if(isUnit)
            query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET unit = '" + value + "' WHERE _ID = '" + mId + "';";
        else
            query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET status = '" + value + "' WHERE _ID = '" + mId + "';";
        database.execSQL(query);
        database.close();
        updateListFragment(context);
    }
    public  static void update(Context context, int unit, int status, int mId) {
        if(dbHelper==null)
            dbHelper = new ItemDbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET unit = '" + unit + "', status = '" + status + "' WHERE _ID = '" + mId + "';";
        database.execSQL(query);
        database.close();
        updateListFragment(context);
    }
    public static void updateListFragment(Context context) {
        /*List Item unit count update*/
        MainActivity mainActivity = (MainActivity)context;
        String listTag = mainActivity.getListTag();
        ListItemFragment listItemFragment = (ListItemFragment) mainActivity.getSupportFragmentManager().findFragmentByTag(listTag);
        listItemFragment.listUiUpdateFromDb();
    }
}
