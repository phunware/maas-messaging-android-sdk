package com.phunware.locationmessaging.sample.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.phunware.locationmessaging.Callback;
import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.attributes.AttributeManager;
import com.phunware.locationmessaging.entities.AttributeMetadataItem;
import com.phunware.locationmessaging.entities.ProfileAttribute;
import com.phunware.locationmessaging.log.Logging;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.activities.ProfileAttributeSelectionActivity;
import com.phunware.locationmessaging.sample.adapters.AttributeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Displays, and allows the user to edit, the profile attributes for this device.
 *
 * <p>There are three steps to displaying the data in this fragment.
 * <ol>
 * <li>Download the Attribute Metadata, which includes all possible attributes, and they're
 * possible values.</li>
 * <li>Download all currently set ProfileAttributes, which includes all attributes that currently
 * have a value.</li>
 * <li>Download the current UserAttribute, if it has been set.  This will return an error if one
 * doesn't exist.</li>
 * </ol>
 *
 * <p>All of these except the first can possible return nothing, so we need to handle those cases.
 *
 * <p>When the user decides to update a value, we send the entire new set of values to the server
 * to be updated.  This is important as the server will overwrite values with whatever we send,
 * including null.
 */
public class AttributeFragment extends Fragment implements AttributeAdapter.OnClickListener {

    public static final String TAG = AttributeFragment.class.getSimpleName();

    private static final int REQUEST_ATTRIBUTE_SELECTION = 0x01;

    private AttributeManager mAttributeManager;

    private List<AttributeMetadataItem> mAttributeMetadataItems = new ArrayList<>();
    private ProfileAttribute mProfileAttribute;

    private View mLoadingView;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attribute, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        mLoadingView = v.findViewById(R.id.loading);

        // This eats all touch events while the loading view is active.
        mLoadingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Setup the initial state
        mRecyclerView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAttributeManager = LocationMessaging.attributeManager();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        downloadAttributeMetadata();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.setTitle(R.string.nav_attributes);
        }
    }

    /**
     * Step 1, download attribute metadata.
     *
     * <p>This will download all possible values, irrespective of whether they have a value, and
     * their possible values.  This will allow us to show items that currently aren't set and also
     * provide a list of options for the user to set.
     */
    private void downloadAttributeMetadata() {
        Logging.d(TAG, "downloadAttributeMetadata()", null);
        mAttributeManager.getAttributeMetadata(new Callback<List<AttributeMetadataItem>>() {
            @Override
            public void onSuccess(List<AttributeMetadataItem> data) {
                Logging.d(TAG, "downloadAttributeMetadata" + "::onSuccess() - data =" + data.toString(),
                        null);
                mAttributeMetadataItems.addAll(data);
                downloadProfileAttributes();
            }

            @Override
            public void onFailed(Throwable e) {
                Logging.e(TAG, "Failed to download attribute metadata", e);
                downloadProfileAttributes();
            }
        });
    }

    /**
     * Step 2, download profile attributes.
     *
     * <p>This gets all of the current values of the attributes.
     */
    private void downloadProfileAttributes() {
        Logging.d(TAG, "downloadProfileAttributes()", null);
        mAttributeManager.getProfileAttributes(profileAttributeCallback);
    }

    private final Callback<ProfileAttribute> profileAttributeCallback =
            new Callback<ProfileAttribute>() {
                @Override
                public void onSuccess(ProfileAttribute data) {
                    Logging.v(TAG, "Downloaded " + data.profileAttributes().size()
                            + " profile attributes.", null);
                    mProfileAttribute = data;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buildUI();
                        }
                    });
                }

                @Override
                public void onFailed(Throwable e) {
                    Logging.w(TAG, "Failed to download profile attributes", e);
                    mProfileAttribute = ProfileAttribute.builder()
                            .profileAttributes(new HashMap<String, String>())
                            .deviceId("")
                            .build();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buildUI();
                        }
                    });
                }
            };

    private void buildUI() {
        if (!isResumed()) {
            return;
        }

        // Even if we don't have a value for an attribute, we want to display it in the list
        List<AttributeAdapter.Attribute> profileAttributes =
                new ArrayList<>(mAttributeMetadataItems.size());
        for (AttributeMetadataItem item : mAttributeMetadataItems) {
            if (mProfileAttribute.profileAttributes().containsKey(item.name())) {
                profileAttributes.add(new AttributeAdapter.Attribute(item,
                        mProfileAttribute.profileAttributes().get(item.name())
                ));
            } else {
                profileAttributes.add(new AttributeAdapter.Attribute(item,
                        getString(R.string.attribute_no_value)));
            }
        }

        mRecyclerView.setAdapter(new AttributeAdapter(profileAttributes, this));

        mLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(AttributeAdapter.Attribute attribute) {
        AttributeMetadataItem metadata = attribute.getMetadata();
        showProfileAttributeSelectionDialog(metadata.name(),
                (ArrayList<String>) metadata.allowedValues()
        );
    }

    private void showProfileAttributeSelectionDialog(String key, ArrayList<String> values) {
        Intent intent = new Intent(getActivity(), ProfileAttributeSelectionActivity.class);
        intent.putExtra(ProfileAttributeSelectionActivity.EXTRA_KEY, key);
        intent.putExtra(ProfileAttributeSelectionActivity.EXTRA_VALUES, values);
        startActivityForResult(intent, REQUEST_ATTRIBUTE_SELECTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ATTRIBUTE_SELECTION && resultCode == Activity.RESULT_OK) {
            String key = data.getStringExtra(ProfileAttributeSelectionActivity.EXTRA_KEY);
            String value = data.getStringExtra(ProfileAttributeSelectionActivity.EXTRA_VALUE);
            updateProfileAttribute(key, value);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateProfileAttribute(String key, String value) {
        // Update the view to show the loading overlay
        mLoadingView.setVisibility(View.VISIBLE);

        HashMap<String, String> attributes = new HashMap<>(mProfileAttribute.profileAttributes());

        if (value == null || value.isEmpty()) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }

        // Send the new profile attributes to be updated on the server, updating the current
        // values with the response.
        mAttributeManager.updateProfileAttribute(attributes, profileAttributeCallback);
    }
}
