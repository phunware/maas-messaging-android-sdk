package com.phunware.locationmessaging.sample.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogMessageStore extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "log.db";
    private static final int DATABASE_VERSION = 1;

    public LogMessageStore(Context context) {
        super(context, DATABASE_NAME, null /* CursorFactory */, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LogMessageContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LogMessageContract.SQL_DROP_TABLES);
        onCreate(db);
    }
}
