package com.phunware.locationmessaging.sample.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.phunware.locationmessaging.Callback;
import com.phunware.locationmessaging.LocationMessaging;
import com.phunware.locationmessaging.entities.Message;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.adapters.MessageAdapter;
import com.phunware.locationmessaging.sample.views.DividerItemDecoration;

import java.util.List;

/**
 *
 */
public class MessageListFragment extends Fragment
        implements MessageAdapter.OnMessageSelectedListener {

    private RecyclerView mList;
    private ProgressBar mLoading;
    private TextView mError;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message_list, container, false);
        mList = (RecyclerView) v.findViewById(R.id.list);
        mLoading = (ProgressBar) v.findViewById(R.id.loading);
        mError = (TextView) v.findViewById(R.id.error);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.setTitle(R.string.nav_messages);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.addItemDecoration(new DividerItemDecoration(getActivity()));

        mLoading.setVisibility(View.VISIBLE);
        mError.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);

        LocationMessaging.messageManager().getMessages(new Callback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> data) {
                onMessages(data);
            }

            @Override
            public void onFailed(Throwable e) {
                onError(e);
            }
        });
    }

    private void onMessages(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            showEmpty();
            return;
        }

        MessageAdapter adapter = new MessageAdapter(messages, this);
        mList.setAdapter(adapter);

        mLoading.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mList.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        mLoading.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);
        mError.setText(R.string.no_messages);
    }

    private void onError(Throwable e) {
        mLoading.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);
        mError.setText(getString(R.string.message_load_error, e.getMessage()));
    }

    @Override
    public void onMessageSelected(Message message) {
        MessageDetailFragment f = MessageDetailFragment.newInstance(message);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, f)
                .addToBackStack(null)
                .commit();
    }
}
