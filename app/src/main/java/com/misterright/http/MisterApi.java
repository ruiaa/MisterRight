
package com.misterright.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.misterright.model.MisterData;
import com.misterright.model.entity.ActionResult;
import com.misterright.model.entity.CommunityArticle;
import com.misterright.model.entity.CommunityComment;
import com.misterright.model.entity.CommunityPostResult;
import com.misterright.model.entity.CommunityReplyToComment;
import com.misterright.model.entity.CommunityTopic;
import com.misterright.model.entity.GlobalInfo;
import com.misterright.model.entity.MapInfo;
import com.misterright.model.entity.MisterNote;
import com.misterright.model.entity.MisterStatus;
import com.misterright.model.entity.RegisterResult;
import com.misterright.model.entity.ResetPwdResult;
import com.misterright.model.entity.UserInfo;
import com.misterright.util.EncodeUtil;
import com.misterright.util.LogUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.bus.PostCommunityArticleEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.storage.FileStorage;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class MisterApi {

    public static final String BASE_HOST = "http://www.gogodata.cn:9001/";
    public static final String REGISTER_HOST = "http://gogodata.cn:8081/";
    public static final String COMMUNITY_HOSE = "http://www.gogodata.cn:10010/";
    public static final String QINIU_BASE_URL = "http://odqg0uoa0.bkt.clouddn.com/";

    private static MisterApi instance = null;

    private boolean isUploadingMineInfo = false;

    private MisterBaseApi misterBaseApi;
    private MisterRegisterApi misterRegisterApi;
    private MisterCommunityApi misterCommunityApi;

    private MisterApi() {
        this.misterBaseApi = getMisterApi();
        this.misterRegisterApi = getRegisterApi();
        this.misterCommunityApi = getCommunityApi();
    }

    public static String completeQiniuUrl(String s) {
        if (s == null) return null;
        if (s.contains(QINIU_BASE_URL)) return s;
        return QINIU_BASE_URL + s;
    }


    /*
     *
     * 注册
     *
     */
    public Observable<RegisterResult> checkNeedInviteCode() {
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****checkNeedInviteCode--");
                    return misterRegisterApi.checkNeedInviteCode(addTimeAndSign(new HashMap<>()));
                })
                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribeOn(Schedulers.io())
                .map(registerResultBaseResponse -> registerResultBaseResponse.data);
    }

    public Observable<RegisterResult> checkInviteCode(String inviteCode) {
        Map<String, String> map = new HashMap<>();
        map.put("invite_code", inviteCode);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****checkInviteCode--");
                    return misterRegisterApi.checkInviteCode(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribeOn(Schedulers.io())
                .map(registerResultBaseResponse -> registerResultBaseResponse.data);
    }

    public Observable<RegisterResult> getValidCode(String pho) {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", pho);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getValidCode--");
                    return misterRegisterApi.getValidCode(addTimeAndSign(map));
                })
