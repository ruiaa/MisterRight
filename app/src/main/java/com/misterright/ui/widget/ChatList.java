package com.misterright.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMMessage;
import com.misterright.R;
import com.misterright.ui.adapter.ChatListAdapter;
import com.misterright.util.ToastUtil;
import com.misterright.util.huanxin.ReceiveHuan;

import java.util.List;

/**
 * Created by ruiaa on 2016/10/31.
 */

public class ChatList extends RelativeLayout {

    private Context  context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;

    public ChatList(Context context) {
        super(context);
        init(context);

    }

    public ChatList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.chat_msg_list,this);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.chart_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(()->{
            swipeRefreshLayout.setRefreshing(false);
        });
        recyclerView=(RecyclerView)findViewById(R.id.chat_recycler);
    }

    public void init(ReceiveHuan receiveHuan){
        receiveHuan.setAsRead();

        chatListAdapter=new ChatListAdapter(receiveHuan,recyclerView,context);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatListAdapter);
        recyclerView.addOnItemTouchListener(new ChatListAdapter.RecyclerItemTouchListener(context,recyclerView,chatListAdapter.itemClickListener));

        chatListAdapter.selectLastDelay(300);
        chatListAdapter.selectLastDelay(500);
        chatListAdapter.selectLastDelay(1000);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ToastUtil.showLong(R.string.no_more_messages);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h<oldh&&chatListAdapter!=null){
            chatListAdapter.selectLast();
        }
    }

    public void addMessage(EMMessage message){
        chatListAdapter.addMessage(message);
    }

    public void addMessage(List<EMMessage> messages){
        chatListAdapter.addMessage(messages);
    }

    public ChatListAdapter getChatListAdapter() {
        return chatListAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }
}
