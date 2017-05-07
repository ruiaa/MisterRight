package com.misterright.util.media;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.widget.ImageView;

import com.misterright.util.LogUtil;

/**
 * Created by ruiaa on 2016/11/2.
 */

public class VoicePlayer {

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private MediaPlayer.OnCompletionListener onCompletionListener = null;

    private ImageView imageView= null;
    private AnimationDrawable voiceAnimation=null;
    private String filePath = null;

    public VoicePlayer() {
        onCompletionListener = mp -> stopPlay();
    }

    public void onVoiceCLick(String filePath, ImageView imageView) {
        if (isPlaying
                && this.filePath != null
                && this.filePath.equals(filePath)
                ) {
            stopPlay();
        } else {
            startPlay(filePath, imageView);
        }
    }

    public void startPlay(String filePath,ImageView imageView) {

        if (mediaPlayer != null) {
            stopPlay();
        }

        this.imageView=imageView;
        this.filePath = filePath;

        if (onCompletionListener == null) {
            onCompletionListener = mp -> stopPlay();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(onCompletionListener);

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            isPlaying = true;
            if (imageView != null) {
                //imageView.setImageResource(R.drawable.chat_voice_anim_playing_send);
                voiceAnimation=(AnimationDrawable)imageView.getBackground();
                voiceAnimation.start();
            }

        } catch (Exception e) {
            LogUtil.e("startPlay--", e);
        }

    }

    public void stopPlay() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
            voiceAnimation.selectDrawable(0);
            voiceAnimation = null;
        }
        if (imageView!=null){
           // imageView.setImageResource(R.mipmap.chat_voice_playing);
            imageView=null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;

    }
}
