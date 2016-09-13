package com.phunware.locationmessaging.sample.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phunware.locationmessaging.entities.Message;
import com.phunware.locationmessaging.sample.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public interface OnMessageSelectedListener {
        void onMessageSelected(Message message);
    }

    private final List<Message> messages = new ArrayList<>();
    private final OnMessageSelectedListener listener;
    private final Date now = new Date();

    public MessageAdapter(List<Message> messages, OnMessageSelectedListener listener) {
        this.messages.addAll(messages);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_two_line_badged, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Message message = messages.get(position);

        holder.badge1.setText(R.string.unread);

        String title = message.notificationTitle();
        String msg = message.notificationMessage();

        holder.title.setText(title);
        holder.subtitle.setText(msg);

        final Context context = holder.title.getContext();

        int unreadColor;
        if (!message.isRead()) {
            unreadColor = ContextCompat.getColor(context, R.color.zone_inside_stroke);
        } else {
            unreadColor = ContextCompat.getColor(context, R.color.text_disabled);
        }
        holder.badge1.setTextColor(unreadColor);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void onMessageClicked(int position) {
        if (listener != null) {
            listener.onMessageSelected(messages.get(position));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView badge1;
        protected TextView title;
        protected TextView subtitle;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.title = (TextView) view.findViewById(R.id.title);
            this.subtitle = (TextView) view.findViewById(R.id.subtitle);
            this.badge1 = (TextView) view.findViewById(R.id.badge_one);
        }

        @Override
        public void onClick(View v) {
            onMessageClicked(getAdapterPosition());
        }
    }

}
