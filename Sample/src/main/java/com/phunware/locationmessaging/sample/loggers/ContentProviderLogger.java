package com.phunware.locationmessaging.sample.loggers;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.phunware.locationmessaging.log.Logger;
import com.phunware.locationmessaging.sample.models.LogMessage;
import com.phunware.locationmessaging.sample.sql.LogMessageContract;

/**
 * A simple logger implementation that sends all log messages to a content provider.
 */
public class ContentProviderLogger implements Logger {

    private final ContentResolver mContentResolver;

    public ContentProviderLogger(Context context) {
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void v(String tag, String message, Throwable t) {
        LogMessage msg = new LogMessage(System.currentTimeMillis(), tag,
                LogMessage.Level.VERBOSE, createMessage(message, t));
        mContentResolver.insert(LogMessageContract.CONTENT_URI,
                LogMessageContract.toContentValues(msg)
        );
    }

    @Override
    public void i(String tag, String message, Throwable t) {
        LogMessage msg = new LogMessage(System.currentTimeMillis(), tag,
                LogMessage.Level.INFO, createMessage(message, t));
        mContentResolver.insert(LogMessageContract.CONTENT_URI,
                LogMessageContract.toContentValues(msg)
        );
    }

    @Override
    public void d(String tag, String message, Throwable t) {
        LogMessage msg = new LogMessage(System.currentTimeMillis(), tag,
                LogMessage.Level.DEBUG, createMessage(message, t));
        mContentResolver.insert(LogMessageContract.CONTENT_URI,
                LogMessageContract.toContentValues(msg)
        );
    }

    @Override
    public void w(String tag, String message, Throwable t) {
        LogMessage msg = new LogMessage(System.currentTimeMillis(), tag,
                LogMessage.Level.WARNING, createMessage(message, t));
        mContentResolver.insert(LogMessageContract.CONTENT_URI,
                LogMessageContract.toContentValues(msg)
        );
    }

    @Override
    public void e(String tag, String message, Throwable t) {
        LogMessage msg = new LogMessage(System.currentTimeMillis(), tag,
                LogMessage.Level.ERROR, createMessage(message, t));
        mContentResolver.insert(LogMessageContract.CONTENT_URI,
                LogMessageContract.toContentValues(msg)
        );
    }

    @Override
    public void wtf(String tag, String message, Throwable t) {
        LogMessage msg = new LogMessage(System.currentTimeMillis(), tag,
                LogMessage.Level.WTF, createMessage(message, t));
        mContentResolver.insert(LogMessageContract.CONTENT_URI,
                LogMessageContract.toContentValues(msg)
        );
    }

    private String createMessage(String message, Throwable t) {
        StringBuilder builder =  new StringBuilder(message);
        builder.append(" ");
        builder.append(Log.getStackTraceString(t));
        return builder.toString();
    }
}
