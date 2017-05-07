package com.misterright.http;


import com.misterright.model.entity.BaseResponse;
import com.misterright.model.entity.RegisterResult;
import com.misterright.model.entity.ResetPwdResult;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by ruiaa on 2016/11/3.
 */

public interface MisterRegisterApi {

    //172.30.16.173


    // www.gogodata.cn:8081/

    //测试
    @GET("manager/start")
    Observable<ResponseBody> tryMatchStart(@Query("_") String time, @Query("sign") String sign);

    @GET("manager/start")
    Observable<ResponseBody> tryMatchStart(@QueryMap Map<String,String> fields);

    @GET("manager/stop")
    Observable<ResponseBody> tryMatchStop(@Query("_") String time, @Query("sign") String sign);

    @GET("manager/stop")
    Observable<ResponseBody> tryMatchStop(@QueryMap Map<String,String> fields);

    @GET("manager/join_all")
    Observable<ResponseBody> tryMatchJoinAll(@Query("_") String time, @Query("sign") String sign);

    @GET("manager/join_all")
    Observable<ResponseBody> tryMatchJoinAll(@QueryMap Map<String,String> fields);


    //注册
    @GET("register/invite_check")
    Observable<BaseResponse<RegisterResult>> checkNeedInviteCode(
            @Query("_") String time,
            @Query("sign") String sign);

    @GET("register/invite_check")
    Observable<BaseResponse<RegisterResult>> checkNeedInviteCode(@QueryMap Map<String,String> fields);

    @FormUrlEncoded
    @POST("register/invite_check")
    Observable<BaseResponse<RegisterResult>> checkInviteCode(
            @Field("invite_code") String code,
            @Field("_") String time,
            @Field("sign") String sign);

    @FormUrlEncoded
    @POST("register/invite_check")
    Observable<BaseResponse<RegisterResult>> checkInviteCode(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("register/phone_code")
    Observable<BaseResponse<RegisterResult>> getValidCode(
            @Field("phone_num") String phone,
            @Field("_") String time,
            @Field("sign") String sign);

    @FormUrlEncoded
    @POST("register/phone_code")
    Observable<BaseResponse<RegisterResult>> getValidCode(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("register/user")
    Observable<BaseResponse<RegisterResult>> registerUser(
            @Field("phone_num") String phone,
            @Field("valid_code") String validCode,
            @Field("pwd") String pwd,
            @Field("invite_uid") String inviteUid,
            @Field("_") String time,
            @Field("sign") String sign);

    @FormUrlEncoded
    @POST("register/user")
    Observable<BaseResponse<RegisterResult>> registerUser(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("register/base_info")
    Observable<BaseResponse<RegisterResult>> registerBaseinfo(
            @Field("phone_num") String phone,
            @Field("pwd") String pwd,
            @Field("id_card_url") String idCard,
            @Field("birtyday") String birthday,
            @Field("sex") boolean sex,
            @Field("name") String name,
            @Field("job") String job,
            @Field("map_info") String mapInfo,
            @Field("_") String time,
            @Field("sign") String sign);

    @FormUrlEncoded
    @POST("register/base_info")
    Observable<BaseResponse<RegisterResult>> registerBaseinfo(@FieldMap Map<String, String> fields);

    /*
     *
     */
    //反馈
    @FormUrlEncoded
    @POST("advice")
    Observable<ResponseBody> feedback(
            @Field("send_uid") String sendUid,
            @Field("message") String message,
            @Field("_") String time,
            @Field("sign") String sign);

    @FormUrlEncoded
    @POST("advice")
    Observable<ResponseBody> feedback(@FieldMap Map<String, String> fields);

    /*
     *
     *  修改密码
     *
     */
    @FormUrlEncoded
    @POST("user/alter/pwd")
    Observable<BaseResponse<ResetPwdResult>> resetPwd(
            @Field("phone_num") String phone,
            @Field("pwd") String pwd,
            @Field("new_pwd") String newPwd,
            @Field("_") String time,
            @Field("sign") String sign);

    @FormUrlEncoded
    @POST("user/alter/pwd")
    Observable<BaseResponse<ResetPwdResult>> resetPwd(@FieldMap Map<String, String> fields);

    @GET("pwd/forget/msg_code")
    Observable<BaseResponse<ResetPwdResult>> forgetToResetPwdAskValid(
            @Field("phone_num") String phone,
            @Field("_") String time,
            @Field("sign") String sign);

    @GET("pwd/forget/msg_code")
    Observable<BaseResponse<ResetPwdResult>> forgetToResetPwdAskValid(@QueryMap Map<String,String> fields);

    @GET("pwd/forget/msg_code")
    Observable<BaseResponse<ResetPwdResult>> forgetToResetPwd(
            @Field("phone_num") String phone,
            @Field("valid_code") String valid,
            @Field("new_pwd") String newPwd,
            @Field("_") String time,
            @Field("sign") String sign);

    @GET("pwd/forget/msg_code")
    Observable<BaseResponse<ResetPwdResult>> forgetToResetPwd(@QueryMap Map<String,String> fields);

}
