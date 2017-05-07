package com.misterright.util.media;

import android.content.Context;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;

import com.misterright.util.LogUtil;
import com.misterright.util.bus.NoRecordVoicePermissionEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.storage.FileStorage;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by ruiaa on 2016/10/31.
 */

public class VoiceRecorder {

    private static final String FOLDER_NAME="voices";
    private static final String EXTENSION = ".amr";

    private static final int RECORD_ERROR=-1;

    private Context context;

    private File voicesDir;
    private File file;
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private long startTime;

    private VoiceRecorder(){

    }

    private VoiceRecorder(Context context){
        this.context=context;
        voicesDir=new File(FileStorage.getVoiceCacheDir());
    }

    @Nullable
    public static VoiceRecorder newVoiceRecorder(Context context){

       return new VoiceRecorder(context);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getFilePath() {
        return file==null ? null : file.getAbsolutePath();
    }

    public String startRecord(){
        file =null;
        try {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioChannels(1); // MONO
            recorder.setAudioSamplingRate(44100); // 44100Hz
            recorder.setAudioEncodingBitRate(64);

            file =new File(voicesDir.getAbsoluteFile(),createFileName());
            recorder.setOutputFile(file.getAbsolutePath());
            recorder.prepare();
            isRecording = true;
            recorder.start();

            startTime = new Date().getTime();

            return file == null ? null : file.getAbsolutePath();

        }catch (Exception e){
            LogUtil.e("startRecord--",e);
            RxBus.getDefault().post(new NoRecordVoicePermissionEvent());
        }
        return null;
    }

    public void cancelRecord(){
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                if (file != null && file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            } catch (Exception e){
                LogUtil.e("cancelRecord--",e);
            }
            isRecording = false;
        }
    }

    public int stopRecord(){
        if(recorder != null){

            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;

            if(file == null || !file.exists() || !file.isFile()){
                return RECORD_ERROR;
            }
            if (file.length() == 0) {
                file.delete();
                return RECORD_ERROR;
            }
            double l=(new Date().getTime()) - startTime;
            return (int)Math.round(l/1000);
        }
        return RECORD_ERROR;
    }

    public Observable<Integer> getVolume(int millis){
        return Observable
                .interval(millis, TimeUnit.MILLISECONDS)
                .map(aLong -> getVoiceVolume());
    }

    private  String createFileName(){
        return System.currentTimeMillis() + EXTENSION;
    }

    private int getVoiceVolume(){
        if (isRecording&&recorder!=null) {
            int i=recorder.getMaxAmplitude();
/*            LogUtil.i("getVoiceVolume--"+"原始"+i);
            LogUtil.i("getVoiceVolume--"+(i * 16 / 0x7FFF));*/
            return i * 8 / 0x7FFF;
        }else {
            return 0;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
    }
}
