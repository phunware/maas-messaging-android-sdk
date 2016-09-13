package com.phunware.locationmessaging.sample.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.phunware.locationmessaging.sample.BuildConfig;
import com.phunware.locationmessaging.sample.models.LogMessage;

public final class LogMessageContract {

    public static final Uri CONTENT_URI = Uri.parse("content://" + BuildConfig.APPLICATION_ID
                    + "/logs");

    public static final String TABLE_NAME = "LogMessages";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("
            + LogMessageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LogMessageEntry.COLUMN_TIMESTAMP + " INTEGER,"
            + LogMessageEntry.COLUMN_TAG + " TEXT,"
            + LogMessageEntry.COLUMN_LEVEL + " INTEGER,"
            + LogMessageEntry.COLUMN_MESSAGE + " TEXT);";

    public static final String SQL_DROP_TABLES = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public static final String DEFAULT_SORT = LogMessageEntry.COLUMN_TIMESTAMP + " desc";

    public interface LogMessageEntry extends BaseColumns {
        String COLUMN_TIMESTAMP = "timestamp";
        String COLUMN_TAG = "tag";
        String COLUMN_LEVEL = "level";
        String COLUMN_MESSAGE = "message";
    }

    public static ContentValues toContentValues(final LogMessage message) {
        ContentValues values = new ContentValues();
        values.put(LogMessageEntry.COLUMN_TIMESTAMP, message.getTimestamp());
        values.put(LogMessageEntry.COLUMN_TAG, message.getTag());
        values.put(LogMessageEntry.COLUMN_LEVEL, message.getLevel().ordinal());
        values.put(LogMessageEntry.COLUMN_MESSAGE, message.getMessage());
        return values;
    }

    public static LogMessage fromCursor(Cursor cursor) {
        long timestamp = cursor.getLong(cursor.getColumnIndex(LogMessageEntry.COLUMN_TIMESTAMP));
        String tag = cursor.getString(cursor.getColumnIndex(LogMessageEntry.COLUMN_TAG));
        int level = cursor.getInt(cursor.getColumnIndex(LogMessageEntry.COLUMN_LEVEL));
        String message = cursor.getString(cursor.getColumnIndex(LogMessageEntry.COLUMN_MESSAGE));
        return new LogMessage(timestamp, tag, LogMessage.Level.values()[level], message);
    }

    private LogMessageContract() { }
}
