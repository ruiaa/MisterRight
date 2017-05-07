package com.misterright.ui.base;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.misterright.R;
import com.misterright.util.ResUtil;

/**
 * Created by ruiaa on 2016/11/8.
 */

public class ToolbarFragment extends BaseFragment {

    protected Activity containerActivity;
    protected Toolbar toolbar;
    protected TextView toolbarTitle;
    protected ImageView toolbarLeft;
    protected TextView toolbarRight;
    protected boolean toolbarIsHidden = false;

    protected void initToolbar(View rootView, Activity containerActivity) {
        this.containerActivity=containerActivity;
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarTitle=(TextView)rootView.findViewById(R.id.toolbar_title);
        toolbarLeft=(ImageView)rootView.findViewById(R.id.toolbar_left);
        toolbarRight=(TextView)rootView.findViewById(R.id.toolbar_right);

        if (canTurnBack()){
            setToolbarLeftShow(v->{
                onTurnBack();
            });
        }
    }


    protected boolean canTurnBack() {
        return false;
    }

    protected void onTurnBack(){containerActivity.onBackPressed();}

    protected void hideOrShowToolbar() {
        toolbar.animate()
                .translationY(toolbarIsHidden ? 0 : -toolbar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        toolbarIsHidden = !toolbarIsHidden;
    }

    public void setTitle(String s) {
        toolbarTitle.setText(s);
    }

    public void setTitle(@StringRes int title){
        toolbarTitle.setText(title);
    }

    public void setToolbarTransparent(){
        toolbar.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setToolbarLeftShow(View.OnClickListener onClickListener){
        toolbarLeft.setVisibility(View.VISIBLE);
        toolbarLeft.setOnClickListener(onClickListener);
    }


    public void setToolbarRightText(String text,View.OnClickListener onClickListener){
        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setText(text);
        if (onClickListener!=null){
            toolbarRight.setOnClickListener(onClickListener);
        }
    }

    public void setToolbarRightText(int text,View.OnClickListener onClickListener){
        setToolbarRightText(ResUtil.getString(text),onClickListener);
    }
}
