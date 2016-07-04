package com.example.simpletodo;

import android.provider.BaseColumns;

import java.lang.String;

public class FeedReaderContract {
    public FeedReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ITEM = "item";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_DUEDATE = "dueDate";
        public static final String COLUMN_NAME_NULLABLE = null;
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_ITEM + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_PRIORITY + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DUEDATE + TEXT_TYPE +
                    " )";

    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}

