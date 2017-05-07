package com.misterright.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.misterright.R;

import java.util.List;

/**
 * Created by ruiaa on 2016/10/30.
 */

public class LabelLayout extends FlowLayout {

    private boolean haveAdd=false;
    private int labelId=R.layout.view_label;

    public LabelLayout(Context context) {
        super(context);
    }

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelLayout);
        try {
            labelId=a.getResourceId(R.styleable.LabelLayout_child_layout,R.layout.view_label);
        } finally {
            a.recycle();
        }
    }


    @BindingAdapter("textList")
    public static void setTextList(LabelLayout labelLayout, List<String> list){
        if (list==null) return;
        String[] strings=new String[list.size()];
        list.toArray(strings);
        setTextList(labelLayout,strings);
    }

    @BindingAdapter("textList")
    public static void setTextList(LabelLayout labelLayout, String[] strings){
        if (strings==null) return;
        if (labelLayout.haveAdd){
            labelLayout.removeAllViews();
        }else {
            labelLayout.haveAdd=true;
        }
        LayoutInflater inflater=LayoutInflater.from(labelLayout.getContext());
        for (String s:strings){
            TextView textView=(TextView)inflater.inflate(labelLayout.labelId,labelLayout,false);
            textView.setText(s);
            labelLayout.addView(textView);
        }
    }

}
