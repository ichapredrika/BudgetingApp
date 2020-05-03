package com.icha.budgetingapp.helper;

import android.database.Cursor;

import com.icha.budgetingapp.db.DatabaseContract;
import com.icha.budgetingapp.entity.Trans;

import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<Trans> mapCursorToArrayList(Cursor transCursor){
        ArrayList<Trans> transList = new ArrayList<>();

        while(transCursor.moveToNext()){
            int id = transCursor.getInt(transCursor.getColumnIndexOrThrow(DatabaseContract.TransColumns._ID));
            double amount = transCursor.getDouble(transCursor.getColumnIndexOrThrow(DatabaseContract.TransColumns.AMOUNT));
            String type = transCursor.getString(transCursor.getColumnIndexOrThrow(DatabaseContract.TransColumns.TYPE));
            String date = transCursor.getString(transCursor.getColumnIndexOrThrow(DatabaseContract.TransColumns.DATE));
            String description = transCursor.getString(transCursor.getColumnIndexOrThrow(DatabaseContract.TransColumns.DESCRIPTION));
            transList.add(new Trans(id, type, amount, description, date ));
        }
        return transList;
    }

    public static ArrayList<String> mapDateToArrayList(Cursor transCursor){
        ArrayList<String> dateList = new ArrayList<>();

        while(transCursor.moveToNext()){
            String date = transCursor.getString(transCursor.getColumnIndexOrThrow("group_date"));
            dateList.add(date);
        }
        return dateList;
    }
}
