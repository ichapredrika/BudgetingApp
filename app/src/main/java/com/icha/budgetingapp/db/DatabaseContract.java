package com.icha.budgetingapp.db;

import android.provider.BaseColumns;

public class DatabaseContract {

    static String TABLE_NAME = "trans";

    public static final class TransColumns implements BaseColumns {
        public static String TYPE = "title";
        public static String AMOUNT = "amount";
        public static String DESCRIPTION = "description";
        public static String DATE = "date";
    }
}
