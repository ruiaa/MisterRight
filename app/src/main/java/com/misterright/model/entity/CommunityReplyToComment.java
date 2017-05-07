package com.misterright.model.entity;

import com.google.gson.annotations.SerializedName;
import com.misterright.R;
import com.misterright.model.MisterData;
import com.misterright.util.EncodeUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.StringStyles;

import java.util.ArrayList;

/**
 * Created by ruiaa on 2016/11/26.
 */

public class CommunityReplyToComment {

    /*
     "id": 1,
     "topic_doc_id": 1,
     "reply_id": 1,
     "time_stamp": 1478331140,
     "send_uid": 10001,
     "sex": false,
     "content": "content",
     "show_name": "show_name",
     "show_head_url": "",
     "by_reply_id": 0,
     "by_reply_name": "",
     "by_reply_sex": false
     */

    @SerializedName("id")
    public String replyId;
    @SerializedName("topic_doc_id")
    public String docId;
    @SerializedName("reply_id")
    public String commentId;
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

    @SerializedName("by_reply_id")
    public String byReplyId;
    @SerializedName("by_reply_name")
    public String byReplyName;
    @SerializedName("by_reply_sex")
    public boolean byReplySex;

    transient public String waitingToSendReply = null;

    public CommunityReplyToComment() {

    }

    public CommunityReplyToComment(String replyId, String docId, String commentId, String contentDecode) {
        this.replyId = replyId;
        this.docId = docId;
        this.commentId = commentId;
        timeStamp = System.currentTimeMillis() / 1000;

        this.contentDecode = contentDecode;
        contentEncode = "";

        sendUid = String.valueOf(MisterData.getInstance().globalInfo.userInfo.uid);
        sex = MisterData.getInstance().globalInfo.userInfo.sex;
        showName = MisterData.getInstance().globalInfo.userInfo.mapInfo.baseInfo.getNicknameStr();
        showHeadUrl = MisterData.getInstance().globalInfo.userInfo.getCompleteHeadUrl();
    }

    public CommunityReplyToComment(String replyId, String docId, String commentId, String contentDecode,
                                   String byReplyName, String byReplyId, boolean byReplySex) {
        this(replyId,docId,commentId,contentDecode);
        this.byReplyName = byReplyName;
        this.byReplyId = byReplyId;
        this.byReplySex = byReplySex;
    }

    public static class Pck {
        @SerializedName("reply")
        public ArrayList<CommunityReplyToComment> replyToCommentArrayList;

        public Pck parse() {
            if (replyToCommentArrayList != null) {
                for (CommunityReplyToComment replyToComment : replyToCommentArrayList) {
                    replyToComment.parse();
                }
            }
            return this;
        }
    }

    public CommunityReplyToComment parse() {
        if (contentEncode != null) {
            contentDecode = EncodeUtil.decodeBase64(contentEncode);
        }
        return this;
    }

    public CharSequence getReplyText() {
        StringStyles.RichText richText = new StringStyles.RichText();
        richText.appendColorId(showName, sex ? R.color.text_blue : R.color.text_red);
        if (byReplyName != null && !byReplyName.isEmpty()) {
            richText.append(" " + ResUtil.getString(R.string.reply) + " ");
            richText.appendColorId(byReplyName, byReplySex ? R.color.text_blue : R.color.text_red);
        }
        richText.append(" :    ");
        richText.append(contentDecode);
        return richText.build();
    }

}