//                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribeOn(Schedulers.io())
                .map(registerResultBaseResponse -> registerResultBaseResponse.data);
    }

    public Observable<RegisterResult> registerUser(String pho, String pwd, String validCode, String inviteUid) {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", pho);
        map.put("pwd", EncodeUtil.encodeMD5NoE(pwd));
        map.put("valid_code", validCode);
        map.put("invite_uid", inviteUid);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****registerUser--");
                    return misterRegisterApi.registerUser(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 2000))
                .subscribeOn(Schedulers.io())
                .map(registerResultBaseResponse -> registerResultBaseResponse.data);
    }

    public Observable<RegisterResult> registerBaseinfo(
            String pho, String pwd,
            String name, boolean sex, String birthday, String job,
            String school, String gradle, String idCardUrlKey) {
        MapInfo mapInfo = new MapInfo();
        mapInfo.baseInfo = new MapInfo.BaseInfo();
        mapInfo.baseInfo.school = new String[]{school};
        mapInfo.baseInfo.grade = new String[]{gradle};
        Gson gson = new Gson();
        String mStr = gson.toJson(mapInfo);
        String mapInfoBase64 = Base64.encodeToString(mStr.getBytes(), Base64.DEFAULT);

        Map<String, String> map = new HashMap<>();
        map.put("phone_num", pho);
        map.put("pwd", EncodeUtil.encodeMD5NoE(pwd));
        map.put("id_card_url", idCardUrlKey);
        map.put("birtyday", birthday);
        map.put("sex", String.valueOf(sex));
        map.put("name", name);
        map.put("job", job);
        map.put("map_info", mapInfoBase64);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****registerBaseinfo--");
                    return misterRegisterApi.registerBaseinfo(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 2000))
                .subscribeOn(Schedulers.io())
                .map(registerResultBaseResponse -> registerResultBaseResponse.data);
    }

    /*
     *
     * 登录
     *
     */
    public Observable<GlobalInfo> loginUsePwd(final String phone, final String pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", phone);
        map.put("pwd", EncodeUtil.encodeMD5NoE(pwd));
        LogUtil.i("****loginUsePwd--" + "phone" + phone + "pwd" + pwd);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****loginUsePwd--");
                    return misterBaseApi.loginUsePwd(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(4, 2000))
                .subscribeOn(Schedulers.io())
                .map(globalInfoBaseResponse -> {
                    MisterData.setGlobalInfo(globalInfoBaseResponse.data);
                    MisterData.getInstance().phone = phone;
                    MisterData.getInstance().pwd = pwd;
                    MisterData.save();
                    return globalInfoBaseResponse.data;
                });
    }

    public Observable<GlobalInfo> loginUseToken(final String phone, final String token) {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", phone);
        map.put("token", token);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****loginUseToken--");
                    return misterBaseApi.loginUseToken(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(4, 2000))
                .subscribeOn(Schedulers.io())
                .map(globalInfoBaseResponse -> {
                    MisterData.setGlobalInfo(globalInfoBaseResponse.data);
                    MisterData.save();
                    return globalInfoBaseResponse.data;
                });
    }

    public Observable<GlobalInfo> login(final String phone, final String pwd, final String token) {
        if (token == null || token.isEmpty()) {
            return loginUsePwd(phone, pwd);
        } else {
            return loginUseToken(phone, token);
        }
    }



    /*
     *
     *  修改密码
     *
     *
     */

    public Observable<ResetPwdResult> reSetPwd(String oldPwd, String newPwd) {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", MisterData.getInstance().phone);
        map.put("pwd", EncodeUtil.encodeMD5NoE(oldPwd));
        map.put("new_pwd", EncodeUtil.encodeMD5NoE(newPwd));
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****reSetPwd--");
                    return misterRegisterApi.resetPwd(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribeOn(Schedulers.io())
                .map(resetPwdResultBaseResponse -> {
                    ResetPwdResult resetPwdResult = resetPwdResultBaseResponse.data;
                    if (resetPwdResultBaseResponse.data.result) {
                        MisterData.getInstance().pwd = newPwd;
                        MisterData.getInstance().globalInfo.hxUser = resetPwdResult.hxUser;
                        MisterData.getInstance().globalInfo.hxPwd = resetPwdResult.hxPwd;
                        MisterData.save();
                    }
                    return resetPwdResult;
                });
    }

    public Observable<ResetPwdResult> forgetToResetPwd(String phone, String valid, String newPwd) {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", phone);
        map.put("valid_code", valid);
        map.put("new_pwd", EncodeUtil.encodeMD5NoE(newPwd));
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****forgetToResetPwd--");
                    return misterRegisterApi.forgetToResetPwd(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribeOn(Schedulers.io())
                .map(resetPwdResultBaseResponse -> resetPwdResultBaseResponse.data);
    }

    public Observable<ResetPwdResult> forgetToResetPwdAskValid(String phone) {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", phone);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****forgetToResetPwdAskValid--");
                    return misterRegisterApi.forgetToResetPwdAskValid(addTimeAndSign(map));
                })
