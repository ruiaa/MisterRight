package com.misterright.ui.community;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.misterright.BR;
import com.misterright.R;
import com.misterright.databinding.ActivityCommunityArticleDetailBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.entity.CommunityArticle;
import com.misterright.model.entity.CommunityComment;
import com.misterright.model.entity.CommunityReplyToComment;
import com.misterright.ui.adapter.SimpleRecyclerAdapter;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.ui.widget.MultiSwipeRefreshLayout;
import com.misterright.util.AppUtil;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.ShareUtil;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ArticleDetailActivity extends ToolbarActivity {

    private static final String TOPIC = "topic";
    private static final String DOC_ID = "docId";

    @BindView(R.id.community_article_detail_swipe)
    MultiSwipeRefreshLayout swipeRefresh;
    @BindView(R.id.community_article_detail_scroll)
    NestedScrollView nestedScrollView;
    @BindView(R.id.community_article_detail_comment)
    RecyclerView commentRecycler;
    @BindView(R.id.community_article_detail_input_reply_emotion)
    ImageButton inputReplyEmotion;
    @BindView(R.id.community_article_detail_input_reply_edit)
    EditText inputReplyEdit;
    @BindView(R.id.community_article_detail_input_reply_send)
    Button inputReplySend;
    private ActivityCommunityArticleDetailBinding binding;
    private String topic;
    private String docId;
    private CommunityArticle article;
    private ArrayList<CommunityComment> commentArrayList;
    private SimpleRecyclerAdapter<CommunityComment> commentAdapter;
    private LinearLayoutManager layoutManager;
    private Object sendToTarget = null;

    public static Intent newIntent(Context context, String topic, String docId) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        intent.putExtra(TOPIC, topic);
        intent.putExtra(DOC_ID, docId);
        return intent;
    }

    private void parseIntent() {
        topic = getIntent().getStringExtra(TOPIC);
        docId = getIntent().getStringExtra(DOC_ID);
        if (topic == null || topic.isEmpty()) {
            topic = "love_share";
        }
        if (docId == null || docId.isEmpty()) {
            docId = "1";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_article_detail);
        fixKeyboardAdjustResizeNoEffect();

        ButterKnife.bind(this);

        commentArrayList = new ArrayList<>();

        setToolbar();
        loadArticle();
        loadComment(0);
        initCommentRecycler();
        initSwipeRefresh();

        setKeyBoard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtil.hideKeyBoard(inputReplyEdit);
    }

    private void setToolbar() {
        initToolbar();
        setToolbarLeftShow(v -> {
            onBackPressed();
        });
        toolbar.inflateMenu(R.menu.menu_community_article_detail);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_community_article_detail_share: {
                    ShareUtil.shareText(ArticleDetailActivity.this, "这是标题标题标题标题", "这是扩展内容扩展内容扩展内容扩展内容扩展内容扩展内容");
                    break;
                }
                case R.id.menu_community_article_detail_collect: {
                    break;
                }
            }
            return true;
        });
    }

    private void setKeyBoard() {
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (!isOpen) {
                    sendToTarget = article;
                    inputReplyEdit.setHint(R.string.comment);
                    inputReplyEdit.setText(article == null ? "" : article.waitingToSendComment);
                }
            }
        });
    }

    private void initSwipeRefresh() {
        swipeRefresh.setRefreshHandler(() -> {
            loadComment(0);
        });
        swipeRefresh.setLoadMoreHandler(nestedScrollView, () -> {
            if (commentArrayList.isEmpty()) {
                loadComment(0);
            } else {
                loadComment(commentArrayList.get(commentArrayList.size() - 1).timeStamp);
            }
        });
    }

    private void initCommentRecycler() {
        commentAdapter = new SimpleRecyclerAdapter<>(
                this,
                R.layout.item_community_comment,
                commentArrayList,
                (holder, position, model) -> {
                    model.refreshLeftReplyCount();
                    holder.getBinding().setVariable(BR.comment, model);
                    holder.getBinding().setVariable(BR.leftReplyCount, model.leftReplyCount);

                    RecyclerView recyclerView = (RecyclerView) holder.getBinding().getRoot().findViewById(R.id.item_comment_reply);
                    model.recyclerView = recyclerView;
                    if (model.adapter != null) {
                        recyclerView.setAdapter(model.adapter);
                        if (recyclerView.getLayoutManager() == null) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(ArticleDetailActivity.this, LinearLayoutManager.VERTICAL, true));
                        }
                    }
                },
                (holder, position, model) -> {
                    holder.getBinding().getRoot().setOnClickListener(v -> {
                        sendToTarget = model;
                        AppUtil.showKeyBoard(inputReplyEdit, 0);
                        inputReplyEdit.setHint(ResUtil.getString(R.string.reply) + "  " + model.showName);
                        inputReplyEdit.setText(model.waitingToSendReply);
                    });
                    holder.getBinding().getRoot().findViewById(R.id.item_comment_reply_click_open_more).setOnClickListener(v -> {
                        openMoreReply(model);
                    });
                }
        );
        commentRecycler.setAdapter(commentAdapter);

        //嵌套解决
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);

        commentRecycler.setLayoutManager(layoutManager);
        commentRecycler.setHasFixedSize(true);
        commentRecycler.setNestedScrollingEnabled(false);
    }

    private void initReplyAdapter() {
        for (CommunityComment comment : commentArrayList) {
            if (comment.replyList == null) {
                comment.replyList = new ArrayList<>();
            }
            if (comment.adapter == null) {
                SimpleRecyclerAdapter<CommunityReplyToComment> a =
                        new SimpleRecyclerAdapter<>(
                                this,
                                R.layout.item_community_reply_to_comment,
                                comment.replyList,
                                (holder, position, model) -> {
                                    holder.getBinding().setVariable(BR.replyToComment, model);
                                },
                                (holder, position, model) -> {
                                    holder.getBinding().getRoot().setOnClickListener(v -> {
                                        sendToTarget = model;
                                        AppUtil.showKeyBoard(inputReplyEdit, 0);
                                        inputReplyEdit.setHint(ResUtil.getString(R.string.reply) + "  " + model.showName);
                                        inputReplyEdit.setText(model.waitingToSendReply);
                                    });
                                }
                        );
                comment.adapter = a;
                comment.adapter.notifyDataSetChanged();
            } else {
                comment.adapter.notifyDataSetChanged();
            }
        }
    }

    /*
     *      加载
     */

    private void loadArticle() {
        MisterApi.getInstance()
                .getCommunityArticle(topic, docId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        communityArticle -> {
                            article = communityArticle;
                            sendToTarget = article;
                            binding.setCommunityArticle(article);
                        },
                        throwable -> {
                            LogUtil.e("loadArticleAndInit--", throwable);
                        }
                );
    }

    private void loadComment(long endtime) {
        MisterApi.getInstance()
                .getCommunityComment(topic, docId, 10, endtime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        communityComments -> {
                            merge(communityComments);
                            finishLoad(endtime == 0, true, communityComments.isEmpty());
                        },
                        throwable -> {
                            LogUtil.e("loadComment--", throwable);
                            finishLoad(endtime == 0, false, false);
                        }
                );
    }

    private void loadReply(CommunityComment communityComment, long endtime) {
        MisterApi.getInstance()
                .getCommunityReplayToComment(topic, docId, communityComment.commentId, 10, endtime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        communityReplyToComments -> {
                            merge(communityComment, communityReplyToComments);
                            finishLoad(endtime == 0, true, communityReplyToComments.isEmpty());
                        }, throwable -> {
                            LogUtil.e("load--", throwable);
                            finishLoad(endtime == 0, false, false);
                        }
                );
    }

    private void openMoreReply(CommunityComment communityComment) {
        if (communityComment == null) return;
        long endtime;
        if (communityComment.replyList == null || communityComment.replyList.size() == 0) {
            endtime = 0;
        } else {
            endtime = communityComment.replyList.get(communityComment.replyList.size() - 1).timeStamp;
        }
        loadReply(communityComment, endtime);
    }

    private void finishLoad(boolean isRefresh, boolean success, boolean noMore) {
        if (swipeRefresh != null) {
            if (isRefresh) {
                swipeRefresh.setRefreshing(false);
            } else {
                swipeRefresh.setLoadingMore(false);
                if (noMore) {
                    //ToastUtil.showShort(R.string.no_more);
                }
            }
        }
    }


    /*
     *      合并
     */
    private void merge(ArrayList<CommunityComment> comments) {
        if (comments == null || comments.isEmpty()) return;
        if (commentArrayList.isEmpty()) {
            commentArrayList.addAll(comments);
            initReplyAdapter();
            if (commentAdapter != null) {
                commentAdapter.notifyDataSetChanged();
            }
        } else {
            long topTime = commentArrayList.get(0).timeStamp;
            long endTime = commentArrayList.get(commentArrayList.size() - 1).timeStamp;
            int i = 0;
            for (CommunityComment comment : comments) {
                if (comment.timeStamp > topTime) {
                    commentArrayList.add(i, comment);
                    i = i + 1;
                } else if (comment.timeStamp < endTime) {
                    commentArrayList.add(comment);
                    endTime = comment.timeStamp;
                }
            }
            if (i > 0) {
                nestedScrollView.smoothScrollTo(0, (int) (commentRecycler.getY()));
            }
            initReplyAdapter();
            if (commentAdapter != null) {
                commentAdapter.notifyDataSetChanged();
            }
        }
    }

    private void merge(CommunityComment communityComment, ArrayList<CommunityReplyToComment> replys) {
        if (replys == null || replys.isEmpty()) return;
        if (commentArrayList.contains(communityComment)) {
            if (communityComment.adapter == null) {
                initReplyAdapter();
            } else {
                if (communityComment.replyList.isEmpty()) {
                    communityComment.replyList.addAll(replys);
                } else {
                    long topTime = communityComment.replyList.get(0).timeStamp;
                    long endTime = communityComment.replyList.get(communityComment.replyList.size() - 1).timeStamp;
                    int i = 0;
                    for (CommunityReplyToComment reply : replys) {
                        if (reply.timeStamp > topTime) {
                            communityComment.replyList.add(i, reply);
                            i = i + 1;
                        } else if (reply.timeStamp < endTime) {
                            communityComment.replyList.add(reply);
                            endTime = reply.timeStamp;
                        }
                    }
                }
                communityComment.adapter.notifyDataSetChanged();
                communityComment.refreshLeftReplyCount();

                //nestedScrollView.scrollTo(0,(int)(commentRecycler.getY()));
                //commentRecycler.smoothScrollToPosition(4);
                //layoutManager.scrollToPositionWithOffset(4,0);
            }
        } else {
            loadComment(0);
        }
    }


    /*
     *      回复
     */
    private void comment(final CommunityArticle communityArticle) {
        final String contentText = communityArticle.waitingToSendComment;
        Subscription subscription = MisterApi.getInstance()
                .postCommunityComment(communityArticle.docId, contentText)
                .delay(500L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        communityPostResult -> {
                            //loadComment(0);
                            ArrayList<CommunityComment> list = new ArrayList<CommunityComment>();
                            list.add(new CommunityComment(communityPostResult.replyId, communityArticle.docId, contentText));
                            merge(list);
                        },
                        throwable -> {
                            LogUtil.e("onSendClick--", throwable);
                        }
                );
        addSubscription(subscription);
        communityArticle.waitingToSendComment = "";
    }

    private void replyToComment(CommunityComment communityComment) {
        final String contentText = communityComment.waitingToSendReply;
        Subscription subscription = MisterApi.getInstance()
                .postCommunityReplyToComment(communityComment.docId, communityComment.commentId, null, contentText)
                .delay(500L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        communityPostResult -> {
                            //loadReply(communityComment, 0);
                            ArrayList<CommunityReplyToComment> list = new ArrayList<>();
                            list.add(new CommunityReplyToComment(communityPostResult.replyId, communityComment.docId,
                                    communityComment.commentId, contentText));
                            merge(communityComment, list);
                        },
                        throwable -> {
                            LogUtil.e("onSendClick--", throwable);
                        }
                );
        addSubscription(subscription);
        communityComment.waitingToSendReply = "";
    }

    private void replyToReply(final CommunityReplyToComment reply) {
        final String contentText = reply.waitingToSendReply;
        Subscription subscription = MisterApi.getInstance()
                .postCommunityReplyToComment(reply.docId, reply.commentId, reply.sendUid, reply.waitingToSendReply)
                .delay(500L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        communityPostResult -> {
                            if (commentArrayList == null) {
                                loadComment(0);
                            } else {
                                for (CommunityComment comment : commentArrayList) {
                                    if (comment.commentId.equals(reply.commentId)) {
                                        //loadReply(comment, 0);
                                        ArrayList<CommunityReplyToComment> list = new ArrayList<>();
                                        list.add(new CommunityReplyToComment(communityPostResult.replyId, reply.docId,
                                                reply.commentId, contentText,
                                                reply.showName, reply.sendUid, reply.sex));
                                        merge(comment, list);
                                        return;
                                    }
                                }
                                loadComment(0);
                            }
                        },
                        throwable -> {
                            LogUtil.e("replyToReply--", throwable);
                        }
                );
        addSubscription(subscription);
        reply.waitingToSendReply = "";
    }

    /*
     *      输入 发送监听
     */
    @OnClick(R.id.community_article_detail_input_reply_send)
    public void onSendClick() {
        if (sendToTarget == null) {

        } else if (sendToTarget instanceof CommunityArticle) {
            comment((CommunityArticle) sendToTarget);
        } else if (sendToTarget instanceof CommunityComment) {
            replyToComment((CommunityComment) sendToTarget);
        } else if (sendToTarget instanceof CommunityReplyToComment) {
            replyToReply((CommunityReplyToComment) sendToTarget);
        }
        inputReplyEdit.setText("");
        AppUtil.hideKeyBoard(inputReplyEdit);
        sendToTarget = article;
    }

    @OnTextChanged(R.id.community_article_detail_input_reply_edit)
    public void onEditTextChange(Editable editable) {
        saveInput(editable.toString());
        if (editable.length() <= 0) {
            inputReplySend.setEnabled(false);
        } else {
            inputReplySend.setEnabled(true);
        }
    }

    private void saveInput(String input) {
        if (sendToTarget == null) return;
        if (sendToTarget instanceof CommunityArticle) {
            ((CommunityArticle) sendToTarget).waitingToSendComment = input;
        } else if (sendToTarget instanceof CommunityComment) {
            ((CommunityComment) sendToTarget).waitingToSendReply = input;
        } else if (sendToTarget instanceof CommunityReplyToComment) {
            ((CommunityReplyToComment) sendToTarget).waitingToSendReply = input;
        }
    }


}
