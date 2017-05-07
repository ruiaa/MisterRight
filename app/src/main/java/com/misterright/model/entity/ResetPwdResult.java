package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ruiaa on 2016/11/18.
 */

public class ResetPwdResult {
    @SerializedName("hx_pwd") public String hxPwd;
    @SerializedName("hx_user") public String hxUser;
    @SerializedName("result") public boolean result;
}
