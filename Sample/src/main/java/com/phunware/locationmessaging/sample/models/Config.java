package com.phunware.locationmessaging.sample.models;

import android.content.Context;

import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.sample.R;

public class Config {
    private String mTitle;
    private long mAppId;
    private LocationMessaging.Environment mEnvironment;

    public Config(String mTitle, long mAppId,
            LocationMessaging.Environment mEnvironment) {
        this.mTitle = mTitle;
        this.mAppId = mAppId;
        this.mEnvironment = mEnvironment;
    }

    public Config(final Context context, String mTitle, long mAppId,
            String environment) {
        this.mTitle = mTitle;
        this.mAppId = mAppId;
        this.mEnvironment = fromEnvironmentString(context, environment);
    }

    public String getTitle() {
        return mTitle;
    }

    public long getAppId() {
        return mAppId;
    }

    public LocationMessaging.Environment getEnvironment() {
        return mEnvironment;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public LocationMessaging.Environment fromEnvironmentString(final Context context,
            final String env) {
        LocationMessaging.Environment environment = LocationMessaging
                .Environment.DEV;
        if (env.equalsIgnoreCase(context.getString(R.string.value_dev))) {
            environment = LocationMessaging.Environment.DEV;
        }
        else if (env.equalsIgnoreCase(context.getString(R.string.value_stage))) {
            environment = LocationMessaging.Environment.STAGE;
        }
        else if (env.equalsIgnoreCase(context.getString(R.string.value_sandbox))) {
            environment = LocationMessaging.Environment.SANDBOX;
        }
        else if (env.equalsIgnoreCase(context.getString(R.string.value_prod))) {
            environment = LocationMessaging.Environment.PROD;
        }
        else if (env.equalsIgnoreCase(context.getString(R.string.value_test))) {
            environment = LocationMessaging.Environment.TEST;
        }
        return environment;
    }

    public String getEnvironmentString(final Context context) {
        String envString;
        switch (mEnvironment) {
            case DEV:
                envString = context.getString(R.string.value_dev);
                break;
            case STAGE:
                envString = context.getString(R.string.value_stage);
                break;
            case SANDBOX:
                envString = context.getString(R.string.value_sandbox);
                break;
            case PROD:
                envString = context.getString(R.string.value_prod);
                break;
            case TEST:
                envString = context.getString(R.string.value_test);
                break;
            default:
                envString = context.getString(R.string.value_dev);
        }
        return envString;
    }

    static public String[] getLocalizedEnvironmentStrings(final Context context) {
        return new String[] {context.getString(R.string.value_dev),
                context.getString(R.string.value_stage),
                context.getString(R.string.value_sandbox),
                context.getString(R.string.value_prod),
                context.getString(R.string.value_test)};
    }

    static public LocationMessaging.Environment environmentFromInt(final int envValue) {
        return LocationMessaging.Environment.values()[envValue];
    }

    static public int environmentToInt(final LocationMessaging.Environment env) {
        return env.ordinal();
    }
}
