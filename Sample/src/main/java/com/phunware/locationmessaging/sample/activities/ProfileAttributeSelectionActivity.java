package com.phunware.locationmessaging.sample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phunware.locationmessaging.sample.R;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.List;

public class ProfileAttributeSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_KEY = "key";
    public static final String EXTRA_VALUES = "values";
    public static final String EXTRA_VALUE = "index";

    private String key;

    RecyclerView mRecyclerView;
    ValueAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = new RecyclerView(this);
        setContentView(mRecyclerView);
        setTitle(R.string.title_choose_value);

        Intent data = getIntent();
        if (data == null) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            key = data.getStringExtra(EXTRA_KEY);

            List<String> values = data.getStringArrayListExtra(EXTRA_VALUES);
            values.add(0, "");
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ValueAdapter(values);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class ValueAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<String> values;

        public ValueAdapter(List<String> values) {
            this.values = values;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_single_line,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String value = values.get(position);
            if (value.isEmpty()) {
                value = getString(R.string.attribute_no_value);
            }
            holder.title.setText(value);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }

        public String get(int position) {
            return values.get(position);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent data = new Intent();
            data.putExtra(EXTRA_KEY, key);
            data.putExtra(EXTRA_VALUE, mAdapter.get(getAdapterPosition()));
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
