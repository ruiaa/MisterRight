package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ruiaa on 2016/11/23.
 */

public class CommunityTopic {
    @SerializedName("head_list")
    public ArrayList<TopicItem> headList;
    @SerializedName("topic_list")
    public ArrayList<TopicItem> topicList;

    public static class TopicItem{
        @SerializedName(value = "head_type",alternate = "topic_type")
        public String type;
        @SerializedName(value = "head_url",alternate = "topic_url")
        public String url;
        @SerializedName(value = "head_content",alternate = "topic_content")
        public String content;
    }
}
