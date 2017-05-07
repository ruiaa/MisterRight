package com.misterright.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.widget.TextView;


/**
 * Created by ruiaa on 2016/10/2.
 */

public class ResUtil {

    private static Context context;
    private static Resources resources;

    public static void register(Context appContext) {
        context = appContext.getApplicationContext();
        resources = context.getResources();
    }

    public static String getString(int stringFromR) {
        return resources.getString(stringFromR);
    }

    public static String format(int stringFromR ,Object... args){
        return String.format(ResUtil.getString(stringFromR),args);
    }

    public static int getColor(int colorFromR) {
        if (Build.VERSION.SDK_INT >= 23) {
            return resources.getColor(colorFromR, null);
        } else {
            return resources.getColor(colorFromR);
        }
    }

    public static int getColor(int colorFromR, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= 23) {
            return resources.getColor(colorFromR, theme);
        } else {
            return resources.getColor(colorFromR);
        }
    }

    public static Drawable getDrawable(int drawableFromR) {
        if (Build.VERSION.SDK_INT >= 21) {
            return resources.getDrawable(drawableFromR, null);
        } else {
            return resources.getDrawable(drawableFromR);
        }
    }

    public static Drawable getDrawable(int drawableFromR, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= 21) {
            return resources.getDrawable(drawableFromR, theme);
        } else {
            return resources.getDrawable(drawableFromR);
        }
    }

    public static float getDimen(int dimenFromR) {
        return resources.getDimension(dimenFromR);
    }

    public static int getDimenInPx(int dimenFromR){
        return resources.getDimensionPixelSize(dimenFromR);
    }

    public static String getPathFromDrawableId(int drawableId) {
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + resources.getResourcePackageName(drawableId)
                + "/" + resources.getResourceTypeName(drawableId)
                + "/" + resources.getResourceEntryName(drawableId)
        );
        return uri.toString();
    }

    public static void setTextAppearance(TextView textView, @StyleRes int resId){
        if (Build.VERSION.SDK_INT>=23){
            textView.setTextAppearance(resId);
        }else {
            textView.setTextAppearance(context,resId);
        }
    }

}
