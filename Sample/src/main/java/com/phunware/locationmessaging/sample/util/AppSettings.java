package com.phunware.locationmessaging.sample.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.models.Config;

public final class AppSettings {

    private static final String APP_SETTINGS_FILE = "appSettings";

    private final static String APP_CONFIG_TITLE = "Title";
    private final static String APP_CONFIG_APPID = "AppId";
    private final static String APP_CONFIG_ENV = "Environment";
    private final static String APP_CONFIG_SELECTED_CONFIG = "SelectedConfig";

    public static void saveConfig(Context context, final Config config) {
        final SharedPreferences prefs = context.getSharedPreferences(APP_SETTINGS_FILE, 0);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(APP_CONFIG_TITLE, config.getTitle());
        editor.putLong(APP_CONFIG_APPID, config.getAppId());
        editor.putInt(APP_CONFIG_ENV, config.getEnvironment().ordinal());
        editor.commit();
    }

    public static void saveConfigPosition(Context context, final int position) {
        final SharedPreferences prefs = context.getSharedPreferences(APP_SETTINGS_FILE, 0);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(APP_CONFIG_SELECTED_CONFIG, position);
        editor.commit();
    }

    public static Config readConfig(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(APP_SETTINGS_FILE, 0);

        String title = prefs.getString(APP_CONFIG_TITLE, "");
        long appId = prefs.getLong(APP_CONFIG_APPID, -1L);
        int envValue = prefs.getInt(APP_CONFIG_ENV, 0);
        LocationMessaging.Environment env = LocationMessaging.Environment.values()[envValue];

        Config config;

        if (title.isEmpty() || appId == -1L) {
            config = getDefaultConfigStage(context);
            saveConfig(context, config);
            saveConfigPosition(context, 0);
        }
        else {
            config = new Config(title, appId, env);
        }

        return config;
    }

    public static int readConfigPosition(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(APP_SETTINGS_FILE, 0);
        return prefs.getInt(APP_CONFIG_SELECTED_CONFIG, 0);
    }

    public static Config getDefaultConfigStage(Context context) {
        return new Config("Test Messaging Prod",
                Long.parseLong(context.getString(R.string.app_id)),
                LocationMessaging.Environment.PROD);
    }

    private AppSettings() { }
}

