package com.misterright.ui.info;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.misterright.R;
import com.misterright.databinding.FragmentInfoMatchBinding;
import com.misterright.model.MisterData;
import com.misterright.model.StatusMonitor;
import com.misterright.model.entity.UserInfo;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.ui.widget.ImgViewGlide;
import com.misterright.ui.widget.NineImg;
import com.misterright.util.bus.MeetDislikeEvent;
import com.misterright.util.bus.MeetLikeEvent;
import com.misterright.util.bus.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruiaa on 2016/11/11.
 */

public class MatchInfoFragment extends ToolbarFragment {

    private static final String USER_POSITION = "position";

    @BindView(R.id.info_detail_head)
    ImgViewGlide headImg;
    @BindView(R.id.info_detail_img)
    NineImg nineImg;
    @BindView(R.id.info_match_dislike)
    ImageButton buttonDislike;
    @BindView(R.id.info_match_like)
    ImageButton buttonLike;

    private InfoActivity infoActivity;
    private FragmentInfoMatchBinding binding;
    private UserInfo userInfo;
    private int position = 0;

    public MatchInfoFragment() {

    }

    public static MatchInfoFragment newInstance() {
        MatchInfoFragment fragment = new MatchInfoFragment();
        return fragment;
    }

    public static MatchInfoFragment newInstance(int position) {
        MatchInfoFragment fragment = new MatchInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(USER_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoActivity = (InfoActivity) getActivity();
        if (getArguments() != null) {
            position = getArguments().getInt(USER_POSITION, 0);
        }
        userInfo = MisterData.getInstance().status.getMatchUserInfo(position);
        if (userInfo == null) infoActivity.turnBackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info_match, container, false);
        ButterKnife.bind(this, binding.getRoot());

        binding.setMapInfo(userInfo.mapInfo);
        binding.setTime(StatusMonitor.getInstance().waitingCount);

        setToolbar();


        return binding.getRoot();
    }

    private void setToolbar(){
        initToolbar(binding.getRoot(),infoActivity);
        setTitle(R.string.meet);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setToolbarLeftShow(v -> {
            infoActivity.finish();
        });
    }

    @OnClick(R.id.info_match_dislike)
    public void onDisLikeClick() {
        RxBus.getDefault().post(new MeetDislikeEvent(userInfo.uid));
        buttonDislike.setVisibility(View.INVISIBLE);
        buttonLike.setVisibility(View.INVISIBLE);
        infoActivity.finish();
    }

    @OnClick({R.id.info_match_like/*,R.id.info_match_fab*/})
    public void onLikeClick() {
        RxBus.getDefault().post(new MeetLikeEvent(userInfo.uid));
        buttonLike.setVisibility(View.INVISIBLE);
        buttonDislike.setVisibility(View.INVISIBLE);
    }

}
