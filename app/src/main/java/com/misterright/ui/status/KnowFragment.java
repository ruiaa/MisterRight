package com.misterright.ui.status;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.misterright.BR;
import com.misterright.R;
import com.misterright.databinding.FragmentMeetKnowBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.model.StatusMonitor;
import com.misterright.model.entity.MisterNote;
import com.misterright.model.entity.MisterStatus;
import com.misterright.ui.MainActivity;
import com.misterright.ui.adapter.SimpleRecyclerAdapter;
import com.misterright.ui.base.BaseFragment;
import com.misterright.ui.info.InfoActivity;
import com.misterright.ui.widget.BadgeAttach;
import com.misterright.ui.widget.MultiSwipeRefreshLayout;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.bus.NewMsgEvent;
import com.misterright.util.bus.NewNoteEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.huanxin.ReceiveHuan;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by ruiaa on 2016/11/2.
 */

public class KnowFragment extends BaseFragment {

    @BindView(R.id.meet_know_notice)
    View noteNotice;
    @BindView(R.id.meet_know_message)
    View msgNotice;
    @BindView(R.id.meet_know_open_chat_img)
    View openChatImg;
    @BindView(R.id.meet_know_open_chat)
    View openChat;
    @BindView(R.id.meet_know_token_note)
    View openTokenNote;
    @BindView(R.id.meet_know_collapsing)
    CollapsingToolbarLayout bgCollapsing;
    @BindView(R.id.meet_know_appbarlayout)
    AppBarLayout bgAppbarlayout;

    private MainActivity mainActivity;

    private MisterStatus.PairInfo pairInfo;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SimpleRecyclerAdapter<MisterNote> adapter;
    private List<MisterNote> misterNotes;

    private BadgeAttach noteNoticeBadgeAttach;
    private BadgeAttach msgNoticeBadgeAttach;
    private BadgeAttach chatOpenImgBadgeAttach;
    // private BadgeItem bottomBadgeItem0;
    private BadgeAttach bottomBadgeItem0;
    private int newMsg = 0;
    private int newNote = 0;

    public KnowFragment() {

    }

    public static KnowFragment newInstance() {
        KnowFragment fragment = new KnowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MisterApi.getInstance().setPairUserInfo();

        mainActivity = (MainActivity) getActivity();
        pairInfo = MisterData.getInstance().status.pairInfo;
        misterNotes = new ArrayList<>();
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(MisterData.getInstance().otherUser.hxUser);
        if (conversation == null) {
            newMsg = 0;
        } else {
            newMsg = conversation.getUnreadMsgCount();
        }

        Subscription subscription1 = RxBus.getDefault().toObservable(NewMsgEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        newMsgEvent -> {
                            if (newMsgEvent.newCount < 0) {
                                newMsg = 0;
                            } else {
                                newMsg = newMsg + newMsgEvent.newCount;
                            }
                            //LogUtil.i("NewMsgEvent--" + newMsgEvent.newCount);
                            resetBadgeContent();
                        }
                );
        Subscription subscription2 = RxBus.getDefault().toObservable(NewNoteEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        newNoteEvent -> {
                            if (newNoteEvent.forceRefresh && swipeRefreshLayout != null) {
                                swipeRefreshLayout.refresh();
                            } else if (newNoteEvent.newCount < 0) {
                                newNote = 0;
                            } else {
                                newNote = newNote + newNoteEvent.newCount;
                            }
                            //LogUtil.i("NewNoteEvent--" + newNoteEvent.newCount);
                            resetBadgeContent();
                        }
                );
        mainActivity.addSubscription(subscription1);
        mainActivity.addSubscription(subscription2);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMeetKnowBinding binding = (FragmentMeetKnowBinding) DataBindingUtil
                .inflate(inflater, R.layout.fragment_meet_know, container, false);
        ButterKnife.bind(this, binding.getRoot());

        mainActivity.binding.mainTabMeetText.setText(R.string.know);

        initToolbar(binding.getRoot());
        initBadge();

        swipeRefreshLayout = binding.meetKnowNoteswipe;
        recyclerView = binding.meetKnowNotelist;
        initNoteList();

        resetBadgeContent();
        setListener();

        return binding.getRoot();
    }

