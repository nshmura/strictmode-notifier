package com.juanimoli.strictmodenotifier.testapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LeakedSQLiteOpenHelper extends SQLiteOpenHelper {

    static final String DB = "sqlite_sample.db";
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE =
            "create table sample ( _id integer primary key autoincrement, data integer not null );";
    static final String DROP_TABLE = "drop table sample;";

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