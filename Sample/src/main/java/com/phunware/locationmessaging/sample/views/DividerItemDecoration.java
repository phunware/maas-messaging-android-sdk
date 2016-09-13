package com.phunware.locationmessaging.sample.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final Rect mTemp = new Rect();

    public DividerItemDecoration(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.dividerHorizontal, typedValue, true);
        TypedArray a = context.obtainStyledAttributes(typedValue.data,
                new int[]{android.R.attr.dividerHorizontal});
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }

        final int childCount = parent.getChildCount();
        final int adapterCount = adapter.getItemCount();

        for (int i = 0; i < childCount; i++) {
            final View v = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(v);
            if (position < adapterCount) {
                mDivider.setBounds(v.getLeft(), v.getBottom() - 1, v.getRight(), v.getBottom());
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
        // skip the last item
        if (parent.getChildAdapterPosition(view) < parent.getAdapter().getItemCount()) {
            outRect.bottom = 1;
        }
    }
}
