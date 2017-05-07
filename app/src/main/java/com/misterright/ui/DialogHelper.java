package com.misterright.ui;

import android.app.Activity;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.misterright.R;
import com.misterright.util.ResUtil;

/**
 * Created by ruiaa on 2016/11/20.
 */

public class DialogHelper {

    private Activity activity;

    private MaterialDialog progressWaitingDialog;

    private MaterialDialog textPositiveDialog;
    private DialogOnClickListener textPositiveListener;


    public DialogHelper(Activity activity) {
        this.activity = activity;
    }


    /*
     * 一直转的等待条
     */
    public MaterialDialog waitingProgressDialog(String title) {
        if (progressWaitingDialog == null) {
            progressWaitingDialog = new MaterialDialog.Builder(activity)
                    .progress(true, 0)
                    .build();
            progressWaitingDialog.setCanceledOnTouchOutside(false);
        }
        //progressWaitingDialog.setTitle(title);
        progressWaitingDialog.setContent(title);
        progressWaitingDialog.show();
        return progressWaitingDialog;
    }

    public MaterialDialog waitingProgressDialog(int titleId){
        return waitingProgressDialog(ResUtil.getString(titleId));
    }

    public MaterialDialog hideWaitingProgressDialog(){
        if (progressWaitingDialog!=null){
            progressWaitingDialog.dismiss();
        }
        return progressWaitingDialog;
    }

    public MaterialDialog showTextPositiveDialog(String content, DialogOnClickListener listener) {
        if (textPositiveDialog == null) {
            textPositiveDialog = new MaterialDialog.Builder(activity)
                    .contentGravity(GravityEnum.CENTER)
                    .positiveText(R.string.sure)
                    .onPositive((dialog, which) -> {
                        if (textPositiveListener != null) {
                            textPositiveListener.onClick(dialog);
                        }
                    })
                    .build();
        }
        textPositiveDialog.setContent(content);
        textPositiveListener = listener;
        textPositiveDialog.show();
        return textPositiveDialog;
    }

    public MaterialDialog showTextPositiveDialog(int contentId, DialogOnClickListener listener) {
        return showTextPositiveDialog(ResUtil.getString(contentId),listener);
    }

    public MaterialDialog showTextPositiveDialog(String content){
        return showTextPositiveDialog(content,null);
    }

    public MaterialDialog showTextPositiveDialog(int contentId){
        return showTextPositiveDialog(contentId,null);
    }

    public void hideAll(){
        if (progressWaitingDialog!=null&&progressWaitingDialog.isShowing()){
            progressWaitingDialog.hide();
        }
        if (textPositiveDialog!=null&&textPositiveDialog.isShowing()){
            textPositiveDialog.hide();
        }
    }

    interface DialogOnClickListener {
        void onClick(MaterialDialog dialog);
    }
}
