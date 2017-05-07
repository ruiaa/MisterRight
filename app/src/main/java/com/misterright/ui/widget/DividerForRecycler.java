package com.misterright.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.misterright.R;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;

/**
 * Created by ruiaa on 2016/11/26.
 * <p>
 * android.support.v7.widget.DividerItemDecoration
 */
public class DividerForRecycler extends RecyclerView.ItemDecoration {

    private int drawableId = R.drawable.linearlayout_divider;
    private Drawable mDivider;

    private boolean drawHorizontal = false;
    private boolean drawVertical = false;


    public DividerForRecycler(Context context, boolean drawHorizontal, boolean drawVertical) {
        mDivider = ResUtil.getDrawable(drawableId);
        this.drawHorizontal = drawHorizontal;
        this.drawVertical = drawVertical;
    }

    public void setDrawHorizontal(boolean drawHorizontal) {
        this.drawHorizontal = drawHorizontal;
    }

    public void setDrawVertical(boolean drawVertical) {
        this.drawVertical = drawVertical;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        /*if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }*/
        if (drawHorizontal) {
            drawHorizontal(c, parent);
        }
        if (drawVertical) {
            drawVertical(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        /*if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }*/
        int width = drawHorizontal ? mDivider.getIntrinsicWidth() : 0;
        int height = drawVertical ? mDivider.getIntrinsicHeight() : 0;
        outRect.set(0, 0, width, height);
    }
}
