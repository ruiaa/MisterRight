package com.misterright.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.misterright.R;
import com.misterright.model.MisterData;
import com.misterright.util.ResUtil;
import com.misterright.util.media.VoiceRecorder;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/10/31.
 */

public class ChatVoiceRecorder extends RelativeLayout {

    private Context context;
    private ImageView imgVol;
    private ImageView imgMir;
    private ImageView imgCancel;
    private TextView hintRecording;
    private ImageView hintBg;
    private AnimationDrawable animationDrawable;
    private VoiceRecorder voiceRecorder;
    private ChatVoiceRecorderCallback callback;

    private boolean forbidCancelRecorder=false;

    private Subscription subscription;

    public ChatVoiceRecorder(Context context) {
        super(context);
        init(context);
    }

    public ChatVoiceRecorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatVoiceRecorder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.chat_voice_recorder, this);
        imgMir=(ImageView)findViewById(R.id.chat_voice_recording_mic);
        imgVol = (ImageView) findViewById(R.id.chat_voice_recording_animate);
        imgCancel=(ImageView)findViewById(R.id.chat_voice_recording_cancel);
        hintRecording = (TextView) findViewById(R.id.chat_voice_recording_hint);
        hintBg=(ImageView)findViewById(R.id.chat_voice_recording_hint_bg);
        animationDrawable=(AnimationDrawable) imgVol.getDrawable();
    }

    public void setCallback(ChatVoiceRecorderCallback callback) {
        this.callback = callback;
    }



    //流程
    public boolean startRecord() {
        if (voiceRecorder == null){
            voiceRecorder = VoiceRecorder.newVoiceRecorder(context);
        }
        if (voiceRecorder==null){
            return false;
        }
        voiceRecorder.startRecord();

        this.setVisibility(View.VISIBLE);
        showMoveToCancelAndVol();
        hintRecording.setBackgroundColor(Color.TRANSPARENT);
        subscription = voiceRecorder
                .getVolume(200)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showVoiceVolume);
        return true;
    }

    public void cancelRecord() {
        if (voiceRecorder == null) return;
        if (voiceRecorder.isRecording()) {
            voiceRecorder.cancelRecord();
            this.setVisibility(View.INVISIBLE);
        }
        if (subscription!=null){
            subscription.unsubscribe();
        }
    }

    public void cancelRecordForTooShort(){
        if (voiceRecorder == null) return;
        if (voiceRecorder.isRecording()) {
            voiceRecorder.cancelRecord();
            showTooShortToCancel();
            this.postDelayed(()->{
                ChatVoiceRecorder.this.setVisibility(INVISIBLE);
            },600L);
        }
        if (subscription!=null){
            subscription.unsubscribe();
        }
    }

    public void stopRecoding() {
        if (voiceRecorder == null) return;
        int l = voiceRecorder.stopRecord();
        if (l == 0) {
            showTooShortToCancel();
        } else if (callback != null) {
            callback.onVoiceRecordComplete(getVoiceFilePath(), l);
        }
        this.setVisibility(View.INVISIBLE);
        if (subscription!=null){
            subscription.unsubscribe();
        }
    }



    public void showVoiceVolume(int volume) {
        // 1--8
        if (volume<=7){
            animationDrawable.selectDrawable(volume);
        }else {
            animationDrawable.selectDrawable(7);
        }
    }

    public void showMoveToCancelAndVol() {
        hintBg.setVisibility(INVISIBLE);
        hintRecording.setText(context.getString(R.string.chat_move_cancel_record));
        imgMir.setVisibility(VISIBLE);
        imgVol.setVisibility(VISIBLE);
        imgCancel.setVisibility(INVISIBLE);
    }

    public void showUpToCancelAndBack() {
        hintBg.setVisibility(VISIBLE);
        hintRecording.setText(context.getString(R.string.chat_up_cancel_record));
        imgMir.setVisibility(INVISIBLE);
        imgVol.setVisibility(INVISIBLE);
        imgCancel.setVisibility(VISIBLE);
    }

    public void showTooShortToCancel() {
        imgMir.setVisibility(INVISIBLE);
        imgVol.setVisibility(INVISIBLE);
        imgCancel.setVisibility(VISIBLE);
        hintBg.setVisibility(INVISIBLE);
        hintRecording.setText(R.string.chat_too_short);
    }

    public void showTooLong(int t){
        imgMir.setVisibility(VISIBLE);
        imgVol.setVisibility(VISIBLE);
        imgCancel.setVisibility(INVISIBLE);
        hintBg.setVisibility(VISIBLE);
        hintRecording.setText(ResUtil.format(R.string.chat_too_long,t));
    }

    public String getVoiceFilePath() {
        return voiceRecorder.getFilePath();
    }

    public boolean isRecording() {
        return voiceRecorder.isRecording();
    }

    public interface ChatVoiceRecorderCallback {
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }

}
