package com.phunware.locationmessaging.sample.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.phunware.locationmessaging.Callback;
import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.entities.Geozone;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.views.GeozoneMapView;

import java.util.List;

public class GeozoneDetailFragment extends Fragment {

    private static final String TAG = GeozoneDetailFragment.class.getSimpleName();

    private static final String EXTRA_ID = "id";

    private TextView mTitle;
    private TextView mId;
    private TextView mTags;
    private GeozoneMapView mMap;

    private Geozone mGeozone;

    public static GeozoneDetailFragment newInstance(Geozone zone) {
        GeozoneDetailFragment f = new GeozoneDetailFragment();

        if (zone != null) {
            Bundle args = new Bundle();
            args.putLong(EXTRA_ID, zone.id());
            f.setArguments(args);
        }

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_geozone_detail, container, false);
        mTitle = (TextView) v.findViewById(R.id.title);
        mId = (TextView) v.findViewById(R.id.id);
        mTags = (TextView) v.findViewById(R.id.tags);
        mMap = (GeozoneMapView) v.findViewById(R.id.map);

        final View tags = v.findViewById(R.id.item_tags);
        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTagsClicked();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // disable map gestures
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setAllGesturesEnabled(false);
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            final long id = args.getLong(EXTRA_ID);
            LocationMessaging.locationManager().getGeozone(id, new Callback<Geozone>() {
                @Override
                public void onSuccess(Geozone data) {
                    setGeozone(data);
                }

                @Override
                public void onFailed(Throwable e) {
                    Log.e(TAG, "Failed to get geozone for id: " + id);
                    getActivity().getSupportFragmentManager()
                            .popBackStackImmediate();
                }
            });
        }

        mMap.onCreate(savedInstanceState);
    }

    private void setGeozone(Geozone geozone) {
        mGeozone = geozone;

        mTitle.setText(geozone.name());
        mId.setText(geozone.locationCode());

        int tagCount = 0;
        List<String> tags = geozone.tags();
        if (tags != null) {
            tagCount = tags.size();
        }
        mTags.setText(getResources().getQuantityString(R.plurals.tags, tagCount, tagCount));

        mMap.addGeozone(geozone);
        mMap.centerCamera();
    }

    private void onTagsClicked() {
        List<String> tags = mGeozone.tags();
        if (tags == null || tags.isEmpty()) {
            return;
        }
        CharSequence[] items = tags.toArray(new CharSequence[tags.size()]);
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_tags)
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // no op
                    }
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
        mMap.centerCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }
}
