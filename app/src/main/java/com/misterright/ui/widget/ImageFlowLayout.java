package com.misterright.ui.widget;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.misterright.R;

import java.util.List;

/**
 * Created by ruiaa on 2016/11/6.
 */

public class ImageFlowLayout extends FlowLayout {

    private boolean hadSetAll=false;

    public ImageFlowLayout(Context context) {
        super(context);
    }

    public ImageFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @BindingAdapter("imgUrls")
    public static void setImgUrls(ImageFlowLayout imageFlowLayout, List<String> list) {
        if (list == null) return;
        if (imageFlowLayout.hadSetAll) return;
        imageFlowLayout.hadSetAll=true;
        LayoutInflater inflater = LayoutInflater.from(imageFlowLayout.getContext());
        for (String s : list) {
            ImgViewGlide imgViewGlide = (ImgViewGlide) inflater.inflate(R.layout.item_image, imageFlowLayout, false);
            ImgViewGlide.setImgUrl(imgViewGlide, s);
            imageFlowLayout.addView(imgViewGlide);
        }
        imageFlowLayout.invalidate();
    }

    @BindingAdapter("imgQinius")
    public static void setImgQinius(ImageFlowLayout imageFlowLayout, List<String> list) {
        if (list == null) return;
        if (imageFlowLayout.hadSetAll) return;
        imageFlowLayout.hadSetAll=true;
        LayoutInflater inflater = LayoutInflater.from(imageFlowLayout.getContext());
        for (String s : list) {
            ImgViewGlide imgViewGlide = (ImgViewGlide) inflater.inflate(R.layout.item_image, imageFlowLayout, false);
            ImgViewGlide.setQiniu(imgViewGlide,s);
            imageFlowLayout.addView(imgViewGlide);
        }
        imageFlowLayout.invalidate();
    }

    public static void addImgLocalPath(ImageFlowLayout imageFlowLayout, String path) {
        if (path == null) return;
        LayoutInflater inflater = LayoutInflater.from(imageFlowLayout.getContext());
        ImgViewGlide imgViewGlide = (ImgViewGlide) inflater.inflate(R.layout.item_image, imageFlowLayout, false);
        ImgViewGlide.setImgUrl(imgViewGlide, path);
        imageFlowLayout.addView(imgViewGlide);
        imageFlowLayout.invalidate();
    }
    public  void addImgLocalPath(String path) {
        addImgLocalPath(this,path);
    }

    public static ImgViewGlide addImgRes(ImageFlowLayout imageFlowLayout, int resId) {
        LayoutInflater inflater = LayoutInflater.from(imageFlowLayout.getContext());
        ImgViewGlide imgViewGlide = (ImgViewGlide) inflater.inflate(R.layout.item_image, imageFlowLayout, false);
        ImgViewGlide.setImgResId(imgViewGlide,resId);
        imageFlowLayout.addView(imgViewGlide);
        imageFlowLayout.invalidate();
        return imgViewGlide;
    }
    public ImgViewGlide addImgRes(int resId){
        return addImgRes(this,resId);
    }
}
