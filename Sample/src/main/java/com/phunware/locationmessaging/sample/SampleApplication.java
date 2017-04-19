package com.phunware.locationmessaging.sample;

import android.app.Application;

import com.phunware.core.PwCoreSession;
import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.location.LocationManager;
import com.phunware.locationmessaging.log.FileLogger;
import com.phunware.locationmessaging.log.LogLogger;
import com.phunware.locationmessaging.sample.loggers.ContentProviderLogger;


public class SampleApplication extends Application {

    public FileLogger mFileLogger;

    @Override
    public void onCreate() {
        super.onCreate();

        PwCoreSession.getInstance().registerKeys(this, getString(R.string.app_id),
                getString(R.string.access_key), getString(R.string.signature_key), "");

        mFileLogger = new FileLogger(this, Long.parseLong(getString(R.string.app_id)));

        new LocationMessaging.Builder(this)
                .appId(Long.parseLong(getString(R.string.app_id)))
                .addLogger(new ContentProviderLogger(this))
                .addLogger(new LogLogger())
                .addLogger(mFileLogger)
                .build();
        LocationMessaging.locationManager().start();
    }

    public LocationManager getLocationManager() { return LocationMessaging.locationManager(); }


    public FileLogger getFileLogger() {
        return mFileLogger;
    }

}
