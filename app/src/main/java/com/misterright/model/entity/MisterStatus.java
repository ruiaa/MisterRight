package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ruiaa on 2016/10/30.
 */

public class MisterStatus {

    public static final String MEET_SELF="self_status";
    public static final String MEET_JOIN="join_status";
    public static final String MEET_MATCH="match_status";


    public static final String MEET="meet_status";
    public static final String KNOW="know_status";
    public static final String LOVE="love_status";

    //meet_status
    //know_status
    //love_status
    @SerializedName("page_status")
    public String pageStatus;

    //self_status
    //join_status
    //match_status
    @SerializedName("current_status")
    public String meetStatus;




    @SerializedName("time_left_count")
    public long leftTime;



    //相遇 等待 显示self
    //下一次开始时间
    @SerializedName("next_meet_time")
    public long nextTime;


    //相遇 加入
    @SerializedName("join_count")
    public int joinCount ;
    @SerializedName("success_count")
    public int successCount ;

    //相遇 匹配
    @SerializedName("info_list")
    public List<UserInfo> matchUserInfos;

    //相识
    //相恋
    @SerializedName("like_pair")
    public PairInfo pairInfo ;



    public static class PairInfo{
        @SerializedName("create_time")
        public long startTime;
        @SerializedName("uid")
        public String selfUid;
        @SerializedName("target_uid")
        public String targetUid;
    }

    public String getMatchHeadUrl(){
        return matchUserInfos.get(0).getHeadUrl();
    }

    public UserInfo getMatchUserInfo(int p){
        if (matchUserInfos==null||matchUserInfos.isEmpty()) return null;
        if (matchUserInfos.size()<=p){
            return matchUserInfos.get(0);
        }else {
            return matchUserInfos.get(p);
        }
    }

}
