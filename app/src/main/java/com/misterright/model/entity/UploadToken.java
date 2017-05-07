package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ruiaa on 2016/11/6.
 */

public class UploadToken {
    @SerializedName("result") public boolean result;
    @SerializedName("token") public String token;
}
