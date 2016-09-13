package com.phunware.locationmessaging.sample.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.phunware.locationmessaging.sample.BuildConfig;

public final class LogMessageProvider extends ContentProvider {

    private static final int LOG_MESSAGES = 0;
    private static final int LOG_MESSAGE = 1;
    private static final int LOG_SEARCH = 2;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, "/logs", LOG_MESSAGES);
        URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, "/logs/#", LOG_MESSAGE);
        URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, "/logs/*", LOG_SEARCH);
    }

    private LogMessageStore mStore;

    @Override
    public boolean onCreate() {
        mStore = new LogMessageStore(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mStore.getReadableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case LOG_MESSAGES:
                return db.query(LogMessageContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
            case LOG_MESSAGE:
                String id = uri.getLastPathSegment();
                return db.query(LogMessageContract.TABLE_NAME, projection
                        , LogMessageContract.LogMessageEntry._ID + "=?", new String[] {id}
                        , null, null, sortOrder);
            case LOG_SEARCH:
                String query = "%" + uri.getLastPathSegment() + "%";
                SelectionBuilder builder = new SelectionBuilder();
                builder.table(LogMessageContract.TABLE_NAME);
                builder.where(LogMessageContract.LogMessageEntry.COLUMN_MESSAGE + " LIKE ? OR "
                                + LogMessageContract.LogMessageEntry.COLUMN_TAG + " LIKE ?",
                        query, query);
                return builder.query(db, projection, sortOrder);
            default:
                throw new RuntimeException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case LOG_MESSAGES:
            case LOG_SEARCH:
                return "vnd.android.cursor.dir/vnd.sample.log";
            case LOG_MESSAGE:
                return "vnd.android.cursor.item/vnd.sample.log";
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id = -1;
        SQLiteDatabase db = mStore.getWritableDatabase();
        db.beginTransaction();

        id = db.insert(LogMessageContract.TABLE_NAME, null, values);

        db.setTransactionSuccessful();
        db.endTransaction();

        getContext().getContentResolver().notifyChange(LogMessageContract.CONTENT_URI, null);

        return uri.buildUpon().appendPath(String.valueOf(id)).build();
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mStore.getWritableDatabase();
        db.beginTransaction();

        int count = db.delete(LogMessageContract.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(LogMessageContract.CONTENT_URI, null);
        db.setTransactionSuccessful();
        db.endTransaction();

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mStore.getWritableDatabase();
        db.beginTransaction();

        int count = db.update(LogMessageContract.TABLE_NAME, values, selection, selectionArgs);
        db.setTransactionSuccessful();
        db.endTransaction();

        getContext().getContentResolver().notifyChange(LogMessageContract.CONTENT_URI, null);

        return count;
    }
}
