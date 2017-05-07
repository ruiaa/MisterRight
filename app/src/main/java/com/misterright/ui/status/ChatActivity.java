package com.misterright.ui.status;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.misterright.R;
import com.misterright.databinding.ActivityChatBinding;
import com.misterright.model.MisterData;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.ui.info.InfoActivity;
import com.misterright.ui.widget.ChatInputMenu;
import com.misterright.ui.widget.ChatList;
import com.misterright.ui.widget.ChatVoiceRecorder;
import com.misterright.ui.widget.emotion.EmotionFragment;
import com.misterright.util.ResUtil;
import com.misterright.util.bus.NewMsgEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.huanxin.ReceiveHuan;
import com.misterright.util.huanxin.SendHuan;
import com.misterright.util.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconsFragment;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class ChatActivity extends ToolbarActivity{

    private static final String TO_USER = "to_user";

    private EMMessageListener emMessageListener;
    private SendHuan sendHuan;
    private ReceiveHuan receiveHuan;
    private String toUser;

    private ChatList chatList;
    private ChatInputMenu chatInputMenu;
    private ChatVoiceRecorder chatVoiceRecorder;


    public static Intent newIntent(Context context, String touser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(TO_USER, touser);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChatBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        fixKeyboardAdjustResizeNoEffect();

        setToolbar();

        chatList = (ChatList) findViewById(R.id.chat_list);
        chatInputMenu = (ChatInputMenu) findViewById(R.id.chat_input_menu);
        chatVoiceRecorder = (ChatVoiceRecorder) findViewById(R.id.chat_voice_record);

        toUser = getIntent().getStringExtra(TO_USER);
        receiveHuan=new ReceiveHuan(toUser);
        sendHuan = new SendHuan(toUser);


        chatList.init(receiveHuan);

        sendHuan.setOnSendListener(message -> {
            chatList.getChatListAdapter().refreshSend(message);
        });

        emMessageListener = new ReceiveHuan.MsgListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                chatList.addMessage(messages);
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);


        chatInputMenu.init(this, chatVoiceRecorder, sendHuan);

        setEmotionFragment(false);
    }

    private void setToolbar() {
        initToolbar();
        setTitle(MisterData.getInstance().otherUser.name);
        setToolbarLeftShow(v -> {
            onBackPressed();
        });
        setToolbarRightText(" ", v -> {
            startActivity(InfoActivity.newIntent(this, InfoActivity.INFO_TYPE_PAIR));
        });
        toolbarRight.setCompoundDrawablesWithIntrinsicBounds(ResUtil.getDrawable(R.mipmap.chat_menu_open_info), null, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        RxBus.getDefault().post(new NewMsgEvent(-1));
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!chatInputMenu.keyboardSwitch.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (emMessageListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) return;
        if (requestCode == ChatInputMenu.PICTURE_FROM_PICK) {
            // 从相册返回的数据
            // 得到图片的全路径
            Uri uri = data.getData();
            String s = FileStorage.getPath(this, uri);
            if (s != null) {
                sendHuan.sendImg(s, false);
            }
        } else if (requestCode == ChatInputMenu.PICTURE_FROM_MUITL_PICK) {
            ArrayList<String> newPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            for (String s : newPaths) {
                sendHuan.sendImg(s,false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public View getTopView() {
        return findViewById(R.id.chat_top_view);
    }

    public View getEmojiconFrame() {
        return findViewById(R.id.chat_input_emoji_frame);
    }

    /*
     * 表情面板 输入接口
     */
    private void setEmotionFragment(boolean useSystemDefault) {
        EmotionFragment emotionFragment = EmotionFragment.newInstance(useSystemDefault);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.chat_input_emoji_frame, emotionFragment)
                .commit();

        emotionFragment.setOnEmotionBackspaceClickedListener(v -> {
            EmotionFragment.backspace(chatInputMenu.editText);
        });
        emotionFragment.setOnEmotionClickedListener(emojicon->{
            EmojiconsFragment.input(chatInputMenu.editText,emojicon);
        });
        FileStorage.getImgSaveDir();
    }


    /*
     * 消息的菜单
     */

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        chatList.getChatListAdapter().onMenuSelected(item);
        return super.onContextItemSelected(item);
    }
}
