package com.misterright.http;

import com.misterright.model.entity.MapInfo;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by ruiaa on 2016/10/30.
 */

public interface MisterBaseApiResponse {

    //www.gogodata.cn:9001

    //所有使用签名
    // +time +sign

    //密码登录
    @FormUrlEncoded
    @POST("user/loginUsePwd/pwd")
    Observable<ResponseBody> loginUsePwd(
            @Field("phone_num") String phone,
            @Field("pwd") String pwd,
            @Field("_") String time,
            @Field("sign") String sign
    );

    //token登录
    @FormUrlEncoded
    @POST("user/loginUsePwd/token")
    Observable<ResponseBody> loginUseToken(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("_") String time,
            @Field("sign") String sign
    );

    //获取其他用户
    @GET("user/info")
    Observable<ResponseBody> getOtherUserInfo(
            @Query("phone_num") String phone,
            @Query("token") String token,
            @Query("target_uid") String targetUid,
            @Query("_") String time,
            @Query("sign") String sign
    );

    //更改个人信息
    @FormUrlEncoded
    @POST("main/page/status")
    Observable<ResponseBody> setInfo(
            @Field("phone_num") String phone,
            @Field("token") String token,
            @Field("update_info")MapInfo mapInfo,
            @Field("_") String time,
            @Field("sign") String sign
    );



    //获取状态
    @GET("main/page/status")
    Observable<ResponseBody> getStatus(
            @Query("phone_num") String phone,
            @Query("token") String token,
            @Query("_") String time,
            @Query("sign") String sign
    );

    @GET("main/page/status")
    Observable<ResponseBody> getStatus(@QueryMap Map<String,String> fields);

    @GET("main/page/status")
    Call<ResponseBody> getStatusSync(@QueryMap Map<String,String> fields);

}
