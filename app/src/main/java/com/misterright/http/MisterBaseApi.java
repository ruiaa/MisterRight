package com.misterright.http;

import com.misterright.model.entity.ActionResult;
import com.misterright.model.entity.BaseResponse;
import com.misterright.model.entity.GlobalInfo;
import com.misterright.model.entity.MisterNotePck;
import com.misterright.model.entity.MisterStatus;
import com.misterright.model.entity.UploadToken;
import com.misterright.model.entity.UserInfo;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by ruiaa on 2016/10/29.
 */

public interface MisterBaseApi {

    //www.gogodata.cn:9001

    //所有使用签名
    // +time +sign

    //密码登录
    @FormUrlEncoded
    @POST("user/login/pwd")
    Observable<BaseResponse<GlobalInfo>> loginUsePwd(
            @Field("phone_num") String phone,
            @Field("pwd") String pwd,
            @Field("_") String time,
            @Field("sign") String sign
    );

    @FormUrlEncoded
    @POST("user/login/pwd")
    Observable<BaseResponse<GlobalInfo>> loginUsePwd(@FieldMap Map<String,String> fields);

    //token登录
    @FormUrlEncoded
    @POST("user/login/token")
    Observable<BaseResponse<GlobalInfo>> loginUseToken(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("_") String time,
            @Field("sign") String sign
    );

    @FormUrlEncoded
    @POST("user/login/token")
    Observable<BaseResponse<GlobalInfo>> loginUseToken(@FieldMap Map<String,String> fields);

    //获取其他用户
    @GET("user/info")
    Observable<BaseResponse<UserInfo>> getOtherUserInfo(
            @Query("phone_num") String phone,
            @Query("token") String token,
            @Query("target_uid") String targetUid,
            @Query("_") String time,
            @Query("sign") String sign
    );

    @GET("user/info")
    Observable<BaseResponse<UserInfo>> getOtherUserInfo(@QueryMap Map<String,String> fields);


    //更改个人信息
    @FormUrlEncoded
    @POST("user/info")
    Observable<ResponseBody> setInfo(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("update_info")String mapInfo, // json  base64
            @Field("_") String time,
            @Field("sign") String sign
    );

    @FormUrlEncoded
    @POST("user/info")
    Observable<ResponseBody> setInfo(@FieldMap Map<String,String> fields);



    //获取状态
    @GET("main/page/status")
    Observable<BaseResponse<MisterStatus>> getStatus(
            @Query("phone_num") String phone,
            @Query("token") String token,
            @Query("_") String time,
            @Query("sign") String sign
    );

    @GET("main/page/status")
    Observable<BaseResponse<MisterStatus>> getStatus(@QueryMap Map<String,String> fields);

    @GET("main/page/status")
    Call<BaseResponse<MisterStatus>> getStatusSync(@QueryMap Map<String,String> fields);

    //加入匹配
    @FormUrlEncoded
    @POST("activities/match/join")
    Observable<BaseResponse<ActionResult>> matchJoin(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("_") String time,
            @Field("sign") String sign
    );
    @FormUrlEncoded
    @POST("activities/match/join")
    Observable<BaseResponse<ActionResult>> matchJoin(@FieldMap Map<String,String> fields);


    //匹配 喜欢
    @FormUrlEncoded
    @POST("activities/match/like")
    Observable<BaseResponse<ActionResult>> matchLike(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("target_uid") long targetUid,
            @Field("_") String time,
            @Field("sign") String sign
    );
    @FormUrlEncoded
    @POST("activities/match/like")
    Observable<BaseResponse<ActionResult>> matchLike(@FieldMap Map<String,String> fields);

    //不喜欢
    @FormUrlEncoded
    @POST("activities/match/dislike")
    Observable<BaseResponse<ActionResult>> matchDislike(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("target_uid") long targetUid,
            @Field("_") String time,
            @Field("sign") String sign
    );
    @FormUrlEncoded
    @POST("activities/match/dislike")
    Observable<BaseResponse<ActionResult>> matchDislike(@FieldMap Map<String,String> fields);

    //删除关系
    @FormUrlEncoded
    @POST("relation/delete")
    Observable<BaseResponse<ActionResult>> relationDelete(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("target_uid") long targetUid,
            @Field("_") String time,
            @Field("sign") String sign
    );
    @FormUrlEncoded
    @POST("relation/delete")
    Observable<BaseResponse<ActionResult>> relationDelete(@FieldMap Map<String,String> fields);

    //笔记
    @FormUrlEncoded
    @POST("personal/note")
    Observable<BaseResponse<ActionResult>> postNote(
            @Field("phone_num") String phone,
            @Field("token") String token,

            @Field("note") String note, // json  base64

            @Field("_") String time,
            @Field("sign") String sign
    );

    @FormUrlEncoded
    @POST("personal/note")
    Observable<BaseResponse<ActionResult>> postNote(@FieldMap Map<String,String> fields);

    @GET("personal/note")
    Observable<BaseResponse<MisterNotePck>> getNote(
            @Query("phone_num") String phone,
            @Query("token") String token,

            @Query("end_time") long endTime,  // 时间戳 秒

            @Query("_") String time,
            @Query("sign") String sign
    );

    @GET("personal/note")
    Observable<BaseResponse<MisterNotePck>> getNote(@QueryMap Map<String,String> querys);


    /*
     * 图片
     */
    @GET("picture/upload/token")
    Observable<BaseResponse<UploadToken>> getUploadToken (@QueryMap Map<String,String> standardQuery);


    /*    //获取图片
    @GET("picture/download/url")
    Observable<ResponseBody> getPicture(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("pic_names") String picNames,
            @Field("_") String time,
            @Field("sign") String sign
    );

    //更新图片token
    @GET("picture/upload/token")
    Observable<ResponseBody> uploadPictureToken(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("_") String time,
            @Field("sign") String sign
    );

    @GET("picture/upload/token")
    Observable<ResponseBody> uploadPictureToken(@QueryMap Map<String,String> fields);*/





    //@Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileRemoteUrl);
}
