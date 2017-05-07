package com.misterright.util;

import android.content.Context;

/**
 * Created by ruiaa on 2016/10/25.
 */

public class ConvertUtil  {

    private static Context context;

    public static void register(Context appContext) {
        context = appContext.getApplicationContext();
    }


    public static int dp2px(float dpValue) {
        if (dpValue==0) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dp( float pxValue) {
        if (pxValue==0) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static int sp2px(float spValue) {
        if (spValue==0) return 0;
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static int px2sp(float pxValue) {
        if (pxValue==0) return 0;
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static float voiceLength2Percent(int length){

        /*
         * 0 - 100
         *
         * 小于2秒     20
         *
         * 大于60秒    100
         */
        if (length<=1) return 0.2f;
        if (length>=60) return 0.7f;
        return (float) (Math.log(length)/Math.log(60)*0.5+0.2);
    }
}
