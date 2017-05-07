package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ruiaa on 2016/11/20.
 */

public class RegisterResult {


    //仅用于判断是否需要邀请码
    @SerializedName("flag")
    public boolean needInviteCode;

    //仅用于验证邀请码
    @SerializedName(value = "invite_uid",alternate = "uid")
    public String inviterUid;

    //手机  手机+密码  资料
    @SerializedName("result")
    public boolean result;
}
