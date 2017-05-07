package com.misterright.model.entity;

import android.databinding.ObservableInt;
import android.support.v7.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.ui.adapter.SimpleRecyclerAdapter;
import com.misterright.util.EncodeUtil;

import java.util.ArrayList;

/**
 * Created by ruiaa on 2016/11/26.
 */

public class CommunityComment {

    /*

    "id": 4,
    "topic_doc_id": 1,
    "time_stamp": 1478331390,
    "send_uid": 10002,
    "sex": true,
    "content": "content",
    "reply_count": 0,
    "show_name": "show_name",
    "show_head_url": "",
    "reply_list": null

     */
    @SerializedName("id")
    public String commentId;
    @SerializedName("topic_doc_id")
    public String docId;
    @SerializedName("time_stamp")
    public long timeStamp;

    @SerializedName("content")
    public String contentEncode;
    transient public String contentDecode = null;

    @SerializedName("send_uid")
    public String sendUid;
    @SerializedName("sex")
    public boolean sex;
    @SerializedName("show_name")
    public String showName;
    @SerializedName("show_head_url")
    public String showHeadUrl;

    @SerializedName("reply_count")
    public int replyCount;
    @SerializedName("reply_list")
    public ArrayList<CommunityReplyToComment> replyList;

    transient public String waitingToSendReply = null;
    transient public SimpleRecyclerAdapter<CommunityReplyToComment> adapter = null;
    transient public RecyclerView recyclerView = null;

    public CommunityComment(){

    }

    public CommunityComment(String commentId, String docId, String contentDecode) {
        this.commentId = commentId;
        this.docId = docId;
        this.contentDecode = contentDecode;
        this.contentEncode="";

        timeStamp=System.currentTimeMillis()/1000;
        replyCount=0;
        replyList=new ArrayList<>();

        sendUid= String.valueOf(MisterData.getInstance().globalInfo.userInfo.uid);
        sex=MisterData.getInstance().globalInfo.userInfo.sex;
        showName=MisterData.getInstance().globalInfo.userInfo.mapInfo.baseInfo.getNicknameStr();
        showHeadUrl=MisterData.getInstance().globalInfo.userInfo.getCompleteHeadUrl();
    }

    public static class Pck {
        @SerializedName("reply")
        public ArrayList<CommunityComment> commentArrayList;

        public Pck parse() {
            if (commentArrayList != null) {
                for (CommunityComment comment : commentArrayList) {
                    comment.parse();
                }
            }
            return this;
        }
    }

    public CommunityComment parse() {
        if (contentEncode != null) {
            contentDecode = EncodeUtil.decodeBase64(contentEncode);
        }
        if (showHeadUrl != null) {
            showHeadUrl = MisterApi.completeQiniuUrl(showHeadUrl);
        }
        if (replyList != null) {
            for (CommunityReplyToComment replyToComment : replyList) {
                replyToComment.parse();
            }
        }
        return this;
    }

    public ObservableInt leftReplyCount = new ObservableInt();

    private int getLeftReplyCount() {
        if (replyList == null) {
            return 0;
        } else {
            return replyCount - replyList.size();
        }
    }

    public void refreshLeftReplyCount() {
        leftReplyCount.set(getLeftReplyCount());
    }

}
