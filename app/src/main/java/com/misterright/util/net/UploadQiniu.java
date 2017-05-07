package com.misterright.util.net;


import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.util.EncodeUtil;
import com.misterright.util.LogUtil;
import com.misterright.util.storage.ImageUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import rx.schedulers.Schedulers;

/**
 * Created by ruiaa on 2016/11/6.
 */

public class UploadQiniu {
    private static UploadQiniu instance = null;

    private UploadManager uploadManager;

    private UploadQiniu() {
        uploadManager = new UploadManager();
    }

    public static UploadQiniu getInstance() {
        if (instance == null) {
            synchronized (UploadQiniu.class) {
                if (instance == null) {
                    instance = new UploadQiniu();
                }
            }
        }
        return instance;
    }

    private String getKey() {
        return getKey(String.valueOf(MisterData.getInstance().globalInfo.userInfo.uid));
    }

    private String getKey(String prefix){
        String time = String.valueOf(System.currentTimeMillis());
        String key = EncodeUtil.encodeMD5NoE("_=" + time);
        if (key == null) {
            key = time;
        }
        return prefix+ "_" + key;
    }


    public String upload(final String targetPath) {
        String key = getKey();
        upload(targetPath,key);
        return key;
    }

    private void upload(final String targetPath,final String key){
        MisterApi.getInstance()
                .getUploadToken()
                .observeOn(Schedulers.computation())
                .subscribe(
                        token -> {
                            uploadManager.put(
                                    ImageUtils.compressToQiniu(targetPath),
                                    key,
                                    token,
                                    new UpCompletionHandler() {
                                        @Override
                                        public void complete(String key, ResponseInfo info, JSONObject response) {
                                            LogUtil.d("uploadimg complete--");
                                        }
                                    },
                                    null
                            );
                        },
                        throwable -> {
                            upload(targetPath,key);
                            LogUtil.e("upload--",throwable);
                        }
                );
    }









}