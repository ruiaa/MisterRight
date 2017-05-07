package com.misterright.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.misterright.BR;
import com.misterright.R;
import com.misterright.http.MisterApi;
import com.misterright.ui.PictureActivity;
import com.misterright.ui.widget.ImgViewGlide;
import com.misterright.util.AppUtil;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.huanxin.ReceiveHuan;
import com.misterright.util.media.VoicePlayer;
import com.misterright.util.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by ruiaa on 2016/10/31.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.BindingHolder> {

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 13;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;

    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 12;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;


    private static final long TIME_BETWEEN_NEED_TO_SHOW = 2 * 60 * 1000L;

    private Context context;
    private RecyclerView recyclerView;
    private List<EMMessage> messageList;
    private ReceiveHuan receiveHuan;
    private VoicePlayer voicePlayer;

    public ItemClickListener itemClickListener;

    private boolean hasMore = true;

    private int pageSize = 20;

    public ChatListAdapter(ReceiveHuan receiveHuan, RecyclerView recyclerView, Context context) {
        this.context = context;
        this.receiveHuan = receiveHuan;
        this.recyclerView = recyclerView;
        messageList = new ArrayList<>();
        refresh(true);

        voicePlayer = new VoicePlayer();

        initContextMenuListener();
        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                LogUtil.i("onClick--oooo");

                if (getItemViewType(position) == MESSAGE_TYPE_RECV_VOICE
                        || getItemViewType(position) == MESSAGE_TYPE_SENT_VOICE) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.chat_voice_play_animate);
                    EMVoiceMessageBody body = (EMVoiceMessageBody) (messageList.get(position).getBody());
                    voicePlayer.onVoiceCLick(FileStorage.checkExists(body.getLocalUrl()) ? body.getLocalUrl() : body.getRemoteUrl(), imageView);

                } else if (getItemViewType(position) == MESSAGE_TYPE_RECV_IMAGE
                        || getItemViewType(position) == MESSAGE_TYPE_SENT_IMAGE) {
                    ImgViewGlide imgViewGlide=(ImgViewGlide)view.findViewById(R.id.chat_msg_img_img);
                    EMImageMessageBody body=(EMImageMessageBody) (messageList.get(position).getBody());
                    imgViewGlide.openBigImg(FileStorage.checkExists(body.getLocalUrl()) ? body.getLocalUrl():body.getRemoteUrl());
//                    app:autoOpenBigImg="@{FileStorage.checkExists(((EMImageMessageBody)chatMessage.getBody()).getLocalUrl()) ? ((EMImageMessageBody)chatMessage.getBody()).getLocalUrl(): ((EMImageMessageBody)chatMessage.getBody()).getRemoteUrl()}"

                }

            }

            @Override
            public void onLongClick(View view, int position) {
                LogUtil.i("onLongClick--0000");
                setSelectedPosition(position);
            }
        };
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= messageList.size()) {
            return -1;
        }

        EMMessage message = messageList.get(position);
        if (message.getType() == EMMessage.Type.TXT) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;// invalid
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.chat_msg_received_txt;
        View.OnCreateContextMenuListener listener;
        switch (viewType) {
            case MESSAGE_TYPE_RECV_TXT: {
                layoutId = R.layout.chat_msg_received_txt;
                listener = textCreateMenuListener;
                break;
            }
            case MESSAGE_TYPE_SENT_TXT: {
                layoutId = R.layout.chat_msg_sent_txt;
                listener = textCreateMenuListener;
                break;
            }
            case MESSAGE_TYPE_RECV_VOICE: {
                layoutId = R.layout.chat_msg_received_voice;
                listener = voiceCreateMenuListener;
                break;
            }
            case MESSAGE_TYPE_SENT_VOICE: {
                layoutId = R.layout.chat_msg_sent_voice;
                listener = voiceCreateMenuListener;
                break;
            }
            case MESSAGE_TYPE_RECV_IMAGE: {
                layoutId = R.layout.chat_msg_received_img;
                listener = imgCreateMenuListener;
                break;
            }
            case MESSAGE_TYPE_SENT_IMAGE: {
                layoutId = R.layout.chat_msg_sent_img;
                listener = imgCreateMenuListener;
                break;
            }

            default: {
                layoutId = R.layout.chat_msg_received_txt;
                listener = textCreateMenuListener;
                break;
            }
        }


        ViewDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                layoutId,
                parent,
                false);
        return new BindingHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        holder.getBinding().setVariable(BR.chatMessage, messageList.get(position));
        holder.getBinding().setVariable(BR.showTimeStamp, showTime(position));

