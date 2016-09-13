package com.phunware.locationmessaging.sample.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phunware.locationmessaging.entities.Message;
import com.phunware.locationmessaging.entities.MessageMetadata;
import com.phunware.locationmessaging.sample.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.ViewHolder> {

    private static final int CAMPAIGN_ID = 0;
    private static final int CAMPAIGN_TYPE = 1;
    private static final int NOTIFICATION_TITLE = 2;
    private static final int NOTIFICATION_MESSAGE = 3;
    private static final int HAS_PROMOTION = 4;
    private static final int IS_READ = 5;
    private static final int HAS_METADATA = 6;
    private static final int ITEM_COUNT = 7;

    public interface OnItemClickListener {

        void onPromotionClicked(Message message);

        void onMetadataClicked(Message message);

        void onReadClicked(Message message);

    }

    private final Message mMessage;
    private final OnItemClickListener mListener;

    private final View.OnClickListener mPromotionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onPromotionClicked(mMessage);
            }
        }
    };

    private final View.OnClickListener mMetadataClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onMetadataClicked(mMessage);
            }
        }
    };

    private final View.OnClickListener mIsReadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onReadClicked(mMessage);
            }
        }
    };

    public MessageDetailAdapter(Message message, OnItemClickListener listener) {
        mMessage = message;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_two_line, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(null);

        switch (position) {
            case CAMPAIGN_ID:
                holder.title.setText(R.string.item_campaign_id);
                if (mMessage != null) {
                    holder.subtitle.setText(String.valueOf(mMessage.campaignId()));
                }
                break;
            case CAMPAIGN_TYPE:
                holder.title.setText(R.string.item_campaign_type);
                if (mMessage != null) {
                    holder.subtitle.setText(mMessage.campaignType());
                }
                break;
            case NOTIFICATION_TITLE:
                holder.title.setText(R.string.item_notification_title);
                if (mMessage != null) {
                    holder.subtitle.setText(mMessage.notificationTitle());
                }
                break;
            case NOTIFICATION_MESSAGE:
                holder.title.setText(R.string.item_notification_message);
                if (mMessage != null) {
                    holder.subtitle.setText(mMessage.notificationMessage());
                }
                break;
            case HAS_PROMOTION:
                holder.title.setText(R.string.item_has_promotion);
                if (mMessage != null) {
                    if (mMessage.promotionTitle() != null
                            && !mMessage.promotionTitle().isEmpty()) {
                        holder.subtitle.setText(R.string.yes);

                        holder.itemView.setOnClickListener(mPromotionClickListener);
                    } else {
                        holder.subtitle.setText(R.string.no);
                    }
                }
                break;
            case IS_READ:
                holder.title.setText(R.string.item_is_read);
                if (mMessage != null) {
                    if (mMessage.isRead()) {
                        holder.subtitle.setText(R.string.yes);
                    } else {
                        holder.subtitle.setText(R.string.no);
                        holder.itemView.setOnClickListener(mIsReadClickListener);
                    }
                }
                break;
            case HAS_METADATA:
                holder.title.setText(R.string.item_has_metadata);
                if (mMessage != null) {
                    List<MessageMetadata> metadata = mMessage.metadata();
                    if (metadata != null && !metadata.isEmpty()) {
                        holder.subtitle.setText(R.string.yes);

                        holder.itemView.setOnClickListener(mMetadataClickListener);
                    } else {
                        holder.subtitle.setText(R.string.no);
                    }
                }
                break;
            default:
                // no op
        }
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}
