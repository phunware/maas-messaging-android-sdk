package com.phunware.locationmessaging.sample.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phunware.locationmessaging.entities.AttributeMetadataItem;
import com.phunware.locationmessaging.sample.R;

import java.util.List;

public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.ViewHolder> {

    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_ATTRIBUTE = 1;

    public static class Attribute {
        public static final int TYPE_PROFILE = 0;

        final int mType;
        final String mName;
        final AttributeMetadataItem mMetadata;
        final Object mValue;

        public Attribute(AttributeMetadataItem metadata, Object value) {
            mType = TYPE_PROFILE;
            mMetadata = metadata;
            mName = mMetadata.name();
            mValue = value;
        }

        public String getName() {
            return mName;
        }

        public AttributeMetadataItem getMetadata() {
            return mMetadata;
        }

        public Object getValue() {
            return mValue;
        }
    }

    public interface OnClickListener {
        void onClick(Attribute attribute);
    }

    private final List<Attribute> mProfileAttributes;
    private final OnClickListener mOnClickListener;

    public AttributeAdapter(List<Attribute> profileAttributes,
            OnClickListener onClickListener) {
        mProfileAttributes = profileAttributes;
        mOnClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                v = inflater.inflate(R.layout.row_header, parent, false);
                break;
            case ITEM_TYPE_ATTRIBUTE:
                v = inflater.inflate(R.layout.row_two_line, parent, false);
                break;
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.title.setText(R.string.title_profile_attributes);
            return;
        }

        Attribute attribute = mProfileAttributes.get(position - 1);

        holder.title.setText(attribute.getName());
        holder.subtitle.setText(attribute.getValue().toString());
        holder.itemView.setOnClickListener(new ItemClickListener(mOnClickListener, attribute));
    }

    @Override
    public int getItemCount() {
        return 1 /* headers */ + mProfileAttributes.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return ITEM_TYPE_HEADER;
            default:
                return ITEM_TYPE_ATTRIBUTE;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }

    private static class ItemClickListener implements View.OnClickListener {

        private final OnClickListener onClickListener;
        private final Attribute attribute;

        public ItemClickListener(OnClickListener onClickListener, Attribute attribute) {
            this.onClickListener = onClickListener;
            this.attribute = attribute;
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(attribute);
        }
    }
}
