package com.phunware.locationmessaging.sample.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.adapters.ConfigAdapter;
import com.phunware.locationmessaging.sample.models.Config;
import com.phunware.locationmessaging.sample.sql.ConfigStore;
import com.phunware.locationmessaging.sample.util.AppSettings;

import java.util.List;

/**
 * Fragment that displays configuration information.
 */
public class ConfigFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Config>> {

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_config, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.setTitle(R.string.app_name);
        }
    }

    @Override
    public Loader<List<Config>> onCreateLoader(int id, Bundle args) {
        return new ConfigLoader(getActivity().getApplicationContext(), null);
    }

    @Override
    public void onLoadFinished(Loader<List<Config>> loader, List<Config> data) {
        List<Config> availableConfigs = data;

        Config selectedConfig = AppSettings.readConfig(getActivity());
        int position = AppSettings.readConfigPosition(getActivity());

        ConfigAdapter adapter  = new ConfigAdapter(selectedConfig, availableConfigs, position);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Config>> loader) {
    }

    private static class ConfigLoader extends AsyncTaskLoader<List<Config>> {
        List<Config> mData;
        ConfigStore store;

        public ConfigLoader(final Context context, final Bundle args) {
            super(context);
            store = new ConfigStore(context);
        }

        @Override
        public List<Config> loadInBackground() {
            return store.getConfigurations();
        }

        @Override
        public void deliverResult(final List<Config> data) {
            mData = data;
            super.deliverResult(data);
        }

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                deliverResult(mData);
            } else {
                forceLoad();
            }
        }
    }
}
