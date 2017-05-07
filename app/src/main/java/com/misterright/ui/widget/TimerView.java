package com.misterright.ui.widget;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.misterright.util.ConvertUtil;

/**
 * Created by ruiaa on 2016/10/30.
 */

public class TimerView extends TextView {

    private int height;
    private int width;
    private int unitPadding;
    private int innerPadding;
    private int leftTime = 0;
    private String hour = null;
    private String minute = null;
    private String second = null;

    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hour==null){

        }else {

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }else {
            height= ConvertUtil.dp2px(20);
        }
        width=MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @BindingAdapter("leftTime")
    public static void setLeftTime(TimerView timerView, int leftTime) {
        int h = leftTime / 3600;
        int m = (leftTime % 60) / 60;
        int s = leftTime % 3600;
        if (h == 0) {
            timerView.hour = null;
        }
        if (m < 10) {
            timerView.minute = "0" + m;
        } else {
            timerView.minute=String.valueOf(m);
        }
        if (s<10){
            timerView.second="0"+s;
        }else {
            timerView.second=String.valueOf(s);
        }
    }
}
