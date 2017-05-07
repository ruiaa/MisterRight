package com.misterright.ui.community;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.misterright.BR;
import com.misterright.R;
import com.misterright.databinding.FragmentCommunityBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.entity.CommunityTopic;
import com.misterright.ui.MainActivity;
import com.misterright.ui.adapter.SimpleRecyclerAdapter;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.ui.widget.DividerForRecycler;
import com.misterright.ui.widget.ImgViewGlide;
import com.misterright.util.LogUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by ruiaa on 2016/10/30.
 */

public class CommunityFragment extends ToolbarFragment {

    @BindView(R.id.community_topic_banner)
    BGABanner topicBanner;
    @BindView(R.id.community_topic_grid)
    RecyclerView topicList;
    private MainActivity mainActivity;
    private FragmentCommunityBinding binding;
    private CommunityTopic communityTopic;

    public CommunityFragment() {

    }

    public static CommunityFragment newInstance() {
        CommunityFragment fragment = new CommunityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community, container, false);
        ButterKnife.bind(this, binding.getRoot());

        setToolbar();
        //tryApi();
        loadDataAndInit();
        return binding.getRoot();
    }

    private void setToolbar() {
        initToolbar(binding.getRoot(), mainActivity);
        setTitle(R.string.community);
    }

    private void loadDataAndInit() {
        MisterApi.getInstance()
                .getCommunityTopic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        topic -> {
                            communityTopic = topic;
                            initBanner();
                            initGrid();
                        },
                        throwable -> {
                            LogUtil.e("loadDataAndInit--", throwable);
                        }
                );
    }

    private void initBanner() {
        topicBanner.setData(R.layout.item_community_topic_banner, communityTopic.headList, null);
        topicBanner.setAdapter((banner, view, model, position) -> {
            //((ImgViewGlide) view.findViewById(R.id.item_community_topic_banner_img)).setImgUrl(((CommunityTopic.TopicItem)model).url);
            ((ImgViewGlide) view.findViewById(R.id.item_community_topic_banner_img)).setImgUrl("http://pic2.zhimg.com/21ad327c86b996ba637b5b4cf8e8f0f5_b.jpg");

            ((TextView) view.findViewById(R.id.item_community_topic_banner_text)).setText(((CommunityTopic.TopicItem) model).content);
        });
        topicBanner.setOnItemClickListener((banner, view, model, position) -> {

        });
    }

    private void initGrid() {
        ArrayList<CommunityTopic.TopicItem> list = new ArrayList<>();
        list.addAll(communityTopic.topicList);
        list.addAll(communityTopic.topicList);
        list.addAll(communityTopic.topicList);
        list.addAll(communityTopic.topicList);
        list.addAll(communityTopic.topicList);
        list.addAll(communityTopic.topicList);
        list.addAll(communityTopic.topicList);
        list.addAll(communityTopic.topicList);
        int itemHeight = topicList.getHeight() / 3;

        SimpleRecyclerAdapter<CommunityTopic.TopicItem> adapter = new SimpleRecyclerAdapter<CommunityTopic.TopicItem>(
                mainActivity,
                R.layout.item_community_topic_grid,
                list,
                (holder, position, model) -> {
                    holder.getBinding().setVariable(BR.topicItemHeight, itemHeight);
                    holder.getBinding().setVariable(BR.topicItemText, model.content);
                    holder.getBinding().setVariable(BR.topicItemImg, model.url);
                },
                (holder, position, model) -> {
                    holder.getBinding().getRoot().setOnClickListener(v -> {
                        mainActivity.startActivity(ArticleOutlineListActivity.newIntent(mainActivity, model.type));
                    });
                }
        );
        topicList.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mainActivity, 3);
        topicList.setLayoutManager(gridLayoutManager);
        topicList.addItemDecoration(new DividerForRecycler(mainActivity, true,true));

    }

    private void tryApi() {
        MisterApi.getInstance().getCommunityTopic().subscribe();
        MisterApi.getInstance().getCommunityHot("love_share",10,0).subscribe();
        MisterApi.getInstance().getCommunityNew("love_share",10,0).subscribe();
        MisterApi.getInstance().getCommunityArticle("love_share", String.valueOf(1)).subscribe();
        MisterApi.getInstance().getCommunityComment("love_share", String.valueOf(1)).subscribe();
        MisterApi.getInstance().getCommunityReplayToComment("love_share", String.valueOf(1), String.valueOf(1)).subscribe();
    }


}
