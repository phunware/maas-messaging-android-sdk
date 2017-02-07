package com.phunware.locationmessaging.sample.models;

import android.content.Context;

import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.sample.R;

public class Config {
    private String mTitle;
    private long mAppId;

    public Config(String mTitle, long mAppId) {
        this.mTitle = mTitle;
        this.mAppId = mAppId;
    }

    public Config(final Context context, String mTitle, long mAppId) {
        this.mTitle = mTitle;
        this.mAppId = mAppId;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getAppId() {
        return mAppId;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
