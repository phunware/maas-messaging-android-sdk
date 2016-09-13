package com.phunware.locationmessaging.sample.models;

public class LogMessage {

    public enum Level {
        VERBOSE("V"),
        INFO("I"),
        DEBUG("D"),
        WARNING("W"),
        ERROR("E"),
        WTF("WTF");

        private final String mPrefix;

        Level(String prefix) {
            mPrefix = prefix;
        }

        public String getPrefix() {
            return mPrefix;
        }
    }

    private long mTimestamp;
    private String mTag;
    private Level mLevel;
    private String mMessage;

    public LogMessage(long timestamp, String tag, Level level, String message) {
        this.mTimestamp = timestamp;
        this.mTag = tag;
        this.mLevel = level;
        this.mMessage = message;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public Level getLevel() {
        return mLevel;
    }

    public void setLevel(Level level) {
        this.mLevel = level;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

}