//                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribeOn(Schedulers.io())
                .map(resetPwdResultBaseResponse -> resetPwdResultBaseResponse.data);
    }

    /*
     * 获取 修改 个人资料
     */
    public Observable<UserInfo> getOtherInfo(String targetUid) {
        Map<String, String> map = phoAndToken();
        map.put("target_uid", targetUid);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getOtherInfo--");
                    return misterBaseApi.getOtherUserInfo(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(3, 2000))
                .subscribeOn(Schedulers.io())
                .map(userInfoBaseResponse -> {
                    return userInfoBaseResponse.data;
                });
    }

    public Observable<UserInfo> getMineInfo() {
        return getOtherInfo(String.valueOf(MisterData.getInstance().globalInfo.userInfo.uid))
                .map(userInfo -> {
                    MisterData.getInstance().globalInfo.userInfo = userInfo;
                    return userInfo;
                });
    }

    public Observable<UserInfo> getPairUserInfo() {
        return getOtherInfo(MisterData.getInstance().status.pairInfo.targetUid)
                .map(userInfo -> {
                    MisterData.getInstance().otherUser = userInfo;
                    return userInfo;
                });
    }

    public void setPairUserInfo() {
        getOtherInfo(MisterData.getInstance().status.pairInfo.targetUid)
                .subscribe(
                        userInfo -> {
                            MisterData.getInstance().otherUser = userInfo;
                        },
                        throwable -> {
                            LogUtil.e("setPairUserInfo--", throwable);
                        });
    }

    public void reSetInfo(final MapInfo newMapInfo) {
        Gson gson = new Gson();
        String mStr = gson.toJson(newMapInfo);
        String mBase64 = Base64.encodeToString(mStr.getBytes(), Base64.DEFAULT);

        Map<String, String> map = phoAndToken();
        map.put("update_info", mBase64);
        Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****reSetInfo--");
                    return misterBaseApi.setInfo(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .delay(2000L, TimeUnit.MILLISECONDS)
                .subscribe(
                        responseBody -> {
                            MisterData.getInstance().globalInfo.userInfo.mapInfo = newMapInfo;
                            MisterData.save();
                            LogUtil.i("reSetInfo--ok");
                        },
                        throwable -> {
                            LogUtil.e("reSetInfo--", throwable);
                        }
                );
    }

    /*
     *
     *  相遇 状态
     *
     */
    public Observable<MisterStatus> getStatus() {
        Map<String, String> map = phoAndToken();
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getStatus--");
                    return misterBaseApi.getStatus(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(3, 2000))
                .subscribeOn(Schedulers.io())
                .map(statusBaseResponse -> {
                    MisterData.getInstance().status = statusBaseResponse.data;
                    MisterData.save();
                    return statusBaseResponse.data;
                });

    }

    public MisterStatus getStatusSync() {
        Map<String, String> map = phoAndToken();
        try {
            return misterBaseApi.getStatusSync(addTimeAndSign(map)).execute().body().data;
        } catch (Exception e) {
            LogUtil.e("getStatusSync--", e);
        }
        return null;
    }

    public Observable<ActionResult> matchJoin() {
        Map<String, String> map = phoAndToken();
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****matchJoin--");
                    return misterBaseApi.matchJoin(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .map(actionResultBaseResponse -> actionResultBaseResponse.data);
    }

    public Observable<ActionResult> matchLike(String targetUid) {
        Map<String, String> map = phoAndToken();
        map.put("target_uid", targetUid);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****matchLike--");
                    return misterBaseApi.matchLike(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .map(actionResultBaseResponse -> actionResultBaseResponse.data);
    }

    public Observable<ActionResult> matchDislike(String targetUid) {
        Map<String, String> map = phoAndToken();
        map.put("target_uid", targetUid);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****matchDislike--");
                    return misterBaseApi.matchDislike(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .map(actionResultBaseResponse -> actionResultBaseResponse.data);
    }

    public Observable<ActionResult> relationDelete(String targetUid) {
        Map<String, String> map = phoAndToken();
        map.put("target_uid", targetUid);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****relationDelete--");
                    return misterBaseApi.relationDelete(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .map(actionResultBaseResponse -> actionResultBaseResponse.data);
    }

    public void tryStartMatch() {
        Map<String, String> map = new HashMap<>();
        Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****tryStartMatch--");
                    return misterRegisterApi.tryMatchStart(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(3, 2000))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(responseBody -> {
                    LogUtil.d("****tryStartMatchJoinall--");
                    return misterRegisterApi.tryMatchJoinAll(addTimeAndSign(map));
                })
                .subscribe(
                        responseBody -> {

                        }, throwable -> {
                            LogUtil.e("tryStartMatch--", throwable);
                            ToastUtil.showShort("失败");
                        }
                );
    }

    public void tryStopMatch() {
        Map<String, String> map = new HashMap<>();
        Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****tryStopMatch--");
                    return misterRegisterApi.tryMatchStop(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(3, 3000))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        responseBody -> {

                        }, throwable -> {
                            LogUtil.e("tryStartMatch--", throwable);
                            ToastUtil.showShort("失败");
                        }
                );
    }

    /*
     *
     * 笔记
     *
     */
    public Observable<List<MisterNote>> getNote(long endTime) {
        Map<String, String> map = phoAndToken();
        map.put("end_time", String.valueOf(endTime));
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getNote--");
                    return misterBaseApi.getNote(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .map(misterNotePckBaseResponse -> misterNotePckBaseResponse.data.data)
                .observeOn(Schedulers.computation())
                .map(misterNotes -> {
                    for (MisterNote note : misterNotes) {
                        if (note.content != null) {
                            note.content = EncodeUtil.decodeBase64(note.content);
                        }
                    }
                    return misterNotes;
                });
    }

    public Observable<List<MisterNote>> getNote() {
        return getNote(0);
    }

    public Observable<ActionResult> sendNote(@NonNull MisterNote note) {
/*        if (note==null) return;
        if (note.isEmpty()) return;*/

        Gson gson = new Gson();
        if (note.content != null) {
            note.content = EncodeUtil.encodeBase64(note.content);
        }
        String nStr = gson.toJson(note);
        String nBase64 = Base64.encodeToString(nStr.getBytes(), Base64.DEFAULT);

        Map<String, String> map = phoAndToken();
        map.put("note", nBase64);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****sendNote--");
                    return misterBaseApi.postNote(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .map(actionResultBaseResponse -> actionResultBaseResponse.data);
    }

    /*
     *
     *  图片上传
     *
     */
    public Observable<String> getUploadToken() {
        Map<String, String> map = phoAndToken();
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getUploadToken--");
                    return misterBaseApi.getUploadToken(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 3000))
                .subscribeOn(Schedulers.io())
                .map(uploadTokenBaseResponse -> uploadTokenBaseResponse.data.token);
    }


    /*
     *
     *  通知 反馈
     *
     */
    public void feedback(String feedbackText) {
        Map<String, String> map = new HashMap<>();
        map.put("send_uid", String.valueOf(MisterData.getInstance().globalInfo.userInfo.uid));
        map.put("message", EncodeUtil.encodeBase64(feedbackText));
        Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****feedback--");
                    return misterRegisterApi.feedback(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(5, 5000))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        responseBody -> {

                        },
                        throwable -> {
                            LogUtil.e("feedback--", throwable);
                        }
                );
    }

    public void getNotice() {

    }


    /*
     *
     *  社区
     *
     */
    //      主题
    public Observable<CommunityTopic> getCommunityTopic() {
        Map<String, String> map = new HashMap<>();
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getCommunityTopic--");
                    return misterCommunityApi.getTopic(addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(4, 2000))
                .subscribeOn(Schedulers.io())
                .map(objectBaseResponse -> objectBaseResponse.data);
    }

    //      文章概要列表
    public Observable<ArrayList<CommunityArticle>> getCommunityArticleOutline(String topic, boolean isHot, int count, long endTime) {
        return isHot ? getCommunityHot(topic, count, endTime) : getCommunityNew(topic, count, endTime);
    }

    public Observable<ArrayList<CommunityArticle>> getCommunityArticleOutline(String topic, boolean isHot) {
        return getCommunityArticleOutline(topic, isHot, 10, System.currentTimeMillis() / 1000);
    }

    //      热门
    public Observable<ArrayList<CommunityArticle>> getCommunityHot(String topic, int count, long endTime) {
        Map<String, String> map = countAndEnd_Time(String.valueOf(count), String.valueOf(endTime));
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getCommunityHot--");
                    return misterCommunityApi.getHot(topic, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(2, 2000))
                .subscribeOn(Schedulers.io())
                .map(objectBaseResponse -> objectBaseResponse.data.parse().articleArrayList);
    }

    //      最新
    public Observable<ArrayList<CommunityArticle>> getCommunityNew(String topic, int count, long endTime) {
        Map<String, String> map = countAndEnd_Time(String.valueOf(count), String.valueOf(endTime));
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getCommunityNew--");
                    return misterCommunityApi.getNew(topic, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(2, 2000))
                .subscribeOn(Schedulers.io())
                .map(objectBaseResponse -> objectBaseResponse.data.parse().articleArrayList);
    }

    //      文章
    public Observable<CommunityArticle> getCommunityArticle(String topic, String id) {
        Map<String, String> map = new HashMap<>();
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getCommunityArticle--");
                    return misterCommunityApi.getArticle(topic, id, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(2, 2000))
                .subscribeOn(Schedulers.io())
                .map(objectBaseResponse -> objectBaseResponse.data.parse());
    }


    //      评论
    public Observable<ArrayList<CommunityComment>> getCommunityComment(String topic, String docId) {
        return getCommunityComment(topic, docId, 10, 0);
    }

    public Observable<ArrayList<CommunityComment>> getCommunityComment(String topic, String docId, int count, long endTime) {
        Map<String, String> map = countAndEnd_Time(count, endTime);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getCommunityComment--");
                    return misterCommunityApi.getComment(topic, docId, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(2, 2000))
                .subscribeOn(Schedulers.io())
                .map(objectBaseResponse -> objectBaseResponse.data.parse().commentArrayList);
    }

    //      评论的回复
    public Observable<ArrayList<CommunityReplyToComment>> getCommunityReplayToComment(String topic, String docId, String commentId) {
        return getCommunityReplayToComment(topic, docId, commentId, 10, 0);
    }

    public Observable<ArrayList<CommunityReplyToComment>> getCommunityReplayToComment(String topic, String docId, String commentId, int count, long endTime) {
        Map<String, String> map = countAndEnd_Time(count, endTime);
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****getCommunityReplay--");
                    return misterCommunityApi.getReplayToComment(topic, docId, commentId, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(2, 2000))
                .subscribeOn(Schedulers.io())
                .map(objectBaseResponse -> objectBaseResponse.data.parse().replyToCommentArrayList);
    }

    //      发文章
    public Observable<CommunityPostResult> postCommunityArticle(String topic, String title, String outlineText, String detailText, ArrayList<String> imgUrls) {
        Map<String, String> map = phoAndToken();
        map.put("topic_head", EncodeUtil.encodeBase64(title) + "");
        map.put("abstract", CommunityArticle.ArticleContent.encode(outlineText, imgUrls) + "");
        map.put("detail", CommunityArticle.ArticleContent.encode(detailText, imgUrls) + "");
        map.put("show_name", MisterData.getInstance().globalInfo.userInfo.mapInfo.baseInfo.getNicknameStr() + "");
        map.put("show_url", MisterData.getInstance().globalInfo.userInfo.mapInfo.headUrl + "");
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****postCommunityArticle--");
                    return misterCommunityApi.postArticle(topic, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(4, 2000))
                .subscribeOn(Schedulers.io())
                .map(communityPostResultBaseResponse -> communityPostResultBaseResponse.data);
    }

    public void postCommunityArticleAndSendEvent(final String topic, final String title, final String outlineText, final String detailText, final ArrayList<String> imgUrls) {
        postCommunityArticle(topic, title, outlineText, detailText, imgUrls)
                .subscribe(
                        communityPostResult -> {
                            RxBus.getDefault().post(
                                    new PostCommunityArticleEvent(
                                            new CommunityArticle(
                                                    communityPostResult.docId, topic, title,
                                                    new CommunityArticle.ArticleContent(imgUrls, outlineText),
                                                    new CommunityArticle.ArticleContent(imgUrls, detailText)
                                            )
                                    )
                            );
                        }
                );
    }

    //      发评论
    public Observable<CommunityPostResult> postCommunityComment(String docId, String contentText) {
        Map<String, String> map = phoAndToken();
        map.put("content", EncodeUtil.encodeBase64(contentText) + "");
        map.put("show_name", MisterData.getInstance().globalInfo.userInfo.mapInfo.baseInfo.getNicknameStr() + "");
        map.put("show_url", MisterData.getInstance().globalInfo.userInfo.mapInfo.headUrl + "");
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****postCommunityComment--");
                    return misterCommunityApi.postComment(docId, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(4, 2000))
                .subscribeOn(Schedulers.io())
                .map(communityPostResultBaseResponse -> communityPostResultBaseResponse.data);
    }

    //      回复评论
    public Observable<CommunityPostResult> postCommunityReplyToComment(String docId, String commentId, String byReplyId, String contentText) {
        Map<String, String> map = phoAndToken();
        map.put("content", EncodeUtil.encodeBase64(contentText) + "");
        map.put("show_name", MisterData.getInstance().globalInfo.userInfo.mapInfo.baseInfo.getNicknameStr() + "");
        map.put("show_url", MisterData.getInstance().globalInfo.userInfo.mapInfo.headUrl + "");
        if (byReplyId != null) {
            map.put("by_reply_id", byReplyId);
        }
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****postCommunityComment--");
                    return misterCommunityApi.postReplyToComment(docId, commentId, addTimeAndSign(map));
                })
                .retryWhen(new RetryWithDelay(4, 2000))
                .subscribeOn(Schedulers.io())
                .map(communityPostResultBaseResponse -> communityPostResultBaseResponse.data);
    }


    /*
     *      工具
     */
    public Observable<Boolean> downloadFile(@NonNull String remoteUrl,@NonNull String saveDir,@NonNull String fileName ){
        return Observable.just(1)
                .flatMap(integer -> {
                    LogUtil.d("****downloadFile--");
                    return misterBaseApi.downloadFile(remoteUrl);
                })
                .retryWhen(new RetryWithDelay(2,2000))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(responseBody -> {
                    if (FileStorage.createOrExistsDir(new File(saveDir))){
                        LogUtil.d("****downloadFile--"+new File(saveDir,fileName).getAbsolutePath());
                        FileStorage.saveFile(new File(saveDir,fileName),responseBody.byteStream());
                        return true;
                    }else {
                        return false;
                    }
                });
    }

    /*
     *
     *  retrofit 构造器
     *
     */
    public static MisterApi getInstance() {
        if (instance == null) {
            synchronized (MisterApi.class) {
                if (instance == null) {
                    instance = new MisterApi();
                }
            }
        }
        return instance;
    }

    private static MisterBaseApi getMisterApi() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HOST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(MisterBaseApi.class);
    }

    private static MisterRegisterApi getRegisterApi() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REGISTER_HOST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(MisterRegisterApi.class);
    }

    private static MisterCommunityApi getCommunityApi() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(COMMUNITY_HOSE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(MisterCommunityApi.class);
    }


    /*
     *
     *  标准字段
     *
     */
    public static Map<String, String> phoAndToken() {
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", MisterData.getInstance().phone);
        map.put("token", MisterData.getInstance().globalInfo.getToken());
        return map;
    }

    public static Map<String, String> countAndEnd_Time(String count, String end_time) {
        Map<String, String> map = new HashMap<>();
        map.put("count", count);
        map.put("end_time", end_time);
        return map;
    }

    public static Map<String, String> countAndEnd_Time(long count, long end_time) {
        return countAndEnd_Time(String.valueOf(count), String.valueOf(end_time));
    }

    public static Map<String, String> addTimeAndSign(@Nullable Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        map.put("_", time);
        if (map.containsKey("sign")) {
            map.remove("sign");
        }
        map.put("sign", EncodeUtil.encodeMD5(map));
        return map;
    }

    public static ArrayList<String> getCompleteQiniuUrl(List<String> keys) {
        ArrayList<String> list = new ArrayList<>();
        if (keys != null) {
            for (String s : keys) {
                list.add(QINIU_BASE_URL + s);
            }
        }
        return list;
    }

    /*
     * 重试
     */

    public static class RetryWithDelay implements Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;
        private final int retryDelayMillis;
        private int retryCount = 0;

        public RetryWithDelay(int maxRetries, int retryDelayMillis) {
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
        }

        /*        Observable
                .create((Subscriber<? super String> s) -> {
                    LogUtil.i("subscribing--");
                    s.onError(new RuntimeException("always fails"));
                })
                .retryWhen(attempts -> {
                    return attempts
                            .zipWith(Observable.range(1, 3), (n, i) -> i)
                            .flatMap(i -> {
                                LogUtil.i("delay retry by " + i + " second(s)");
                                return Observable.timer(i, TimeUnit.SECONDS);
                            });
                })
                .toBlocking()
                .forEach(LogUtil::i);*/



