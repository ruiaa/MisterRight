package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;
import com.misterright.http.MisterApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiaa on 2016/11/5.
 */

public class MisterNote {
    @SerializedName("content") public String content;
    @SerializedName("pictures") public List<String> pictures;
    @SerializedName("send_uid") public long sendUid;
    @SerializedName("time_stamp")public long time;

    private ArrayList<String> completePictUrl;


    public MisterNote(String content, List<String> pictures) {
        this.content = content;
        this.pictures = pictures;
    }

    public boolean isEmpty(){
        if ((content==null||content.isEmpty())&&(pictures==null||pictures.size()==0)) {
            return true;
        }else {
            return false;
        }
    }

    public ArrayList<String> getCompletePictUrl(){
        if (completePictUrl==null){
           completePictUrl= MisterApi.getCompleteQiniuUrl(pictures);
        }
        return completePictUrl;
    }

}
