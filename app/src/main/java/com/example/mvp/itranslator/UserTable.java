package com.example.mvp.itranslator;

/**
 * Created by MVP on 11/27/2017.
 */

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserTable {
    static final String _ID = "_id";
    static final String NAME = "name";
    static final String SOURCE_LANG = "sourceLanguage";
    static final String TARGET_LANG = "targetLanguage";
    static final String SPEECH_LANG = "speechLanguage";



    static final String TABLE_NAME = "user";
    static final String CREATE_DB_TABLE =
            "CREATE TABLE " + TABLE_NAME
                    + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "sourceLanguage TEXT NOT NULL, "
                    + "targetLanguage TEXT NOT NULL, "
                    + "speechLanguage TEXT NOT NULL);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DB_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UserTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
