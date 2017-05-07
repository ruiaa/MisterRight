package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;
import com.misterright.http.MisterApi;

import java.util.Date;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class UserInfo {

    @SerializedName("name")
    public String name;
    @SerializedName("uid")
    public long uid;
    @SerializedName("hx_user")
    public String hxUser;

    @SerializedName("sex")
    public boolean sex;
    @SerializedName("job")
    public String job;
    @SerializedName("brithday")
    public Date birthday;


    @SerializedName("map_info")
    public MapInfo mapInfo;


    public String getHeadUrl(){
        return mapInfo.headUrl;
    }

    public String getCompleteHeadUrl(){
        return MisterApi.completeQiniuUrl(getHeadUrl());
    }
}