    private void initToolbar(View view) {
        Toolbar toolbar;
        TextView toolbarTile;

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setContentInsetStartWithNavigation(100);
        toolbarTile = (TextView) view.findViewById(R.id.toolbar_title);
        //setHasOptionsMenu(true);
        toolbarTile.setText(R.string.know);
        toolbar.inflateMenu(R.menu.menu_meet_know);
        toolbar.setOverflowIcon(ResUtil.getDrawable(R.mipmap.icon_nav_menu));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_meet_know_delete: {
                        MisterApi.getInstance()
                                .relationDelete(pairInfo.targetUid)
                                .subscribe(
                                        actionResult -> {
                                            (new ReceiveHuan(MisterData.getInstance().otherUser.hxUser)).deleteThisConversation();
                                            StatusMonitor.getInstance().startMonitor();
                                        },
                                        throwable -> {
                                            LogUtil.e("onMenuItemClick--", throwable);
                                        }
                                );
                        break;
                    }
                    case R.id.menu_meet_know_otherinfo: {
                        startActivity(InfoActivity.newIntent(mainActivity, InfoActivity.INFO_TYPE_PAIR));
                        break;
                    }
                    case R.id.menu_meet_know_mineinfo: {
                        startActivity(InfoActivity.newIntent(mainActivity, InfoActivity.INFO_TYPE_MINE));
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void initBadge() {
        //bottomBadgeItem0 = mainActivity.badgeItem0.hide(false);
        bottomBadgeItem0=BadgeAttach.createCircle(mainActivity).setMargin(0,4,13,0).bind(mainActivity.binding.mainTabMeetImg).hide();
        noteNoticeBadgeAttach = BadgeAttach.createDot(mainActivity).setMargin(0,4,4,0).bind(noteNotice).hide();
        msgNoticeBadgeAttach = BadgeAttach.createCircle(mainActivity).setMargin(0,6,7,0).bind(msgNotice).hide();
        chatOpenImgBadgeAttach = BadgeAttach.createCircle(mainActivity).setMargin(0,0,6,0).bind(openChatImg).hide();
    }

    private void resetBadgeContent() {

        if (newNote > 0) {
            noteNoticeBadgeAttach.show();
        } else {
            noteNoticeBadgeAttach.hide();
        }

        if (newMsg > 10) {
            msgNoticeBadgeAttach.setBadgeCount(".").show();
            chatOpenImgBadgeAttach.setBadgeCount(".").show();
        } else if (newMsg > 0) {
            msgNoticeBadgeAttach.setBadgeCount(newMsg).show();
            chatOpenImgBadgeAttach.setBadgeCount(newMsg).show();
        } else {
            msgNoticeBadgeAttach.hide();
            chatOpenImgBadgeAttach.hide();
        }

        int max = Math.max(newMsg, newNote);
        if (max > 10) {
            bottomBadgeItem0.setBadgeCount("...").show();
        } else if (max > 0) {
            bottomBadgeItem0.setBadgeCount(String.valueOf(max)).show();
        } else {
            bottomBadgeItem0.hide();
        }
/*
        noteNoticeBadgeAttach.setBadgeCount(1).show();
        msgNoticeBadgeAttach.setBadgeCount(1).show();
        chatOpenImgBadgeAttach.setBadgeCount(1).show();
        bottomBadgeItem0.setBadgeCount(".").show();*/
    }

    private void setListener() {
        msgNotice.setOnClickListener(v -> {
            startActivity(ChatActivity.newIntent(mainActivity, MisterData.getInstance().otherUser.hxUser));
        });
        openChat.setOnClickListener(v -> {
            startActivity(ChatActivity.newIntent(mainActivity, MisterData.getInstance().otherUser.hxUser));
        });
        openTokenNote.setOnClickListener(v -> {
            startActivity(new Intent(mainActivity, TakeNoteActivity.class));
        });
        bgAppbarlayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset < (-appBarLayout.getHeight() + 10)) {
                if (msgNotice.getVisibility() == View.GONE) {
                    msgNotice.setVisibility(View.VISIBLE);
                    resetBadgeContent();
                }
            } else if (verticalOffset == 0) {
                if (msgNotice.getVisibility() == View.VISIBLE) {
                    msgNotice.setVisibility(View.GONE);
                    resetBadgeContent();
                }
            }
        });
    }


    private void initNoteList() {
        adapter = new SimpleRecyclerAdapter<>(
                mainActivity,
                R.layout.item_note,
                misterNotes,
                (holder, position, model) -> {
                    holder.getBinding().setVariable(BR.note, model);
                }
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setAdapter(adapter);


        MisterApi.getInstance()
                .getNote()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        misterNotes1 -> {
                            misterNotes.addAll(misterNotes1);
                            adapter.notifyDataSetChanged();
                        },
                        throwable -> {
                            LogUtil.e("initNoteList--", throwable);
                        }
                );

        swipeRefreshLayout.setLoadMoreHandler(
                recyclerView,
                new MultiSwipeRefreshLayout.DataHandler() {
                    @Override
                    public void handler() {
                        long time = 0;
                        if (!misterNotes.isEmpty()) {
                            time = misterNotes.get(misterNotes.size() - 1).time;
                        }
                        MisterApi.getInstance()
                                .getNote(time)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        misterNotesMore -> {
                                            if (!misterNotesMore.isEmpty()) {
                                                misterNotes.addAll(misterNotesMore);
                                                adapter.notifyDataSetChanged();
                                            }
                                            swipeRefreshLayout.setLoadingMore(false);
                                        },
                                        throwable -> {
                                            swipeRefreshLayout.setLoadingMore(false);
                                            LogUtil.e("handler--LoadMore", throwable);
                                        }
                                );
                    }
                }
        );

        swipeRefreshLayout.setRefreshHandler(
                new MultiSwipeRefreshLayout.DataHandler() {
                    @Override
                    public void handler() {
                        MisterApi.getInstance()
                                .getNote()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        misterNotes3 -> {
                                            RxBus.getDefault().post(new NewNoteEvent(-1));
                                            swipeRefreshLayout.setRefreshing(false);
                                            if (misterNotes3.isEmpty()) return;
                                            if (misterNotes.isEmpty()) {
                                                misterNotes.addAll(misterNotes3);
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                long topTime = misterNotes.get(0).time;
                                                int i = 0;
                                                for (MisterNote note : misterNotes3) {
                                                    if (note.time > topTime) {
                                                        misterNotes.add(i, note);
                                                        i = i + 1;
                                                    } else {
                                                        break;
                                                    }
                                                }
                                                if (i != 0) {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        },
                                        throwable -> {
                                            swipeRefreshLayout.setRefreshing(false);
                                            LogUtil.e("handler--Refresh", throwable);
                                        }
                                );
                    }
                }
        );
    }
}
