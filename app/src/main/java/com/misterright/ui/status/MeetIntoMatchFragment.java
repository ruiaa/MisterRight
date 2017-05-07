package com.misterright.ui.status;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableLong;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentMeetIntoMatchBinding;
import com.misterright.model.MisterData;
import com.misterright.model.StatusMonitor;
import com.misterright.ui.MainActivity;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.bus.MeetJoinEvent;
import com.misterright.util.bus.RxBus;

/**
 * Created by ruiaa on 2016/11/2.
 */

public class MeetIntoMatchFragment extends ToolbarFragment {

    private MainActivity mainActivity;
    private FragmentMeetIntoMatchBinding binding;
    private ObservableLong successCount;

    public MeetIntoMatchFragment(){

    }

    public static MeetIntoMatchFragment newInstance() {
        MeetIntoMatchFragment fragment = new MeetIntoMatchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=(MainActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    binding=(FragmentMeetIntoMatchBinding) DataBindingUtil
                .inflate(inflater, R.layout.fragment_meet_into_match,container,false);

        setToolbar();
        successCount=new ObservableLong();
        successCount.set(MisterData.getInstance().status.successCount);

        binding.setTime(StatusMonitor.getInstance().waitingCount);
        binding.meetJoinMatch.setOnClickListener(v->{
            RxBus.getDefault().post(new MeetJoinEvent());
            binding.setHadJoin(true);
            binding.setMeetSuccess(successCount);
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        successCount.set(MisterData.getInstance().status.successCount);
        super.onResume();
    }

    private void setToolbar(){
        initToolbar(binding.getRoot(),mainActivity);
        //setHasOptionsMenu(true);
        toolbarTitle.setText(R.string.meet);
        //toolbar.inflateMenu(R.menu.menu_meet);
        toolbar.setBackgroundColor(ResUtil.getColor(R.color.transparent));
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_meet_mine:{
                    ToastUtil.showLong("mine");
                }
            }
            return true;
        });
    }

}
