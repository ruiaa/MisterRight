package com.misterright.ui.register;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;
import com.misterright.MisterConfig;
import com.misterright.R;
import com.misterright.databinding.ActivityLoginBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.model.StatusMonitor;
import com.misterright.model.entity.MisterStatus;
import com.misterright.ui.DialogHelper;
import com.misterright.ui.FragmentStack;
import com.misterright.ui.MainActivity;
import com.misterright.ui.base.BaseActivity;
import com.misterright.util.LogUtil;
import com.misterright.util.bus.NewMsgEvent;
import com.misterright.util.bus.NewNoteEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.huanxin.ReceiveHuan;
import com.misterright.util.storage.KeyStorage;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    public FragmentStack fragmentStack;
    public DialogHelper dialogHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        fixKeyboardAdjustResizeNoEffect();

        if (KeyStorage.contains(MisterConfig.KEY_MISTER_DATA)) {
            MisterData.setFromKeyStorage();
            loginToken();
        } else {
            getWindow().setBackgroundDrawableResource(R.mipmap.register_bg);
            fragmentStack = new FragmentStack(this, R.id.activity_login_frameLayout);
            dialogHelper = new DialogHelper(this);
            if (savedInstanceState == null) {
                fragmentStack.open(RegisterOrLoginFragment.newInstance());
            }
        }
    }

    private void loginFail() {
        getWindow().setBackgroundDrawableResource(R.mipmap.register_bg);
        if (fragmentStack==null) {
            fragmentStack = new FragmentStack(this, R.id.activity_login_frameLayout);
        }else {
            fragmentStack.clear();
        }
        if (dialogHelper==null) {
            dialogHelper = new DialogHelper(this);
        }else {
            dialogHelper.hideAll();
        }
        fragmentStack.open(RegisterOrLoginFragment.newInstance());
        fragmentStack.open(LoginFragment.newInstance());
    }

    private void loginToken() {
        login(MisterData.getInstance().phone, MisterData.getInstance().pwd, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        aBoolean -> {
                            if (aBoolean) {
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                loginFail();
                            }
                        }, throwable -> {
                            loginFail();
                            LogUtil.e("autoLogin--", throwable);
                        }
                );
    }

    public void loginUsePwdOpenMain(final String phone, final String pwd) {
        login(phone, pwd, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        aBoolean -> {
                            if (aBoolean) {
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                loginFail();
                            }
                        }, throwable -> {
                            loginFail();
                            LogUtil.e("autoLogin--", throwable);
                        }
                );
    }

    //登录 获取状态 获取对方资料 登录环信
    public Observable<Boolean> login(final String phone, final String pwd, final String token) {
        LogUtil.i("login--");
        return MisterApi
                .getInstance()
                //登录
                .login(phone, pwd, token)
                //获取状态
                .flatMap(globalInfo -> {
                    LogUtil.i("login--获取状态");
                    return MisterApi.getInstance().getStatus();
                })
                //状态判断
                .flatMap(misterStatus -> {
                    LogUtil.i("login--状态判断");
                    if (MisterData.getInstance().status.pageStatus.equals(MisterStatus.MEET)) {
                        //相遇匹配状态  打开监测
                        StatusMonitor.getInstance().startMonitor();
                        return Observable.just(MisterData.getInstance().status.pageStatus);
                    } else {
                        //相识状态
                        return MisterApi.getInstance()
                                .getOtherInfo(MisterData.getInstance().status.pairInfo.targetUid)
                                .map(userInfo -> {
                                    LogUtil.i("login--获取对方");
                                    MisterData.getInstance().otherUser = userInfo;
                                    return MisterData.getInstance().status.pageStatus;
                                });
                    }
                })
                //登录环信
                .map(s -> {
                    LogUtil.i("login--登录环信");
                    loginHuanxin(MisterData.getInstance().globalInfo.hxUser,
                            MisterData.getInstance().globalInfo.hxPwd);
                    return true;
                });
    }

    private void loginHuanxin(final String userName, final String password) {
        EMClient.getInstance().login(userName, password, new EMCallBack() {
            //回调

            @Override
            public void onSuccess() {
                //加载数据
                EMClient.getInstance().chatManager().loadAllConversations();
                EMClient.getInstance().groupManager().loadAllGroups();
                //监测
                monitorHuanxin();
                LogUtil.d("登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                LogUtil.d("登录聊天服务器失败！");
            }
        });
    }

    private void monitorHuanxin() {
        //监控cmd命令
        ReceiveHuan.registerListener(new ReceiveHuan.MsgListener() {
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                super.onCmdMessageReceived(messages);
                /*
                 * messages.body :
                 *
                 *      body["target_type"] = "users"
                 *      body["target"] = "hx_users"
                 *      body["msg"] = msg_data
                 *                  msg_data["type"] = "txt"
                 *                  msg_data["msg"] = msg
                 *      body["from"] = "admin"
                 *      body["ext"] = ext_data
                 *                  ext_data["attr1"] = "v1"
                 *                  ext_data["msg_type"] = msg_type
                 *
                 *
                 * msg_type = "meet_start"
                 * msg_type = "meet_like"
                 */
                for (EMMessage emMessage : messages) {
                    EMCmdMessageBody body = (EMCmdMessageBody) emMessage.getBody();
                    if (body.action().contains("新状态")) {
                        RxBus.getDefault().post(new NewNoteEvent(1, false));
                    }
                    //LogUtil.i("onCmdMessageReceived--"+body.action());
                }
            }

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                super.onMessageReceived(messages);
                RxBus.getDefault().post(new NewMsgEvent(messages.size()));
            }
        });

        //监控环信掉线
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(final int error) {
                if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                } else {
                    if (NetUtils.hasNetwork(LoginActivity.this)) {
                        //连接不到聊天服务器
                    } else {
                        //当前网络不可用，请检查网络设置
                    }
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.canTurnBack()){
            fragmentStack.turnBackFragment();
        }else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        if (fragmentStack != null) {
            for (Fragment fragment : fragmentStack.getStack()) {

                if (fragment instanceof AddInfoFragment) {
                    //选择图片
                    if (((AddInfoFragment) fragment).onSelectIdcardImgActivityResult(requestCode, resultCode, data))
                        return;
                }
            }
        }
    }

    /*
            *
            *
            *
            *
            *
            *
            *
            *
            *
            *
            *
            *
            *
            */


    /*
     * 引导界面
     * 开屏界面
     *
     * 登录mister
     * 登录环信
     *
     * 监控cmd命令
     * 监控环信掉线
     *
     */

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        *//*startTime = System.currentTimeMillis();
        getStorageAndLoginOrRegister();

        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        tryToSetUser(binding.loginInputName);
        binding.loginCommit.setOnClickListener(v -> {
            loginUsePwd(
                    binding.loginInputName.getText().toString(),
                    binding.loginInputPwd.getText().toString()
            );
        });*//*

        //tryApi();
        //+ open mainActivity
        //13115202019649
        //13115202019650
        //loginUsePwd("13138705415", "123456");
        //loginUsePwd("15202019649", "123456");
        //loginUsePwd("15202019650", "123456");//target 15202019651

        //引导界面
        //开屏界面



    }*/

