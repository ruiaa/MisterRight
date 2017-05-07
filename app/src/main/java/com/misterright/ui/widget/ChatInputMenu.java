package com.misterright.ui.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.misterright.R;
import com.misterright.ui.base.BaseActivity;
import com.misterright.ui.status.ChatActivity;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.bus.NoRecordVoicePermissionEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.huanxin.SendHuan;
import com.misterright.util.keyboard.KeyboardSwitch;

import java.util.concurrent.TimeUnit;

import me.nereo.multi_image_selector.MultiImageSelector;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/10/31.
 */

public class ChatInputMenu extends RelativeLayout {

    public static final int PICTURE_FROM_PICK = 1;
    public static final int PICTURE_FROM_MUITL_PICK=12;
    public static final int REQUEST_RECORD_VOICE_PERMISSION = 11;

    private int minH;
    private int maxH;

    private Activity activity;
    private InputMethodManager inputManager;
    private ChatVoiceRecorder chatVoiceRecorder;
    private SendHuan sendHuan;

    public KeyboardSwitch keyboardSwitch;
    private ImageButton voiceOrText;
    private ImageButton more;
    private ImageButton emotionOrText;
    private TextView sendText;
    private TextView voiceStart;
    public EditText editText;

    public ChatInputMenu(Context context) {
        super(context);
        init(context);
    }

    public ChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatInputMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        minH = ResUtil.getDimenInPx(R.dimen.chat_input_bottom_menu_height);
        maxH = ResUtil.getDimenInPx(R.dimen.chat_input_bottom_menu_height_max);

        LayoutInflater.from(context).inflate(R.layout.chat_input_menu, this);

