package com.phunware.locationmessaging.sample;

import android.app.Application;

import com.phunware.core.PwCoreSession;
import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.location.LocationManager;
import com.phunware.locationmessaging.log.FileLogger;
import com.phunware.locationmessaging.log.LogLogger;
import com.phunware.locationmessaging.sample.loggers.ContentProviderLogger;
import com.phunware.locationmessaging.sample.models.Config;
import com.phunware.locationmessaging.sample.util.AppSettings;

public class SampleApplication extends Application {

    public FileLogger mFileLogger;

    @Override
    public void onCreate() {
        super.onCreate();

        PwCoreSession.getInstance().registerKeys(this);

        mFileLogger = new FileLogger(this, Long.parseLong(getString(R.string.app_id)));

        Config config = AppSettings.readConfig(this);
        new LocationMessaging.Builder(this)
                .appId(config.getAppId())
                .environment(config.getEnvironment())
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
