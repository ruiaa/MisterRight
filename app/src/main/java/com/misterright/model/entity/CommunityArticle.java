package com.misterright.model.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.util.EncodeUtil;
import com.misterright.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by ruiaa on 2016/11/24.
 */

public class CommunityArticle {

    @SerializedName("ID")
    public String docId;
    @SerializedName("Topic")
    public String topic;
    @SerializedName("TimeStamp")
    public long timeStamp;
    @SerializedName("ReplyCount")
    public int replyCount;

    @SerializedName("TopicHead")
    public String topicHeadEncode;
    transient public String topicHeadDecode = null;
    @SerializedName("Abstract")
    public String outlineEncode;
    transient public ArticleContent outlineDecode = null;
    @SerializedName("Detail")
    public String detailEncode;
    transient public ArticleContent detailDecode = null;

    @SerializedName("SendUID")
    public String sendUid;
    @SerializedName("Sex")
    public boolean sex;
    @SerializedName("ShowName")
    public String showName;
    @SerializedName("ShowHeadURL")
    public String showHeadUrl;


    transient public String waitingToSendComment = null;

    public CommunityArticle(){

    }

    public CommunityArticle(String docId, String topic,
                            String topicHeadDecode, ArticleContent outlineDecode, ArticleContent detailDecode) {
        this.docId = docId;
        this.topic = topic;
        timeStamp=System.currentTimeMillis()/1000;
        replyCount=0;

        this.topicHeadDecode = topicHeadDecode;
        this.outlineDecode = outlineDecode;
        this.detailDecode = detailDecode;
        timeStamp=System.currentTimeMillis()/1000;


        sendUid= String.valueOf(MisterData.getInstance().globalInfo.userInfo.uid);
        sex=MisterData.getInstance().globalInfo.userInfo.sex;
        showName=MisterData.getInstance().globalInfo.userInfo.mapInfo.baseInfo.getNicknameStr();
        showHeadUrl=MisterData.getInstance().globalInfo.userInfo.getCompleteHeadUrl();
    }



    public CommunityArticle parse() {
        if (topicHeadEncode != null && topicHeadDecode == null) {
            topicHeadDecode = EncodeUtil.decodeBase64(topicHeadEncode);
        }
        if (outlineEncode != null && outlineDecode == null && !outlineEncode.equals("abstract")) {
            outlineDecode = ArticleContent.parseArticleFromBase64(outlineEncode);
        }
        if (detailEncode != null && detailDecode == null) {
            detailDecode = ArticleContent.parseArticleFromBase64(detailEncode);
        }
        if (showHeadUrl != null) {
            showHeadUrl = MisterApi.completeQiniuUrl(showHeadUrl);
        }
        return this;
    }

    public String getImg(int p) {
        if (outlineDecode == null || outlineDecode.pictures == null || outlineDecode.pictures.size() <= p) {
            return null;
        } else {
            return outlineDecode.pictures.get(p);
        }
    }

    public int getImgCount() {
        if (outlineDecode == null || outlineDecode.pictures == null) {
            return 0;
        } else {
            return outlineDecode.pictures.size();
        }
    }

    public static class Pck {
        @SerializedName("topic")
        public ArrayList<CommunityArticle> articleArrayList;

        public Pck parse() {
            if (articleArrayList != null) {
                for (CommunityArticle communityArticle : articleArrayList) {
                    communityArticle.parse();
                }
            }
            return this;
        }
    }

    public static class ArticleContent {
        @SerializedName("content")
        public String text;
        @SerializedName("pictures")
        public ArrayList<String> pictures;

        public ArticleContent(){

        }

        public ArticleContent(ArrayList<String> pictures, String text) {
            this.pictures = pictures;
            this.text = text;
        }

        public static ArticleContent parseArticleFromBase64(String base64) {
            if (base64 == null || base64.isEmpty()) return new ArticleContent();

            ArticleContent articleContent = null;
            try {
                Gson gson = new Gson();
                articleContent = gson.fromJson(EncodeUtil.decodeBase64(base64), ArticleContent.class);
            } catch (Exception e) {
                LogUtil.e("parseArticleFromBase64--", e);
            }

            if (articleContent != null && articleContent.pictures != null) {
                ArrayList<String> completeUrl = new ArrayList<>();
                for (String s : articleContent.pictures) {
                    completeUrl.add(MisterApi.completeQiniuUrl(s));
                }
                articleContent.pictures = completeUrl;
            }
            return articleContent;
        }

        public static String encode(String text, ArrayList<String> imgs) {
            ArticleContent articleContent = new ArticleContent();
            articleContent.text = text;
            articleContent.pictures = imgs;
            Gson gson = new Gson();
            return EncodeUtil.encodeBase64(gson.toJson(articleContent));
        }
    }

}
