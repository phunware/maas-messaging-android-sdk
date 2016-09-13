package com.phunware.locationmessaging.sample.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.phunware.locationmessaging.sample.models.Config;
import com.phunware.locationmessaging.sample.util.AppSettings;

import java.util.ArrayList;
import java.util.List;

public final class ConfigStore extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "config.db";
    private static final int DATABASE_VERSION = 1;
    private Config defaultConfig;

    public ConfigStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        defaultConfig = AppSettings.getDefaultConfigStage(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConfigContract.SQL_CREATE_TABLE);
        // ensure that the db has at least one configuration
        ContentValues values = ConfigContract.toContentValues(defaultConfig);
        db.insert(ConfigContract.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ConfigStore.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + ConfigContract.TABLE_NAME);
        onCreate(db);
    }

    public List<Config> getConfigurations() {
        List<Config> list = new ArrayList<>();

        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();

            Cursor cursor = db.query(ConfigContract.TABLE_NAME,
                    ConfigContract.ALL_COLUMNS, null, null, null, null, null
            );

            while (cursor.moveToNext()) {
                Config config = ConfigContract.fromCursor(cursor);
                list.add(config);
            }

            cursor.close();
        }
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return list;
    }

    public void addConfig(final Config config) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            ContentValues values = ConfigContract.toContentValues(config);

            db.insert(ConfigContract.TABLE_NAME,
                    null, values);

        }
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}
