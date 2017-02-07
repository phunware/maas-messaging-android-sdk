package com.phunware.locationmessaging.sample.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.sample.models.Config;


final public class ConfigContract {

    private ConfigContract() { }

    /**
     * Contains the name of the table to create
     */
    public static final String TABLE_NAME = "config_table";

    /**
     * Contains the SQL query to use to create the table
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + " ("
            + ConfigEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ConfigEntry.COLUMN_TITLE + " TEXT,"
            + ConfigEntry.COLUMN_APP_ID + " TEXT)";
    /**
     * Contains the SQL query to use to drop the table
     */
    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    /**
     * This class represents the rows for an entry in the config table. The
     * primary key is the _id column from the BaseColumn class.
     */
    public static abstract class ConfigEntry implements BaseColumns {

        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_APP_ID = "AppId";
    }

    static final String[] ALL_COLUMNS = {ConfigEntry._ID,
            ConfigEntry.COLUMN_TITLE,
            ConfigEntry.COLUMN_APP_ID,
    };

    public static ContentValues toContentValues(final Config config) {
        ContentValues values = new ContentValues();
        values.put(ConfigEntry.COLUMN_TITLE, config.getTitle());
        values.put(ConfigEntry.COLUMN_APP_ID, config.getAppId());
        return values;
    }

    public static Config fromCursor(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(ConfigEntry.COLUMN_TITLE));
        Long appId = cursor.getLong(cursor.getColumnIndex(ConfigEntry.COLUMN_APP_ID));

        return new Config(title, appId);
    }

    public static boolean keyExists(SQLiteDatabase db, final String key) {
        String[] args = new String[] {key};
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.query(TABLE_NAME, ConfigContract.ALL_COLUMNS,
                    ConfigEntry.COLUMN_TITLE + "=?", args, null, null, null
            );
            exists = cursor.getCount() > 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return exists;
    }
}
