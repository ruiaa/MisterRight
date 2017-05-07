package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ruiaa on 2016/11/6.
 */

public class MisterNotePck {
    @SerializedName("data") public List<MisterNote> data;
    @SerializedName("result") public boolean result;
}
