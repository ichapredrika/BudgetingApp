package com.icha.budgetingapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "dbtrans";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_TRANS = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s REAL NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TIME NOT NULL)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.TransColumns._ID,
            DatabaseContract.TransColumns.TYPE,
            DatabaseContract.TransColumns.AMOUNT,
            DatabaseContract.TransColumns.DESCRIPTION,
            DatabaseContract.TransColumns.DATE
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TRANS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}
