package com.misterright.util.huanxin;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class ReceiveHuan {

    private String toUser;
    public EMConversation conversation;

    public ReceiveHuan(String toUser){
        this.toUser = toUser;
        conversation = EMClient.getInstance().chatManager().getConversation(toUser);
    }

    public boolean initConversation(){
        conversation = EMClient.getInstance().chatManager().getConversation(toUser);
        return conversation!=null;
    }

    public int getUnReadCount(){
        if (conversation==null&&!initConversation()) return 0;
        return conversation.getUnreadMsgCount();
    }

    public void setAsRead (){
        if (conversation==null&&!initConversation()) return;
        //指定会话消息未读数清零
        conversation.markAllMessagesAsRead();
        //把一条消息置为已读
        //conversation.markMessageAsRead(messageId);
        //所有未读消息数清零
        //EMClient.getInstance().chatManager().markAllConversationsAsRead();
    }

    public int getMsgCount(){
        if (conversation==null&&!initConversation()) return 0;
        //获取此会话在本地的所有的消息数量
        return conversation.getAllMsgCount();
        //如果只是获取当前在内存的消息数量，调用
        //conversation.getAllMessages().size();
    }

    public List<EMMessage>  getAllMsg(){
        if (conversation==null&&!initConversation()) return new ArrayList<>();
        //获取此会话的所有消息
        return conversation.getAllMessages();
        //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
        //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
    }

    public List<EMMessage> loadMoreMsgFromDB(String startMsgId, int num){
        if (conversation==null&&!initConversation()) return new ArrayList<>();
        return conversation.loadMoreMsgFromDB(startMsgId, num);
    }

    public void deleteMsg(EMMessage deleteMsg){
        if (conversation==null&&!initConversation()) return;
        //删除当前会话的某条聊天记录
        conversation.removeMessage(deleteMsg.getMsgId());
    }

    public void deleteThisConversation(){
        if (conversation==null&&!initConversation()) return;
        //删除和某个user会话，如果需要保留聊天记录，传false
        EMClient.getInstance().chatManager().deleteConversation(toUser, true);
    }





    public static void registerListener(EMMessageListener msgListener){
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    public static void removeListener(EMMessageListener msgListener){
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    public static class MsgListener implements EMMessageListener{

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };
}
