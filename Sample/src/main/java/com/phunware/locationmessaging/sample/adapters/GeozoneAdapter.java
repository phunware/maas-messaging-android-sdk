package com.phunware.locationmessaging.sample.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phunware.locationmessaging.entities.Geozone;
import com.phunware.locationmessaging.sample.R;

import java.util.ArrayList;
import java.util.List;

public class GeozoneAdapter extends RecyclerView.Adapter<GeozoneAdapter.ViewHolder> {

    private List<Geozone> mGeozones = new ArrayList<>();
    private final OnGeozoneSelectedListener mListener;

    public interface OnGeozoneSelectedListener {
        void onGeozoneSelected(Geozone zone);
    }

    public GeozoneAdapter(List<Geozone> geozones, OnGeozoneSelectedListener listener) {
        mListener = listener;
        mGeozones.addAll(geozones);
        setHasStableIds(true);
    }

    public void setGeozones(List<Geozone> geozones) {
        mGeozones = geozones;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_two_line_badged, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Geozone geozone = mGeozones.get(position);

        holder.geozone = geozone;
        holder.title.setText(geozone.name());
        holder.isInside.setText(R.string.inside);
        holder.isMonitored.setText(R.string.monitored);

        final Context context = holder.title.getContext();

        int insideColor;
        if (geozone.isInside()) {
            insideColor = ContextCompat.getColor(context, R.color.zone_inside_stroke);
        } else {
            insideColor = ContextCompat.getColor(context, R.color.text_disabled);
        }
        holder.isInside.setTextColor(insideColor);

        int monitoredColor;
        if (geozone.isMonitored()) {
            monitoredColor = ContextCompat.getColor(context, R.color.zone_inside_stroke);
        } else {
            monitoredColor = ContextCompat.getColor(context, R.color.text_disabled);
        }
        holder.isMonitored.setTextColor(monitoredColor);
    }

    @Override
    public int getItemCount() {
        return mGeozones.size();
    }

    @Override
    public long getItemId(int position) {
        return mGeozones.get(position).id();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final OnGeozoneSelectedListener listener;
        final TextView title;
        final TextView isMonitored;
        final TextView isInside;

        Geozone geozone;

        public ViewHolder(View view, OnGeozoneSelectedListener listener) {
            super(view);
            this.listener = listener;
            this.title = (TextView) view.findViewById(R.id.title);
            this.isMonitored = (TextView) view.findViewById(R.id.badge_two);
            this.isInside = (TextView) view.findViewById(R.id.badge_one);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null && geozone != null) {
                listener.onGeozoneSelected(geozone);
            }
        }
    }

}
