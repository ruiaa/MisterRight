package com.misterright;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.misterright.util.AppUtil;
import com.misterright.util.ConvertUtil;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.StringStyles;
import com.misterright.util.ToastUtil;
import com.misterright.util.storage.FileStorage;
import com.misterright.util.storage.KeyStorage;

/**
 * Created by ruiaa on 2016/10/28.
 */

public class App extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        //util
        ToastUtil.register(appContext);
        ResUtil.register(appContext);
        StringStyles.register(appContext);
        ConvertUtil.register(appContext);
        AppUtil.register(appContext);
        FileStorage.register(appContext);

        //Hawk
        KeyStorage.init(appContext);


        //环信
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        //options.setAcceptInvitationAlways(false);
        //
        options.setAutoLogin(false);
        //
        //options.setNumberOfMessagesLoaded(1);
        //初始化
        EMClient.getInstance().init(appContext, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);


        LogUtil.i("onCreateView--px"+ AppUtil.getScreenWidth());
        LogUtil.i("onCreateView--dp"+ ConvertUtil.px2dp(AppUtil.getScreenWidth()));
    }
}
