package com.misterright.util.media;

/**
 * Created by ruiaa on 2016/10/28.
 */

public class RecoderManager {
    private static RecoderManager instance = null;

    private RecoderManager(){

    }

    public static RecoderManager getInstance() {
        if(instance==null) {
            synchronized (RecoderManager.class) {
                if (instance == null) {
                    instance = new RecoderManager();
                }
            }
        }
        return instance;
    }
}
