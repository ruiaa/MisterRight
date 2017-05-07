package com.misterright.util.huanxin;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.misterright.util.LogUtil;
import com.misterright.util.bus.NoRecordVoicePermissionEvent;
import com.misterright.util.bus.RxBus;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class SendHuan {

    private String toUser;
    private OnSendListener onSendListener;

    public SendHuan(String toUser){
        this.toUser = toUser;
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public void sendText(String content){
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toUser);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

        if (onSendListener!=null){
            onSendListener.onSend(message);
        }
    }

    public void sendVideo(String filePath, int length){
        //filePath为语音文件路径，length为录音时间(秒)
        try{
            EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toUser);
            EMClient.getInstance().chatManager().sendMessage(message);
            if (onSendListener!=null){
                onSendListener.onSend(message);
            }
        }catch (Exception e){
            LogUtil.e("sendVideo--",e);
            RxBus.getDefault().post(new NoRecordVoicePermissionEvent());
        }
    }

    public void sendImg(String imagePath,boolean unUseCompress){
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imagePath, unUseCompress, toUser);
       // EMMessage message = EMMessage.createTxtSendMessage(imagePath, toUser);
        EMClient.getInstance().chatManager().sendMessage(message);

        if (onSendListener!=null){
            onSendListener.onSend(message);
        }
    }


/*
    public void sendExpand(String content){
        EMMessage message = EMMessage.createTxtSendMessage(content, toUser);

// 增加自己特定的属性
        message.setAttribute("attribute1", "value");
        message.setAttribute("attribute2", true);

        EMClient.getInstance().chatManager().sendMessage(message);

//接收消息的时候获取到扩展属性
//获取自定义的属性，第2个参数为没有此定义的属性时返回的默认值
        message.getStringAttribute("attribute1",null);
        message.getBooleanAttribute("attribute2", false);
    }
*/

/*    public void sendCmd(){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action="action1";//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        String toUsername = "test1";//发送给某个人
        cmdMsg.setReceipt(toUsername);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }*/


    public static interface OnSendListener{
        void onSend(EMMessage message);
    }
}
