package com.phunware.locationmessaging.sample.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.SampleApplication;
import com.phunware.locationmessaging.sample.activities.MainActivity;
import com.phunware.locationmessaging.sample.adapters.LogMessageAdapter;
import com.phunware.locationmessaging.sample.models.Config;
import com.phunware.locationmessaging.sample.models.LogMessage;
import com.phunware.locationmessaging.sample.sql.LogMessageContract;
import com.phunware.locationmessaging.sample.util.AppSettings;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Displays log messages for the current config to the user.
 */
public class LogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        PopupMenu.OnMenuItemClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = LogFragment.class.getSimpleName();
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL = 15;
    //TODO: CHANGE THIS BACK TO FALSE WHEN WE FIX PERMISSIONS
    private boolean mWriteExternal = true;

    private static final String BODY_FORMAT = "LocationMessaging log file.%n%n"
            + "AppId: %s%n"
            + "Environment: %s%n";

    private RecyclerView mRecyclerView;
    private LogMessageAdapter mAdapter;
    private String mSearchQuery = "";
    private LogMessage.Level mFilterLevel = LogMessage.Level.VERBOSE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log, container, false);
        mRecyclerView = (RecyclerView) v;
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LogMessageAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.setTitle(R.string.nav_logs);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_log, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mWriteExternal) {
                    sendLogFile();
                }
                else {
                    ((MainActivity) getActivity()).checkWriteExternalPermissions();
                }
                return true;
            case R.id.filter:
                View itemView = getActivity().findViewById(R.id.filter);
                showFilterPopup(itemView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Allows the user to send the log file. This method will create a copy of the current log file
     * in a public directory so that it can be sent.
     */
    private void sendLogFile() {
        File file = ((SampleApplication) getActivity().getApplication())
                .getFileLogger()
                .getLogFile();

        // create a file in the public location
        File targetDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File target = null;
        try {
            target = File.createTempFile("lm_sample", ".txt", targetDir);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create log file at: "
                    + (targetDir != null ? targetDir.getAbsolutePath() : null), e);
            Toast.makeText(getActivity(), R.string.error_send_log_create_file, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // copy the log file there
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(new FileOutputStream(target));

            byte[] buf = new byte[4096];
            int count;
            while ((count = in.read(buf)) > 0) {
                out.write(buf, 0, count);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy log file", e);
            Toast.makeText(getActivity(), R.string.error_send_log_copy_file, Toast.LENGTH_LONG)
                    .show();
            return;
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                // don't care
            }
        }

        // Send it
        Config config = AppSettings.readConfig(getActivity());
        String body = String.format(Locale.US, BODY_FORMAT,
                config.getAppId(), config.getEnvironment());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(target));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, getString(R.string.title_share)));
    }

    /**
     * Constructs and shows a filter popup menu rooted a <code>view</code>.
     * @param view The view for the popup to be rooted at.
     */
    private void showFilterPopup(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        Menu menu = popup.getMenu();
        final int count = LogMessage.Level.values().length;
        for (int i = 0; i < count; i++) {
            menu.add(0, i, i, getStringResForLevel(LogMessage.Level.values()[i]));
        }
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    private int getStringResForLevel(LogMessage.Level level) {
        switch (level) {
            case VERBOSE:
                return R.string.log_level_v;
            case INFO:
                return R.string.log_level_i;
            case DEBUG:
                return R.string.log_level_d;
            case WARNING:
                return R.string.log_level_w;
            case ERROR:
                return R.string.log_level_e;
            case WTF:
            default:
                return R.string.log_level_wtf;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mFilterLevel = LogMessage.Level.values()[item.getItemId()];
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = LogMessageContract.LogMessageEntry.COLUMN_LEVEL + ">=?";
        String[] selectionArgs = {String.valueOf(mFilterLevel.ordinal())};
        Uri uri = LogMessageContract.CONTENT_URI;
        if (!TextUtils.isEmpty(mSearchQuery)) {
            uri = uri.buildUpon().appendPath(mSearchQuery).build();
        }

        return new CursorLoader(getActivity(), uri, null, selection, selectionArgs,
                LogMessageContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
        mAdapter.notifyDataSetChanged();
    }

    /*
     * Query Callbacks
     */

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchQuery = query;
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return onQueryTextSubmit(newText);
    }

    public void writeExternalPrivsGranted() {
        mWriteExternal = true;
        sendLogFile();
    }
}
