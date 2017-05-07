package com.misterright.ui.status;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.misterright.R;
import com.misterright.databinding.FragmentMeetMatchBinding;
import com.misterright.model.MisterData;
import com.misterright.model.StatusMonitor;
import com.misterright.model.entity.UserInfo;
import com.misterright.ui.MainActivity;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.ui.info.InfoActivity;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.bus.MeetDislikeEvent;
import com.misterright.util.bus.MeetLikeEvent;
import com.misterright.util.bus.MeetMatchChangeEvent;
import com.misterright.util.bus.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetMatchFragment extends ToolbarFragment {

    @BindView(R.id.meet_match_dislike)
    ImageButton buttonDislike;
    @BindView(R.id.meet_match_like)
    ImageButton buttonLike;
    @BindView(R.id.meet_match_open_info)
    ImageButton buttonOpenInfo;
    private MainActivity mainActivity;
    FragmentMeetMatchBinding binding;
    private UserInfo info1;

    public MeetMatchFragment() {

    }

    public static MeetMatchFragment newInstance() {
        MeetMatchFragment fragment = new MeetMatchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = (FragmentMeetMatchBinding) DataBindingUtil
                .inflate(inflater, R.layout.fragment_meet_match, container, false);
        ButterKnife.bind(this, binding.getRoot());
        setToolbar();

        info1 = MisterData.getInstance().status.matchUserInfos.get(0);
        binding.setOtherInfo(info1);

        binding.setTime(StatusMonitor.getInstance().waitingCount);
        binding.setMatchDegree(String.valueOf(98));


        RxBus.getDefault().toObservable(MeetMatchChangeEvent.class)
                .subscribe(meetChangeMatchEvent -> {
                    info1 = MisterData.getInstance().status.matchUserInfos.get(0);
                    binding.setOtherInfo(info1);
                    buttonLike.setVisibility(View.VISIBLE);
                    buttonDislike.setVisibility(View.VISIBLE);
                });

        return binding.getRoot();
    }


    private void setToolbar() {
        initToolbar(binding.getRoot(), mainActivity);
        //setHasOptionsMenu(true);
        toolbarTitle.setText(R.string.meet);
        toolbar.setBackgroundColor(ResUtil.getColor(R.color.transparent));
        //toolbar.inflateMenu(R.menu.menu_meet);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_meet_mine: {
                        ToastUtil.showLong("mine");
                    }
                }
                return true;
            }
        });
    }

    @OnClick(R.id.meet_match_like)
    public void onLikeClick() {
        RxBus.getDefault().post(new MeetLikeEvent(info1.uid));
        buttonDislike.setVisibility(View.INVISIBLE);
        buttonLike.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.meet_match_dislike)
    public void onDislikeClick() {
        RxBus.getDefault().post(new MeetDislikeEvent(info1.uid));
        buttonDislike.setVisibility(View.INVISIBLE);
        buttonLike.setVisibility(View.INVISIBLE);
        buttonOpenInfo.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.meet_match_open_info)
    public void onOpneInfoClick() {
        mainActivity.startActivity(InfoActivity.newIntent(mainActivity, InfoActivity.INFO_TYPE_MATCH));
    }
}
