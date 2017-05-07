package com.misterright.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.misterright.util.RxManager;
import com.misterright.util.keyboard.AndroidKeyboardAdjustResizeNoWork;

import io.github.rockerhieu.emojiconize.Emojiconize;
import rx.Subscription;

/**
 * Created by ruiaa on 2016/10/21.
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Emojiconize.activity(this).go();
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initTheme();
    }


    /*
     *  after setContent();
     *  keyboard adjustResize
     */
    protected void fixKeyboardAdjustResizeNoEffect(){
        AndroidKeyboardAdjustResizeNoWork.assistActivity(this);
    }

    /*
     *
     * 设置主题
     *
     */
    public void initTheme(){

    }

    public void saveThemeChoose(){

    }



    /*
     *
     * 管理RxJava的subscription的释放
     *
     */
    private RxManager rxManager=new RxManager();

    public void addSubscription(Subscription s){
        rxManager.add(s);
    }

    public void clearSubscription(){
        rxManager.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxManager.clear();
    }
}
