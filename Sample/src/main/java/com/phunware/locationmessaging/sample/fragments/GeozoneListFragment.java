package com.phunware.locationmessaging.sample.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.phunware.locationmessaging.Callback;
import com.phunware.locationmessaging.entities.Geozone;
import com.phunware.locationmessaging.location.LocationListener;
import com.phunware.locationmessaging.location.LocationManager;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.SampleApplication;
import com.phunware.locationmessaging.sample.adapters.GeozoneAdapter;
import com.phunware.locationmessaging.sample.views.DividerItemDecoration;

import java.util.List;

/**
 *
 */
public class GeozoneListFragment extends Fragment
        implements GeozoneAdapter.OnGeozoneSelectedListener, LocationListener {

    private RecyclerView mList;
    private TextView mError;
    private ProgressBar mLoading;

    private GeozoneAdapter mAdapter;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_list, container, false);
        mList = (RecyclerView) v.findViewById(R.id.list);
        mError = (TextView) v.findViewById(R.id.error);
        mLoading = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        mList.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);

        locationManager = ((SampleApplication) getActivity().getApplication()).getLocationManager();
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.addItemDecoration(new DividerItemDecoration(getActivity()));

        refreshGeozones();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager.addLocationListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.addLocationListener(this);
    }

    private void showError(Throwable e) {
        mList.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        mError.setVisibility(View.VISIBLE);
        mError.setText(getString(R.string.location_load_failed, e.getMessage()));
    }

    public void setGeozones(List<Geozone> geozones) {
        if (geozones == null || geozones.isEmpty()) {
            showEmpty();
            return;
        }

        mAdapter = new GeozoneAdapter(geozones, this);
        mList.setAdapter(mAdapter);

        mList.setVisibility(View.VISIBLE);
        mError.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
    }

    private void showEmpty() {
        mLoading.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);
        mError.setText(R.string.no_locations);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.setTitle(R.string.nav_locations);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.map:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, new GeozoneMapFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onGeozoneSelected(Geozone zone) {
        GeozoneDetailFragment f = GeozoneDetailFragment.newInstance(zone);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, f)
                .addToBackStack(null)
                .commit();
    }

    private void refreshGeozones() {
        locationManager.getAvailableGeozones(new Callback<List<Geozone>>() {
            @Override
            public void onSuccess(List<Geozone> data) {
                setGeozones(data);
            }

            @Override
            public void onFailed(Throwable e) {
                Log.e("error", e.toString());
                showError(e);
            }
        });
    }

    @Override
    public void onZoneAdded(Long id) {
        refreshGeozones();
    }

    @Override
    public void onZoneUpdated(Long id) {
        refreshGeozones();
    }

    @Override
    public void onZoneRemoved(Long id) {
        refreshGeozones();
    }

    @Override
    public void onZoneEntered(Long id) {
        refreshGeozones();
    }

    @Override
    public void onZoneExited(Long id) {
        refreshGeozones();
    }

    @Override
    public void onZoneCheckIn(Long id) {
        refreshGeozones();
    }

    @Override
    public void onZoneCheckInFailure(Long id) {
    }
}