/*        @Override
        public Observable<?> call(Observable<? extends Throwable> throwable) {
            retryCount++;
            LogUtil.i("call--" + retryCount);
            if (retryCount <= maxRetries) {
                // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).

                return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
            }
            // Max retries hit. Just pass the error along.
            return throwable;
        }*/


        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts
                    .flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {
                            retryCount++;
                            if (retryCount <= maxRetries) {
                                // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                                return Observable.timer(retryDelayMillis,
                                        TimeUnit.MILLISECONDS);
                            }
                            // Max retries hit. Just pass the error along.
                            return Observable.error(throwable);
                        }
                    });
        }
    }


    public static class NullOnEmptyConverterFactory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) return null;
                    return delegate.convert(body);
                }
            };
        }
    }




    /*
     * 以下字段构造方法弃用
     */
/*

    public static Map<String, String> addSign(@NonNull Map<String, String> map) {
        map.put("sign", EncodeUtil.encodeMD5(map));
        return map;
    }

    public static Map<String, String> fieldWithPhoTokTim() {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", MisterData.getInstance().phone);
        map.put("token", MisterData.getInstance().globalInfo.getToken());
        map.put("_", time);
        return map;
    }

    public static Map<String, String> fieldPheTim() {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, String> map = new HashMap<>();
        map.put("phone_num", MisterData.getInstance().phone);
        map.put("_", time);
        return map;
    }

    public static Map<String, String> fieldTim() {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, String> map = new HashMap<>();
        map.put("_", time);
        return map;
    }

    public static Map<String, String> standFieldsWithPhoTakTimSig() {
        Map<String, String> map = fieldWithPhoTokTim();
        map.put("sign", EncodeUtil.encodeMD5(map));
        return map;
    }
*/

}
