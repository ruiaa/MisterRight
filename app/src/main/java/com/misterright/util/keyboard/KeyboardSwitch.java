package com.misterright.util.keyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.misterright.util.LogUtil;

/**
 * Created by ruiaa on 2016/11/16.
 */

public class KeyboardSwitch {

    private static final String SHARE_PREFERENCE_NAME = "KeyboardSwitch";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";

    private Activity activity;
    private InputMethodManager inputMethodManager;//软键盘管理类
    private SharedPreferences sp;
    private View emojiLayout;//软键盘 覆盖布局
    private EditText editText;//

    private View topView;//软键盘上面的view,用于固定bar的高度,防止跳闪

    private KeyboardSwitch(){
    }

    /**
     * 外部静态调用
     * @param activity
     * @return
     */
    public static KeyboardSwitch with(Activity activity) {
        KeyboardSwitch keyboardSwitch = new KeyboardSwitch();
        keyboardSwitch.activity = activity;
        keyboardSwitch.inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboardSwitch.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return keyboardSwitch;
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     * @param topView
     * @return
     */
    public KeyboardSwitch bindToTopView(View topView) {
        this.topView = topView;
        return this;
    }

    /**
     * 设置表情内容布局
     * @param emotionView
     * @return
     */
    public KeyboardSwitch bindToEmotionView(View emotionView) {
        emojiLayout = emotionView;
        return this;
    }

    /**
     * 绑定编辑框
     * @param editText
     * @return
     */
    public KeyboardSwitch bindToEditText(EditText editText) {
        this.editText = editText;
        this.editText.requestFocus();
        this.editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && emojiLayout.isShown()) {
                    showEmotionLayout();
                }
                return false;
            }
        });
        return this;
    }

    /**
     * 绑定表情按钮
     * @param emotionButton
     * @return
     */
    public KeyboardSwitch bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiLayout.isShown()) {
                    showSoftInput();
                } else {
                    showEmotionLayout();
                }
            }
        });
        return this;
    }

    public KeyboardSwitch build(){
//设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE  这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软件盘
        hideSoftInput();
        hideEmotionLayout();

        if (editText!=null){
            editText.setOnClickListener(v -> {
                showSoftInput();
            });
        }

        return this;
    }
    /**
     * 点击返回键时先隐藏表情布局
     * @return
     */
    public boolean interceptBackPress() {
        if (emojiLayout.isShown()) {
            hideEmotionLayout();
            return true;
        }
        return false;
    }


    public void showEmotionLayout() {
        if (emojiLayout.isShown()) return;

        if (isSoftInputShown()) {

            lockContentHeight();
            hideSoftInput();
            int softInputHeight = getSupportSoftInputHeight();
            if (softInputHeight == 0) {
                softInputHeight = sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
            }
            emojiLayout.getLayoutParams().height = softInputHeight;
            emojiLayout.setVisibility(View.VISIBLE);
            unlockContentHeightDelayed();

        } else {

            int softInputHeight = getSupportSoftInputHeight();
            if (softInputHeight == 0) {
                softInputHeight = sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
            }
            emojiLayout.getLayoutParams().height = softInputHeight;
            emojiLayout.setVisibility(View.VISIBLE);

        }
    }

    public void hideEmotionLayout() {
        emojiLayout.setVisibility(View.GONE);
    }

    public void showSoftInput() {

        if (emojiLayout.isShown()) {
            //显示软件盘时，锁定内容高度，防止跳闪。
            lockContentHeight();
            //隐藏表情布局
            emojiLayout.setVisibility(View.GONE);
            //显示软件盘
            editText.requestFocus();
            editText.post(new Runnable() {
                @Override
                public void run() {
                    inputMethodManager.showSoftInput(editText, 0);
                }
            });
            //软件盘显示后，释放内容高度
            unlockContentHeightDelayed();

        }else {
            editText.requestFocus();
            editText.post(new Runnable() {
                @Override
                public void run() {
                    inputMethodManager.showSoftInput(editText, 0);
                }
            });
        }
    }

    public void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topView.getLayoutParams();
        params.height = topView.getHeight();
        params.weight = 0.0F;
    }
    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) topView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }


    /**
     * 是否显示软件盘
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }
    /**
     * 获取软件盘的高度
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;
        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
            LogUtil.w("KeyboardSwitch--Warning: value of softInputHeight is below zero!");
        }
        //存一份到本地
        if (softInputHeight > 0) {
            sp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }
    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }
    /**
     * 获取软键盘高度
     * @return
     */
    public int getKeyBoardHeight(){
        return sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
    }
}