/*    private void getStorageAndLoginOrRegister(){
        if (KeyStorage.contains(MisterConfig.KEY_FIRST_ENTER)){
            //auto loginUsePwd
            MisterData.setMisterData(KeyStorage.get(MisterConfig.KEY_MISTER_DATA));
            loginUsePwd(MisterData.getInstance().phone,MisterData.getInstance().pwd);
        }else {
            ToastUtil.showLong("first enter,need to register");
        }
    }

    private void tryApi() {
        String BASE_HOST = "http://www.gogodata.cn:9001/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        MisterBaseApiResponse api = retrofit.create(MisterBaseApiResponse.class);

        String phone = "13138705415";
        //String phone="13430208610";
        String pwd = "123456";
        String pwdMd = EncodeUtil.encodeMD5NoE(pwd);
        String time = String.valueOf(System.currentTimeMillis() / 1000);

        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("phone_num", phone);
        loginMap.put("pwd", pwdMd);
        loginMap.put("_", time);

        String sign = EncodeUtil.encodeMD5(loginMap);


        MisterApi.getInstance()
                .loginUsePwd(phone, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(integer -> {
                    LogUtil.i("tryApi--" + MisterData.getInstance().globalInfo.userInfo.name);

                    MisterApi.getInstance().getStatus()
                            .subscribe(misterStatus -> {
                                LogUtil.i("getStatus--" + MisterData.getInstance().status.meetStatus);
                            });


*//*                    api.loginUsePwd(phone, pwdMd, time, sign)
                            .subscribeOn(Schedulers.io())
                            .subscribe(responseBody -> {
                                try {
                                    LogUtil.i("tryApi--loginUsePwd" + responseBody.string());
                                } catch (Exception e) {
                                    LogUtil.e("tryApi--", e);
                                }
                            });*//*


*//*                    Map<String, String> map = new HashMap<>();
                    map.put("phone_num", phone);
                    map.put("token", MisterData.getInstance().globalInfo.getToken());
                    map.put("_", time);
                    api.getStatus(phone, MisterData.getInstance().globalInfo.getToken(), time, EncodeUtil.encodeMD5(map))
                    //api.getStatus(EncodeUtil.standWithPhoTakTimSig())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                    responseBody -> {
                                        try {
                                            LogUtil.i("tryApi--status" + responseBody.string());
                                        } catch (Exception e) {
                                            LogUtil.e("tryApi--", e);
                                        }
                                    },
                                    throwable -> {
                                        LogUtil.e("tryApi--", throwable);
                                    }
                            );*//*

                });

    }


    private void register(String userName, String password) {

    }

    private void loginUsePwd(final String phone, final String pwd) {
        MisterApi.getInstance()
                .loginUsePwd(phone, pwd)
                .delay((errorLoginCount==0 ? 1L:1000L),TimeUnit.MILLISECONDS)
                .flatMap(globalInfo -> {
                    LogUtil.i("loginUsePwd--mister   " + MisterData.getInstance().phone + "   " + MisterData.getInstance().globalInfo.userInfo.uid);
                    return MisterApi.getInstance()
                            .getStatus();
                })

                .flatMap(misterStatus -> {
                    LogUtil.i("loginUsePwd--" + MisterData.getInstance().status.pageStatus);

                    if (MisterData.getInstance().status.pageStatus.equals(MisterStatus.MEET)) {
                        //MeetStatusMonitor.getInstance().startMonitor();
                        StatusMonitor.getInstance().startMonitor();
                        return Observable.just(MisterData.getInstance().status.pageStatus);
                    } else {
                        return MisterApi.getInstance()
                                .getOtherInfo(MisterData.getInstance().status.pairInfo.targetUid)
                                .map(userInfo -> {
                                    MisterData.getInstance().otherUser = userInfo;
                                    return MisterData.getInstance().status.pageStatus;
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        s -> {
                            LogUtil.i("loginUsePwd--success");

                            loginHuanxin(MisterData.getInstance().globalInfo.hxUser,
                                    MisterData.getInstance().globalInfo.hxPwd);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        },
                        throwable -> {
                            LogUtil.e("loginUsePwd--", throwable);

                            errorLoginCount++;
                            if (errorLoginCount < 6) {
                                loginUsePwd(phone, pwd);
                            } else {
                                errorLoginCount=0;
                                ToastUtil.showShort("登录失败，请重新登录");
                            }
                        }
                );

*//*        .observeOn(Schedulers.io())
                .subscribe(
                        globalInfo -> {
                            LogUtil.i("loginUsePwd--" + "mister   " + MisterData.getInstance().phone
                                    + "    " + MisterData.getInstance().globalInfo.userInfo.name);
                            LogUtil.i("loginUsePwd--" + "hx   " + MisterData.getInstance().globalInfo.hxUser);

                            loginHuanxin(MisterData.getInstance().globalInfo.hxUser,
                                    MisterData.getInstance().globalInfo.hxPwd);

                            MisterApi.getInstance().getStatus()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            misterStatus -> {

                                                LogUtil.i("getStatus--" + MisterData.getInstance().status.pageStatus);
                                                if (MisterData.getInstance().status.pageStatus.equals(MisterStatus.MEET)) {
                                                    MeetStatusMonitor.getInstance().startMonitor();
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    MisterApi.getInstance()
                                                            .getOtherInfo(MisterData.getInstance().status.pairInfo.targetUid)
                                                            .subscribe(userInfo -> {
                                                                MisterData.getInstance().otherUser = userInfo;
                                                                LogUtil.i("loginUsePwd-- 对方" + userInfo.uid + userInfo.name);
                                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                finish();
                                                            }, throwable -> {

                                                            });
                                                }
                                            },
                                            throwable -> {
                                                ToastUtil.showLong("登录失败，请重新登录");
                                                LogUtil.e("loginUsePwd--", throwable);
                                            });


                        },
                        throwable -> {
                            ToastUtil.showLong("登录失败，请重新登录");
                            LogUtil.e("loginUsePwd--", throwable);
                        }
                );*//*
    }

    private void loginHuanxin(final String userName, final String password) {
        EMClient.getInstance().loginUsePwd(userName, password, new EMCallBack() {
            //回调

            @Override
            public void onSuccess() {
                //加载数据
                EMClient.getInstance().chatManager().loadAllConversations();
                EMClient.getInstance().groupManager().loadAllGroups();
                //监测
                monitorHuanxin();
                LogUtil.d("登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                LogUtil.d("登录聊天服务器失败！");
            }
        });
    }

    private void monitorHuanxin() {
        //监控cmd命令
        ReceiveHuan.registerListener(new ReceiveHuan.MsgListener() {
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                super.onCmdMessageReceived(messages);
                *//*
                 * messages.body :
                 *
                 *      body["target_type"] = "users"
                 *      body["target"] = "hx_users"
                 *      body["msg"] = msg_data
                 *                  msg_data["type"] = "txt"
                 *                  msg_data["msg"] = msg
                 *      body["from"] = "admin"
                 *      body["ext"] = ext_data
                 *                  ext_data["attr1"] = "v1"
                 *                  ext_data["msg_type"] = msg_type
                 *
                 *
                 * msg_type = "meet_start"
                 * msg_type = "meet_like"
                 *//*

                for (EMMessage emMessage : messages) {
                    EMCmdMessageBody body = (EMCmdMessageBody) emMessage.getBody();
                    if (body.action().contains("新状态")) {
                        RxBus.getDefault().post(new NewNoteEvent(1, false));
                    }
                    //LogUtil.i("onCmdMessageReceived--"+body.action());
                }
            }

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                super.onMessageReceived(messages);
                RxBus.getDefault().post(new NewMsgEvent(messages.size()));
            }
        });

        //监控环信掉线
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(final int error) {
                if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                } else {
                    if (NetUtils.hasNetwork(LoginActivity.this)) {
                        //连接不到聊天服务器
                    } else {
                        //当前网络不可用，请检查网络设置
                    }
                }
            }

        });
    }


    private void tryToSetUser(EditText editText) {
        switch (getApplicationInfo().loadLabel(getPackageManager()).toString()) {
            //奕鑫 13115202019651  13113430208610  13113430208600 13113430208660
            case "small1": {
                editText.setText("13138471489");
                break;
            }
            case "small2": {
                editText.setText("13430208680");
                break;
            }
            case "small3": {
                editText.setText("13430208670");
                break;
            }
            case "small4": {
                editText.setText("15728675460");
                break;
            }
            case "small5": {
                editText.setText("18620985321");
                break;
            }
            case "small6": {
                editText.setText("18620985322");
                break;
            }
            case "small7": {
                editText.setText("18620985331");
                break;
            }
            case "small8": {
                editText.setText("18620985332");
                break;
            }
            case "small9": {
                editText.setText("18664560864");
                break;
            }
            case "small10": {
                editText.setText("12345678901");
                break;
            }
            default: {
                editText.setText("15202019649");
            }
        }
    }*/
}
