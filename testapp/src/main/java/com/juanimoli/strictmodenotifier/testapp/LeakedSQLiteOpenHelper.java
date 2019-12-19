package com.juanimoli.strictmodenotifier.testapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LeakedSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB = "sqlite_sample.db";
    private static final int DB_VERSION = 1;
    private static final String CREATE_TABLE =
            "create table sample ( _id integer primary key autoincrement, data integer not null );";
    private static final String DROP_TABLE = "drop table sample;";

    public LeakedSQLiteOpenHelper(Context context) {
        super(context, DB, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}