/*        if (getItemViewType(position) == MESSAGE_TYPE_RECV_VOICE
                || getItemViewType(position) == MESSAGE_TYPE_SENT_VOICE) {

            ImageView imageView = (ImageView) holder.getBinding().getRoot().findViewById(R.id.chat_voice_play_animate);
            EMVoiceMessageBody body = (EMVoiceMessageBody) (messageList.get(position).getBody());
            holder.getBinding().getRoot().findViewById(R.id.chat_bubble)
                    .setOnClickListener(v -> {
                        voicePlayer.onVoiceCLick(FileStorage.checkExists(body.getLocalUrl()) ? body.getLocalUrl() : body.getRemoteUrl(), imageView);
                    });
            LogUtil.i("onBindViewHolder--voice local"+body.getLocalUrl());
            LogUtil.i("onBindViewHolder--voice remote"+body.getRemoteUrl());
        }*/

/*        if (getItemViewType(position) == MESSAGE_TYPE_RECV_IMAGE
                || getItemViewType(position) == MESSAGE_TYPE_SENT_IMAGE) {
            EMImageMessageBody body = (EMImageMessageBody) (messageList.get(position).getBody());


            LogUtil.i("onBindViewHolder--img local" + body.getLocalUrl());
            LogUtil.i("onBindViewHolder--img remote" + body.getRemoteUrl());
            LogUtil.i("onBindViewHolder--img thumbnail local" + body.thumbnailLocalPath());
            LogUtil.i("onBindViewHolder--img thumbnail remote" + body.getThumbnailUrl());

            EMMessage chatMessage = messageList.get(position);
            String sentThuUrl = FileStorage.checkExists(((EMImageMessageBody) chatMessage.getBody()).getLocalUrl()) ? ((EMImageMessageBody) chatMessage.getBody()).getLocalUrl() : ((EMImageMessageBody) chatMessage.getBody()).getThumbnailUrl();

            String reThuUrl = FileStorage.checkExists(((EMImageMessageBody) chatMessage.getBody()).thumbnailLocalPath()) ? ((EMImageMessageBody) chatMessage.getBody()).thumbnailLocalPath() : ((EMImageMessageBody) chatMessage.getBody()).getThumbnailUrl();

            String big=FileStorage.checkExists(((EMImageMessageBody)chatMessage.getBody()).getLocalUrl()) ? ((EMImageMessageBody)chatMessage.getBody()).getLocalUrl(): ((EMImageMessageBody)chatMessage.getBody()).getRemoteUrl();



        }*/


        /*holder.getBinding().getRoot().setOnLongClickListener(v -> {
            setSelectedPosition(position);
            LogUtil.i("onBindViewHolder--");
            return false;
        });*/
    }

    private boolean showTime(int position) {
        if (position == 0) {
            return true;
        }
        EMMessage prevMessage = messageList.get(position - 1);
        if (prevMessage == null) {
            return false;
        }
        long between = messageList.get(position).getMsgTime() - prevMessage.getMsgTime();
        return Math.abs(between) >= TIME_BETWEEN_NEED_TO_SHOW;
    }


    public void refresh(Boolean selectLast) {
        Observable
                .create(new Observable.OnSubscribe<List<EMMessage>>() {
                    @Override
                    public void call(Subscriber<? super List<EMMessage>> subscriber) {
                        subscriber.onNext(receiveHuan.getAllMsg());
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(emMessages -> {
                    if (emMessages.size() != messageList.size()) {
                        messageList = emMessages;
                        notifyDataSetChanged();
                        if (selectLast) {
                            selectLast();
                        }
                    }
                });
    }

    public void refresh() {
        refresh(false);
    }

    public void refreshSend(EMMessage message) {
        messageList.add(message);
        notifyItemChanged(messageList.size());
        selectLast();
    }

    public void loadMore(SwipeRefreshLayout swipeRefreshLayout) {

        if (!hasMore) {
            ToastUtil.showLong(R.string.no_more_messages);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        Observable
                .create(new Observable.OnSubscribe<List<EMMessage>>() {
                    @Override
                    public void call(Subscriber<? super List<EMMessage>> subscriber) {
                        subscriber.onNext(receiveHuan.loadMoreMsgFromDB(messageList.get(0).getMsgId(), pageSize));
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        emMessages -> {
                            if (emMessages.size() < pageSize) {
                                hasMore = false;
                            }

                            emMessages.addAll(messageList);
                            messageList = emMessages;
                            notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        },
                        throwable -> {
                            LogUtil.e("loadMore--", throwable);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                );

    }

    public void selectLast() {
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    public void selectLastDelay() {
        selectLastDelay(300);
    }

    public void selectLastDelay(int time) {
        Observable.just(1)
                .delay(time, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    selectLast();
                });
    }

    public void addMessage(EMMessage message) {
        int i = messageList.size();
        messageList.add(message);
        notifyItemInserted(i);
        selectLastDelay();
    }

    public void addMessage(List<EMMessage> messages) {
        if (messages == null) return;
        int i = messageList.size();
        messageList.addAll(messages);
        notifyDataSetChanged();
        selectLastDelay();
    }

    public void deleteMsg(int position) {
        if (messageList.size() - 1 >= position) {
            receiveHuan.deleteMsg(messageList.get(position));
            messageList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void copyToClipboard(int position) {
        if (messageList.size() - 1 >= position) {
            EMMessageBody body = messageList.get(position).getBody();
            if (body instanceof EMTextMessageBody) {
                AppUtil.copyToClipBoard(((EMTextMessageBody) body).getMessage());
            }
        }
    }

    public void saveImgOrVoice(int position) {
        if (messageList.size() - 1 < position) return;
        EMMessageBody body = messageList.get(position).getBody();
        if (body instanceof EMImageMessageBody) {
            String path = ((EMImageMessageBody) body).getLocalUrl();
            String fileName=((EMImageMessageBody) body).getFileName();
            if (FileStorage.copyFile(path, FileStorage.getImgSaveDir()+"/"+fileName)){
                ToastUtil.showShort(ResUtil.format(R.string.tip_picture_has_save_to,FileStorage.getImgSaveDir()));
            }else {
                MisterApi.getInstance()
                        .downloadFile(((EMImageMessageBody)body)
                        .getRemoteUrl(),FileStorage.getImgSaveDir(),fileName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                aBoolean -> {
                                    if (aBoolean){
                                        ToastUtil.showShort(ResUtil.format(R.string.tip_picture_has_save_to,FileStorage.getImgSaveDir()));
                                    }else {
                                        ToastUtil.showShort(R.string.tip_picture_save_fail);
                                    }
                                },
                                throwable -> {
                                    LogUtil.e("saveImgOrVoice--",throwable);
                                    ToastUtil.showShort(R.string.tip_picture_save_fail);
                                }
                        );
            };
        } else if (body instanceof EMVoiceMessageBody) {
            String path = ((EMVoiceMessageBody) body).getLocalUrl();
            String fileName="voice-"+((EMVoiceMessageBody) body).getFileName();
            if (FileStorage.copyFile(path, FileStorage.getVoiceSaveDir()+"/"+fileName)){
                ToastUtil.showShort(ResUtil.format(R.string.tip_voice_has_save_to,FileStorage.getVoiceSaveDir()));
            }else {
                MisterApi.getInstance()
                        .downloadFile(((EMVoiceMessageBody)body).getRemoteUrl(),FileStorage.getVoiceSaveDir(),fileName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                aBoolean -> {
                                    if (aBoolean){
                                        ToastUtil.showShort(ResUtil.format(R.string.tip_voice_has_save_to,FileStorage.getVoiceSaveDir()));
                                    }else {
                                        ToastUtil.showShort(R.string.tip_voice_save_fail);
                                    }
                                },
                                throwable -> {
                                    LogUtil.e("saveImgOrVoice--",throwable);
                                    ToastUtil.showShort(R.string.tip_voice_save_fail);
                                }
                        );
            };
        }
    }

    public void openRawImg(int position) {
        if (messageList.size() - 1 >= position) {
            EMMessageBody body = messageList.get(position).getBody();
            if (body instanceof EMImageMessageBody) {
                context.startActivity(PictureActivity.newIntentForSave(context, ((EMImageMessageBody) body).getRemoteUrl()));
            }
        }
    }


    /*
     *      上下文菜单
     */
    private int selectedPosition = 0;

    private View.OnCreateContextMenuListener textCreateMenuListener = null;
    private View.OnCreateContextMenuListener imgCreateMenuListener = null;
    private View.OnCreateContextMenuListener voiceCreateMenuListener = null;

    private final static int MENU_DELETE = 10;
    private final static int MENU_COPY_TEXT = 11;
    private final static int MENU_OPEN_BIG_IMG = 12;
    private final static int MENU_SAVE_IMG = 13;
    private final static int MENU_SAVE_VOICE = 14;

    private void initContextMenuListener() {
        textCreateMenuListener = ((menu, v, menuInfo) -> {
            menu.add(0, MENU_DELETE, 0, R.string.delete);
            menu.add(0, MENU_COPY_TEXT, 1, R.string.copy_to_clipboard);
        });
        voiceCreateMenuListener = ((menu, v, menuInfo) -> {
            menu.add(1, MENU_DELETE, 0, R.string.delete);
            menu.add(1, MENU_SAVE_VOICE, 1, R.string.save);
        });
        imgCreateMenuListener = ((menu, v, menuInfo) -> {
            menu.add(2, MENU_DELETE, 0, R.string.delete);
            menu.add(2, MENU_OPEN_BIG_IMG, 1, R.string.open_raw_img);
            menu.add(2, MENU_SAVE_IMG, 2, R.string.save);
        });
    }

    // called  by  Activity.onContextItemSelected()
    public void onMenuSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE: {
                deleteMsg(getSelectedPosition());
                break;
            }
            case MENU_COPY_TEXT: {
                copyToClipboard(getSelectedPosition());
                break;
            }
            case MENU_SAVE_IMG: {
                saveImgOrVoice(getSelectedPosition());
                break;
            }
            case MENU_OPEN_BIG_IMG: {
                openRawImg(getSelectedPosition());
                break;
            }
            case MENU_SAVE_VOICE: {
                saveImgOrVoice(getSelectedPosition());
                break;
            }
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }


    /*
     *      binding holder
     */

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;
        private View.OnCreateContextMenuListener listener;

        public BindingHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public BindingHolder(ViewDataBinding binding, View.OnCreateContextMenuListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            binding.getRoot().setOnCreateContextMenuListener(listener);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }


    public static interface ItemClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    public static class RecyclerItemTouchListener implements RecyclerView.OnItemTouchListener {

        private ItemClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerItemTouchListener(Context context, final RecyclerView recycleView, final ItemClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
