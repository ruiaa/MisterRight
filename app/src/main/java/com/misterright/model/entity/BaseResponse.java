package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class BaseResponse<T> {
    @SerializedName("code") public int code;
    @SerializedName("data") public T data;
}