        voiceOrText = (ImageButton) findViewById(R.id.chat_edit_or_voice);
        more = (ImageButton) findViewById(R.id.chat_more);
        emotionOrText = (ImageButton) findViewById(R.id.chat_input_emoji);
        sendText = (TextView) findViewById(R.id.chat_send_text);
        voiceStart = (TextView) findViewById(R.id.chat_voice_start);
        editText = (EditText) findViewById(R.id.chat_edit);
        editText.clearFocus();

    }


    public void init(Activity activity, ChatVoiceRecorder chatVoiceRecorder, SendHuan sendHuan) {
        this.activity = activity;
        inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.chatVoiceRecorder = chatVoiceRecorder;
        this.sendHuan = sendHuan;

        setKeyboardSwitchAndEmotionFrame();
        keyboardSwitch.hideSoftInput();

        setListener();
        stateInputText();

        Subscription subscription = RxBus.getDefault().toObservable(NoRecordVoicePermissionEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    chatVoiceRecorder.cancelRecord();
                    ToastUtil.showShort("没有录音权限");

                });
        ((BaseActivity) activity).addSubscription(subscription);
    }

    public void setListener() {

        chatVoiceRecorder.setCallback((voiceFilePath, voiceTimeLength) -> {
            sendHuan.sendVideo(voiceFilePath, voiceTimeLength);
        });

        voiceStart.setOnTouchListener(new VoiceMonitorListener());

        // 声音 or 文本
        voiceOrText.setOnClickListener(v -> changeTextOrVoice());

        // 表情 or 文本
        emotionOrText.setOnClickListener(v -> changeTextOrEmotion());

        // 发送 or 选择
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switchMoreOrSend();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //发送
        sendText.setOnClickListener(v -> {
            if (editText.length() != 0) {
                sendHuan.sendText(editText.getText().toString());
                editText.setText("");
            }
        });

        //选择图片
        more.setOnClickListener(v -> {
            /*Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image*//*");
            activity.startActivityForResult(intent, PICTURE_FROM_PICK);*/
            MultiImageSelector.create()
                    .showCamera(true) // 是否显示相机. 默认为显示
                    .single() // 单选模式
                    .start(activity, PICTURE_FROM_MUITL_PICK);
        });
    }

    /*
     * 状态转换
     */
    private void changeTextOrVoice() {
        if (!voiceOrText.isSelected()) {
            stateInputText();
        } else {
            stateVoice();
        }
    }

    private void changeTextOrEmotion() {
        if (!emotionOrText.isSelected()) {
            stateInputText();
        } else {
            stateInputEmotion();
        }
    }

    private void switchMoreOrSend() {
        if (editText.getText().length() != 0) {
            sendText.setVisibility(View.VISIBLE);
            more.setVisibility(View.INVISIBLE);
        } else {
            sendText.setVisibility(View.INVISIBLE);
            more.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 3种状态
     */
    private void stateVoice() {
        voiceOrText.setSelected(false);

        editText.setVisibility(View.INVISIBLE);
        voiceStart.setVisibility(View.VISIBLE);

        emotionOrText.setVisibility(GONE);

        sendText.setVisibility(View.INVISIBLE);
        more.setVisibility(View.VISIBLE);

        keyboardSwitch.hideSoftInput();
        keyboardSwitch.hideEmotionLayout();
    }

    private void stateInputText() {
        voiceOrText.setSelected(true);

        editText.setVisibility(VISIBLE);
        voiceStart.setVisibility(INVISIBLE);

        emotionOrText.setVisibility(VISIBLE);
        emotionOrText.setSelected(true);

        switchMoreOrSend();
        keyboardSwitch.showSoftInput();
    }

    private void stateInputEmotion() {
        voiceOrText.setSelected(true);

        editText.setVisibility(VISIBLE);
        voiceStart.setVisibility(INVISIBLE);

        emotionOrText.setVisibility(VISIBLE);
        emotionOrText.setSelected(false);

        switchMoreOrSend();
        keyboardSwitch.showEmotionLayout();
    }


    /*
     * 设置表情面板
     */
    private void setKeyboardSwitchAndEmotionFrame() {
        keyboardSwitch = KeyboardSwitch.with(activity)
                .bindToEditText(editText)
                .bindToTopView(((ChatActivity) activity).getTopView())
                .bindToEmotionView(((ChatActivity) activity).getEmojiconFrame())
                .build();
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }



    public class VoiceMonitorListener implements OnTouchListener {

        private static final float MAX_MOVE = 400L;
        private boolean cancel = false;
        private boolean havePermission = true;
        private boolean haveCheckPer = false;
        float y;

        private long time=0;
        private boolean timeOut;
        private Subscription subscriptionForTimeCount;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    chatVoiceRecorder.startRecord();
                    voiceStart.setText(R.string.chat_up_send);
                    y = event.getY();

                    time=0;
                    cancel = false;
                    timeOut = false;
                    subscriptionForTimeCount = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                            .map(aLong -> aLong+1)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(l -> {
                                time=l;
                                if (l > 55) {
                                    chatVoiceRecorder.showTooLong((int)(60 - l));
                                }
                                if (l >= 60) {
                                    if (!timeOut){
                                        chatVoiceRecorder.stopRecoding();
                                        voiceStart.setText(R.string.chat_down_voice);
                                        if (!subscriptionForTimeCount.isUnsubscribed()){
                                            subscriptionForTimeCount.unsubscribe();
                                        }
                                    }
                                    timeOut = true;
                                }
                            });
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (timeOut||time>55) {
                        break;
                    }
                    if (Math.abs(y - event.getY()) > MAX_MOVE) {
                        chatVoiceRecorder.showUpToCancelAndBack();
                        cancel = true;
                    } else {
                        chatVoiceRecorder.showMoveToCancelAndVol();
                        cancel = false;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (timeOut) {
                        break;
                    }
                    if (!subscriptionForTimeCount.isUnsubscribed()){
                        subscriptionForTimeCount.unsubscribe();
                    }
                    if (cancel) {
                        chatVoiceRecorder.cancelRecord();
                    } else if (time<1) {
                        chatVoiceRecorder.cancelRecordForTooShort();
                    } else {
                        chatVoiceRecorder.stopRecoding();
                    }
                    voiceStart.setText(R.string.chat_down_voice);
                    break;
                }
            }
            return true;
        }
    }

}
