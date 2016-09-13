package com.phunware.locationmessaging.sample.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.sample.BuildConfig;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.models.Config;

import java.util.List;

public class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.ViewHolder> {

    private static final int ITEM_TYPE_STAT = 0;
    private static final int ITEM_TYPE_COUNT = 5;

    private static final int[] ITEM_TYPES = {
            ITEM_TYPE_STAT, // App Name(no subtitle), id, Device Id, SDK Version, Sample App Version
    };

    private Config mSelectedConfig;
    private List<Config> mAvailableConfigs;
    private int mCurrentPosition = 0;

    private AdapterView.OnItemSelectedListener mSpinnerListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrentPosition = position;
            setConfig(mAvailableConfigs.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public ConfigAdapter(Config mSelectedConfig,
            List<Config> mAvailableConfigs,
            int position) {
        this.mSelectedConfig = mSelectedConfig;
        this.mAvailableConfigs = mAvailableConfigs;
        this.mCurrentPosition = position;
    }

    public void setConfig(Config config) {
        mSelectedConfig = config;
        notifyItemRangeChanged(1, 3);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_TYPE_STAT:
                v = inflater.inflate(R.layout.row_two_line, parent, false);
                break;
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (position) {
            case 0: // App Name
                holder.title.setText(mAvailableConfigs.get(0).getTitle());
                break;
            case 1: // App Id
                holder.title.setText(R.string.title_app_id);
                holder.subtitle.setText(String.valueOf(mSelectedConfig.getAppId()));
                break;
            case 2: // Device Id
                holder.title.setText(R.string.title_device_id);
                holder.subtitle.setText(LocationMessaging.deviceId());
                break;
            case 3: // SDK Version
                holder.title.setText(R.string.title_sdk_version);
                holder.subtitle.setText(R.string.locationmessaging_sdk_version);
                break;
            case 4: // Sample App Version
                holder.title.setText(R.string.title_app_version);
                holder.subtitle.setText(BuildConfig.VERSION_NAME);
                break;
            default:
                // who cares
        }
    }

    @Override
    public int getItemCount() {
        return ITEM_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        //all items are of the same type right now
        return ITEM_TYPE_STAT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}
