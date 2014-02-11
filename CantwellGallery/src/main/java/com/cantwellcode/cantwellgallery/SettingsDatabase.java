package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Daniel on 2/7/14.
 */
public class SettingsDatabase extends SQLiteOpenHelper {

    /* Static Variables */

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "settingsDatabase";

    // Table Name
    private static final String TABLE_SETTINGS = "settings";

    // Table Column Names
    private static final String KEY_BAR_COLOR = "barColor";


    public SettingsDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /* Create the table */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
