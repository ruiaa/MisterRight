package com.misterright.http;

import com.misterright.model.entity.BaseResponse;
import com.misterright.model.entity.CommunityArticle;
import com.misterright.model.entity.CommunityComment;
import com.misterright.model.entity.CommunityPostResult;
import com.misterright.model.entity.CommunityReplyToComment;
import com.misterright.model.entity.CommunityTopic;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by ruiaa on 2016/11/16.
 */

public interface MisterCommunityApi {

    // www.gogodata.cn:10010/

    @GET("home/topic")
    Observable<BaseResponse<CommunityTopic>> getTopic(
            @QueryMap Map<String, String> fields);

    @GET("page/{topic}/hot")
//"count"  "end_time"
    Observable<BaseResponse<CommunityArticle.Pck>> getHot(
            @Path("topic") String topic,
            @QueryMap Map<String, String> fields);


    @GET("page/{topic}/new")
//"count"  "end_time"
    Observable<BaseResponse<CommunityArticle.Pck>> getNew(
            @Path("topic") String topic,
            @QueryMap Map<String, String> fields);


    @GET("community/doc/{topic}/{id}")
    Observable<BaseResponse<CommunityArticle>> getArticle(
            @Path("topic") String topic,
            @Path("id") String id,
            @QueryMap Map<String, String> fields);

    @GET("doc/reply/{topic}/{doc_id}")
        //"count"  "end_time"
    Observable<BaseResponse<CommunityComment.Pck>> getComment(
            @Path("topic") String topic,
            @Path("doc_id") String docId,
            @QueryMap Map<String, String> fields);


    @GET("reply/reply/{topic}/{doc_id}/{reply_id}")
        //"count"  "end_time"
    Observable<BaseResponse<CommunityReplyToComment.Pck>> getReplayToComment(
            @Path("topic") String topic,
            @Path("doc_id") String docId,
            @Path("reply_id") String commentId,
            @QueryMap Map<String, String> fields);


    //      发文章
    @FormUrlEncoded
    @POST("community/append/doc/{topic}")
    Observable<BaseResponse<CommunityPostResult>> postArticle(
            @Path("topic") String topic,
            @Field("topic_head") String title,      //base64
            @Field("abstract") String outline,      //json  base64
            @Field("detail") String detail,         //json  base64
            @Field("show_name") String showName,
            @Field("show_url") String showUrl,

            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("_") String time,
            @Field("sign") String sign
    );

    @FormUrlEncoded
    @POST("community/append/doc/{topic}")
    Observable<BaseResponse<CommunityPostResult>> postArticle(
            @Path("topic") String topic,
            @FieldMap Map<String,String> fields
            );

    //      发评论
    @FormUrlEncoded
    @POST("doc/reply/{doc_id}")
    Observable<BaseResponse<CommunityPostResult>> postComment(
            @Path("doc_id") String docId,
            @Field("content") String text,          //base64
            @Field("show_name") String showName,
            @Field("show_url") String showUrl,

            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("_") String time,
            @Field("sign") String sign
    );

    @FormUrlEncoded
    @POST("doc/reply/{doc_id}")
    Observable<BaseResponse<CommunityPostResult>> postComment(
            @Path("doc_id") String docId,
            @FieldMap Map<String,String> fields
    );

    //      回复评论
    @FormUrlEncoded
    @POST("reply/reply/{doc_id}/{reply_id}")
    Observable<BaseResponse<CommunityPostResult>> postReplyToComment(
            @Path("doc_id") String docId,
            @Path("reply_id") String commentId,
            @Field("content") String text,          //base64
            @Field("show_name") String showName,
            @Field("show_url") String showUrl,
            @Field("by_reply_id") String by_reply_id,

            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("_") String time,
            @Field("sign") String sign
    );

    @FormUrlEncoded
    @POST("reply/reply/{doc_id}/{reply_id}")
    Observable<BaseResponse<CommunityPostResult>> postReplyToComment(
            @Path("doc_id") String docId,
            @Path("reply_id") String replyId,
            @FieldMap Map<String,String> fields
    );


}
