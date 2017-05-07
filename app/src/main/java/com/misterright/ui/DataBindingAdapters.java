package com.misterright.ui;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.misterright.util.ResUtil;
import com.misterright.util.StringStyles;

/**
 * Created by ruiaa on 2016/11/13.
 */

public class DataBindingAdapters {

    /*
     * 图片
     */

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, String imageUri) {
        if (imageUri == null) {
            view.setImageURI(null);
        } else {
            view.setImageURI(Uri.parse(imageUri));
        }
    }

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, Uri imageUri) {
        view.setImageURI(imageUri);
    }

    @BindingAdapter("android:src")
    public static void setImageDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:drawableLeft")
    public static void setDrawableLeft(TextView textView,int resource){
        textView.setCompoundDrawablesWithIntrinsicBounds(ResUtil.getDrawable(resource),null,null,null);
    }

    @BindingAdapter("android:drawableRight")
    public static void setDrawableRight(TextView textView,int resource){
        textView.setCompoundDrawablesWithIntrinsicBounds(null,null,ResUtil.getDrawable(resource),null);
    }

    /*
     * 尺寸
     */
    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:minHeight")
    public static void setMinHeight(View view,float minHeight){
        view.setMinimumHeight((int)minHeight);
    }

    @BindingAdapter("android:minWidth")
    public static void setMinWidth(View view,float minWidth){
        view.setMinimumWidth((int)minWidth);
    }

    @BindingAdapter("android:paddingRight")
    public static void setPaddingRight(View view,float padding){
        view.setPadding(view.getPaddingLeft(),view.getPaddingTop(),(int)padding,view.getPaddingBottom());
    }

    @BindingAdapter("android:paddingLeft")
    public static void setPaddingLeft(View view,float padding){
        view.setPadding((int)padding,view.getPaddingTop(),view.getPaddingRight(),view.getPaddingBottom());
    }

    @BindingAdapter("android:layout_marginRight")
    public static void setMarginRight(View target,float margin){
        ViewGroup.MarginLayoutParams marginLayoutParams=(ViewGroup.MarginLayoutParams )target.getLayoutParams();
        if (marginLayoutParams!=null){
            marginLayoutParams.rightMargin=(int)margin;
            target.setLayoutParams(marginLayoutParams);
        }
    }

    @BindingAdapter("layout_widthPercent")
    public static void setWidthPercent(View target,float p){
        PercentRelativeLayout.LayoutParams layoutParams=(PercentRelativeLayout.LayoutParams)target.getLayoutParams();
        if (layoutParams!=null){
            layoutParams.getPercentLayoutInfo().widthPercent=p;
        }
    }

    @BindingAdapter("hintTextSize")
    public static void setHintTextSize(EditText editText, float size){

        CharSequence hint=editText.getHint();
        if (hint!=null&&hint.length()!=0){
            editText.setHint(StringStyles.RichText.setFormatSizePx(hint,size));
        }
    }




    /*
     * 位置
     */
    @BindingAdapter("android:layout_alignParentBottom")
    public static void setAlignParentBottom(View view, boolean alignParentBottom) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
        if(layoutParams!=null) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM , alignParentBottom?RelativeLayout.TRUE:0);
            view.setLayoutParams(layoutParams);
        }
    }
    @BindingAdapter("android:layout_alignParentRight")
    public static void setAlignParentRight(View view, boolean b) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
        if(layoutParams!=null) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT , b?RelativeLayout.TRUE:0);
            view.setLayoutParams(layoutParams);
        }
    }
    @BindingAdapter("android:layout_alignParentLeft")
    public static void setAlignParentLeft(View view, boolean b) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
        if(layoutParams!=null) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT , b?RelativeLayout.TRUE:0);
            view.setLayoutParams(layoutParams);
        }
    }
    @BindingAdapter("android:layout_alignParentTop")
    public static void setAlignParentTop(View view, boolean b) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
        if(layoutParams!=null) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP , b?RelativeLayout.TRUE:0);
            view.setLayoutParams(layoutParams);
        }
    }
}
