package com.icha.budgetingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.icha.budgetingapp.db.DatabaseContract.TABLE_NAME;

public class TransHelper {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper dataBaseHelper;
    private static TransHelper INSTANCE;
    private static SQLiteDatabase database;

    private TransHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static TransHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TransHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen()) database.close();

    }

    public Cursor queryAll() {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                _ID + " DESC");
    }

    public Cursor queryById(String id) {
        return database.query(
                DATABASE_TABLE,
                null,
                _ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null);
    }

    public Cursor getDate() {
        String Query = "Select substr (" + DatabaseContract.TransColumns.DATE + ",1,7) " + "  as group_date FROM " + DATABASE_TABLE + " GROUP BY substr (" + DatabaseContract.TransColumns.DATE + ",1,7)" + " ORDER BY " + DatabaseContract.TransColumns.DATE + " DESC";
        Cursor cursor = database.rawQuery(Query, null);
        return cursor;

    }

    public Cursor queryByDate(String substring) {
        String Query = "Select * FROM " + DATABASE_TABLE + " where " + DatabaseContract.TransColumns.DATE + " LIKE '" + substring + "%'";
        Cursor cursor = database.rawQuery(Query, null);
        return cursor;
    }

    public double sumIncome(String substring) {
        String Query = "Select SUM(" + DatabaseContract.TransColumns.AMOUNT + ") as Total FROM " + DATABASE_TABLE + " where " + DatabaseContract.TransColumns.TYPE + " = " + "\"" + "income" + "\"" + " AND " + DatabaseContract.TransColumns.DATE + " LIKE '" + substring + "%'";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            double income = cursor.getInt(cursor.getColumnIndex("Total"));
            cursor.close();
            return income;
        } else {
            cursor.close();
            return 0;
        }
    }

    public double sumExpense(String substring) {
        String Query = "Select SUM(" + DatabaseContract.TransColumns.AMOUNT + ") as Total FROM " + DATABASE_TABLE + " where " + DatabaseContract.TransColumns.TYPE + " = " + "\"" + "expense" + "\"" + " AND " + DatabaseContract.TransColumns.DATE + " LIKE '" + substring + "%'";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            double expense = cursor.getInt(cursor.getColumnIndex("Total"));
            cursor.close();
            return expense;
        } else {
            cursor.close();
            return 0;
        }
    }

    public double sumTotalIncome() {
        String Query = "Select SUM(" + DatabaseContract.TransColumns.AMOUNT + ") as Total FROM " + DATABASE_TABLE + " where " + DatabaseContract.TransColumns.TYPE + " = " + "\"" + "income" + "\"";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            double income = cursor.getInt(cursor.getColumnIndex("Total"));
            cursor.close();
            return income;
        } else {
            cursor.close();
            return 0;
        }
    }

    public double sumTotalExpense() {
        String Query = "Select SUM(" + DatabaseContract.TransColumns.AMOUNT + ") as Total FROM " + DATABASE_TABLE + " where " + DatabaseContract.TransColumns.TYPE + " = " + "\"" + "expense" + "\"";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            double expense = cursor.getInt(cursor.getColumnIndex("Total"));
            cursor.close();
            return expense;
        } else {
            cursor.close();
            return 0;
        }
    }

    public long insert(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int update(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, _ID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        return database.delete(DATABASE_TABLE, _ID + " = ?", new String[]{id});
    }
}
