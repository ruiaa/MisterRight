package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class GlobalInfo {

    @SerializedName("hx_user")
    public String hxUser;
    @SerializedName("hx_pwd")
    public String hxPwd;
    @SerializedName("token")
    private String token;
    @SerializedName("available_time")
    public long tokenTime;
    @SerializedName("result")
    public boolean result;
    @SerializedName("user_info")
    public UserInfo userInfo;

    public String getToken() {
        return token;
    }
}
