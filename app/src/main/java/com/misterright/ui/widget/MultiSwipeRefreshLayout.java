package com.misterright.ui.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.misterright.R;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * Created by ruiaa on 2016/10/22.
 */

public class MultiSwipeRefreshLayout extends SwipeRefreshLayout {

    private Context context;
    private boolean isLoadingMore=false;
    private ProgressBar loadProgress;
    private DataHandler refreshHandler;
    private DataHandler loadMoreHandler;

    public MultiSwipeRefreshLayout(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public MultiSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    private void init(){
        setRefreshing(false);
        setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary);
        initLoadProgress();
    }

    private void initLoadProgress(){

    }

    public void setRefreshHandler(final DataHandler refreshHandler){
        this.refreshHandler=refreshHandler;
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (MultiSwipeRefreshLayout.this.refreshHandler != null) {
                    setRefreshing(true);
                    MultiSwipeRefreshLayout.this.refreshHandler.handler();
                }
            }
        });
    }

    public void setLoadMoreHandler(RecyclerView recyclerView ,final DataHandler loadMoreHandler){
        this.loadMoreHandler=loadMoreHandler;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // scroll_state_idle 无拖动 , scroll_state_dragging 拖动 , scroll_state_settling 底部
                if (newState==SCROLL_STATE_SETTLING){
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void setLoadMoreHandler(NestedScrollView nestedScrollView,final DataHandler loadMoreHandler){
        this.loadMoreHandler=loadMoreHandler;
        nestedScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!v.canScrollVertically(0)){
                   loadMore();
                }
                return false;
            }
        });
    }

    public void refresh(){
        if (isRefreshing()) return;
        if (MultiSwipeRefreshLayout.this.refreshHandler != null) {
            setRefreshing(true);
            MultiSwipeRefreshLayout.this.refreshHandler.handler();
        }
    }

    public void loadMore(){
        if (MultiSwipeRefreshLayout.this.loadMoreHandler!=null&&!isLoadingMore()&&!isRefreshing()){
            setLoadingMore(true);
            MultiSwipeRefreshLayout.this.loadMoreHandler.handler();
        }
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public interface DataHandler{
        void handler();
    }

}
