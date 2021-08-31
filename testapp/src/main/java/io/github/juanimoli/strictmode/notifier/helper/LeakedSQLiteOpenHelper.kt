package io.github.juanimoli.strictmode.notifier.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LeakedSQLiteOpenHelper(context: Context?) : SQLiteOpenHelper(context, DB, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    companion object {
        private const val DB = "sqlite_sample.db"
        private const val DB_VERSION = 1
        private const val CREATE_TABLE =
            "CREATE TABLE sample (_id INTEGER PRIMARY KEY AUTOINCREMENT, data INTEGER NOT NULL );"
        private const val DROP_TABLE = "DROP TABLE sample;"
    }
}
