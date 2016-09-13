package com.phunware.locationmessaging.sample.adapters;

import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phunware.locationmessaging.sample.models.LogMessage;
import com.phunware.locationmessaging.sample.sql.LogMessageContract;

/**
 * Adapter to display log messages.
 */
public class LogMessageAdapter extends RecyclerView.Adapter<LogMessageAdapter.ViewHolder> {

    private static final String MESSAGE_FORMAT = "%s/%s: %s"; // W/TAG: message

    private Cursor mCursor;

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = new TextView(parent.getContext());
        v.setTypeface(Typeface.MONOSPACE);
        v.setHorizontallyScrolling(true);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final LogMessage message = LogMessageContract.fromCursor(mCursor);
        holder.message.setTextColor(getTextColor(message.getLevel()));
        holder.message.setText(String.format(MESSAGE_FORMAT,
                        message.getLevel().getPrefix(),
                        message.getTag(),
                        message.getMessage()));
    }

    private int getTextColor(LogMessage.Level level) {
        switch (level) {
            case ERROR:
            case WTF:
                return 0xffff0000;
            case WARNING:
                return 0xff006699;
            default:
                return 0xff000000;
        }
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView message;

        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView;
        }
    }
}
