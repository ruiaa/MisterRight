package com.misterright.util.keyboard;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

/**
 *
 *
 * use
 * Activity的onCreate()方法里调用AndroidBug5497Workaround.assistActivity(this);即可。注意：在setContentView(R.layout.xxx)之后调用。
 *
 */

public class AndroidKeyboardAdjustResizeNoWork {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity) {
       // new AndroidKeyboardAdjustResizeNoWork(activity);
        assistActivity(activity,null);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidKeyboardAdjustResizeNoWork(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }


    /*
     * 第2种
     */
    /**
     * 关于 全屏Activity adjustResize属性无效解决方案
     * 在Activity setContentView 之后调用
     *
     * @param activity
     * @param listener 软键盘显示隐藏监听, 可以为空
     */
    public static void assistActivity(Activity activity, OnKeyboardListener listener) {
        boolean subOffset = false;
        if (Build.VERSION.SDK_INT < 18) {
            subOffset = true;
        }
        new AndroidKeyboardAdjustResizeNoWork(activity, subOffset, listener);
    }

    private boolean mSubOffset;
    private int mCurrHeight;
    private Rect mRootRect;
    private View mContentView;
    private LayoutParams mLayoutParams;
    private OnKeyboardListener mListener;

    /**
     *
     * @param activity
     * @param isSubOffset 是否需要偏移
     * @param listener 软键盘监听 监听隐藏，显示
     */
    private AndroidKeyboardAdjustResizeNoWork(final Activity activity, boolean isSubOffset, OnKeyboardListener listener) {
        this.mSubOffset = isSubOffset;
        this.mListener = listener;
        this.mContentView = ((FrameLayout) activity.findViewById(android.R.id.content)).getChildAt(0);
        this.mLayoutParams = (LayoutParams) mContentView.getLayoutParams();
        this.mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent2();
            }
        });
        activity.getWindow().getDecorView().getRootView().getWindowVisibleDisplayFrame(mRootRect = new Rect());
    }

    private void possiblyResizeChildOfContent2() {
        Rect r = new Rect();
        mContentView.getWindowVisibleDisplayFrame(r);
        int usableHeightNow = r.height();
        // Log.i("III", "usableHeightNow " + usableHeightNow + ", r " + r.toString());
        if (usableHeightNow != mCurrHeight) {
            int usableHeightSansKeyboard = r.bottom;
            if(mSubOffset) {
                usableHeightSansKeyboard -= r.top;
            }
            // android.util.Log.i("III", "usableHeightSansKeyboard " + usableHeightSansKeyboard + ", r " + r.toString());
            // keyboard probably just became hidden
            mLayoutParams.height = usableHeightSansKeyboard;
            if (mListener != null) {
                if (mRootRect.bottom != r.bottom) {
                    mListener.onKeyboardChanged(OnKeyboardListener.SHOW);
                } else {
                    mListener.onKeyboardChanged(OnKeyboardListener.HIDE);
                }
            }

            mContentView.requestLayout();
            mCurrHeight = usableHeightNow;
        }
    }

    public interface OnKeyboardListener {
        int SHOW = 1;
        int HIDE = 0;

        void onKeyboardChanged(int state);
    }

}
