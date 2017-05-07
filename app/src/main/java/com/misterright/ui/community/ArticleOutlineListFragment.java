package com.misterright.ui.community;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.BR;
import com.misterright.R;
import com.misterright.databinding.FragmentCommunityArticleOutlineBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.entity.CommunityArticle;
import com.misterright.ui.adapter.SimpleRecyclerAdapter;
import com.misterright.ui.base.BaseFragment;
import com.misterright.ui.widget.DividerForRecycler;
import com.misterright.ui.widget.MultiSwipeRefreshLayout;
import com.misterright.util.LogUtil;
import com.misterright.util.bus.PostCommunityArticleEvent;
import com.misterright.util.bus.RxBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by ruiaa on 2016/11/24.
 */

public class ArticleOutlineListFragment extends BaseFragment {
    private static final String TYPE = "type";
    private static final String TOPIC = "topic";
    public static final int HOT_TYPE = 1;
    public static final int NEW_TYPE = 2;

    @BindView(R.id.community_article_outline_list_recycler)
    RecyclerView articleRecycler;
    @BindView(R.id.community_article_outline_list_refresh)
    MultiSwipeRefreshLayout swipeRefresh;
    private FragmentCommunityArticleOutlineBinding binding;
    private ArticleOutlineListActivity articleOutlineListActivity;
    private int type = HOT_TYPE;
    private String topic;
    private SimpleRecyclerAdapter<CommunityArticle> adapter;
    private ArrayList<CommunityArticle> articleArrayList;

    public ArticleOutlineListFragment() {

    }

    public static ArticleOutlineListFragment newInstance(int type, String topic) {
        ArticleOutlineListFragment fragment = new ArticleOutlineListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        bundle.putString(TOPIC, topic);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);
            topic = getArguments().getString(TOPIC);
        }
        articleOutlineListActivity = (ArticleOutlineListActivity) getActivity();
        articleArrayList=new ArrayList<>();

        receiveNewArticle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community_article_outline, container, false);
        ButterKnife.bind(this, binding.getRoot());

        initRecycler();
        initSwipeRefresh();

        return binding.getRoot();
    }

    private void initRecycler() {
        adapter = new SimpleRecyclerAdapter<CommunityArticle>(
                articleOutlineListActivity,
                R.layout.item_community_article_outline,
                articleArrayList,
                (holder, position, model) -> {
                    holder.getBinding().setVariable(BR.communityArticle, model);
                },
                (holder, position, model) -> {
                    holder.getBinding().getRoot().setOnClickListener(v -> {
                        articleOutlineListActivity.startActivity(ArticleDetailActivity.newIntent(
                                articleOutlineListActivity, topic, model.docId));
                    });
                }
        );
        articleRecycler.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(articleOutlineListActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        articleRecycler.setLayoutManager(linearLayoutManager);
        articleRecycler.addItemDecoration(new DividerForRecycler(articleOutlineListActivity,false,true));
    }

    private void initSwipeRefresh() {
        if (articleArrayList.isEmpty()) {
            MisterApi.getInstance()
                    .getCommunityArticleOutline(topic, type == HOT_TYPE,10,System.currentTimeMillis()/10)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            communityArticles -> {
                                articleArrayList.addAll(communityArticles);
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            },
                            throwable -> {
                                LogUtil.e("initSwipeRefresh--", throwable);
                                swipeRefresh.setRefreshing(false);
                            }
                    );
        }
        swipeRefresh.setRefreshHandler(() -> {
            MisterApi.getInstance()
                    .getCommunityArticleOutline(topic, type == HOT_TYPE,10,System.currentTimeMillis()/1000)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            communityArticles -> {
                                refresh(communityArticles);
                                swipeRefresh.setRefreshing(false);
                            },
                            throwable -> {
                                LogUtil.e("initSwipeRefresh--", throwable);
                                swipeRefresh.setRefreshing(false);
                            }
                    );
        });
        swipeRefresh.setLoadMoreHandler(articleRecycler, () -> {
            long endTime=0;
            if (articleArrayList!=null&&!articleArrayList.isEmpty()){
                endTime=articleArrayList.get(articleArrayList.size()-1).timeStamp;
            }
            MisterApi.getInstance()
                    .getCommunityArticleOutline(topic,type==HOT_TYPE,10,endTime)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            communityArticles -> {
                                loadMore(communityArticles);
                                swipeRefresh.setLoadingMore(false);
                            },
                            throwable -> {
                                LogUtil.e("initSwipeRefresh--",throwable);
                                swipeRefresh.setLoadingMore(false);
                            }
                    );
        });
    }

    private void refresh(ArrayList<CommunityArticle> communityArticles){
        if (communityArticles==null||communityArticles.isEmpty()) return;
        if (articleArrayList.isEmpty()){
            articleArrayList.addAll(communityArticles);
            adapter.notifyDataSetChanged();
        }else if (type==HOT_TYPE){

        }else if (type==NEW_TYPE){

        }
    }

    private void loadMore(ArrayList<CommunityArticle> communityArticles){
        if (communityArticles==null||communityArticles.isEmpty()) return;
        articleArrayList.addAll(communityArticles);
        adapter.notifyDataSetChanged();
    }

    private void receiveNewArticle(){
        if (type==HOT_TYPE) return;
        Subscription subscription= RxBus.getDefault().toObservable(PostCommunityArticleEvent.class)
                .subscribe(
                        postCommunityArticleEvent -> {
                            if (articleArrayList!=null){
                                articleArrayList.add(0,postCommunityArticleEvent.article);
                                if (adapter!=null){
                                    adapter.notifyItemInserted(0);
                                }
                            }
                        },
                        throwable -> {
                            LogUtil.e("receiveNewArticle--",throwable);
                        }
                );
        articleOutlineListActivity.addSubscription(subscription);
    }

}
