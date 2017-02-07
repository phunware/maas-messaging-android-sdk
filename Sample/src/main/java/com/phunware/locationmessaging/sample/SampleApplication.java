package com.phunware.locationmessaging.sample;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.phunware.core.PwCoreSession;
import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.location.LocationManager;
import com.phunware.locationmessaging.log.FileLogger;
import com.phunware.locationmessaging.log.LogLogger;
import com.phunware.locationmessaging.sample.loggers.ContentProviderLogger;
import com.phunware.locationmessaging.sample.models.Config;
import com.phunware.locationmessaging.sample.util.AppSettings;
import io.fabric.sdk.android.Fabric;

public class SampleApplication extends Application {

    public FileLogger mFileLogger;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        PwCoreSession.getInstance().setEnvironment(PwCoreSession.Environment.PROD);
        PwCoreSession.getInstance().registerKeys(this, getString(R.string.app_id),
                getString(R.string.access_key), getString(R.string.signature_key), "");

        mFileLogger = new FileLogger(this, Long.parseLong(getString(R.string.app_id)));

        new LocationMessaging.Builder(this)
                .appId(Long.parseLong(getString(R.string.app_id)))
                .environment(PwCoreSession.getInstance().getEnvironment())
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
