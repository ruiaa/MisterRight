package com.misterright.ui.status;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentMeetBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.model.StatusMonitor;
import com.misterright.ui.MainActivity;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.ui.info.InfoActivity;
import com.misterright.util.DateUtil;
import com.misterright.util.LogUtil;

/**
 * Created by ruiaa on 2016/10/29.
 */
public class MeetFragment extends ToolbarFragment {

    private MainActivity mainActivity;
    private FragmentMeetBinding binding;

    public MeetFragment(){

    }

    public static MeetFragment newInstance() {
        MeetFragment fragment = new MeetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=(MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=(FragmentMeetBinding) DataBindingUtil
                .inflate(inflater,R.layout.fragment_meet,container,false);

        setToolbar();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        initBindData();
        super.onResume();
    }

    private void initBindData(){
        binding.setMeetInfo(MisterData.getInstance().globalInfo.userInfo);
        binding.setTime(StatusMonitor.getInstance().waitingCount);
        binding.setNextTime(DateUtil.toNoteTime(MisterData.getInstance().status.nextTime));
    }

    private void setToolbar(){
        initToolbar(binding.getRoot(),mainActivity);
        //setHasOptionsMenu(true);
        toolbarTitle.setText(R.string.meet);
        toolbar.inflateMenu(R.menu.menu_meet_self);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_meet_try_match_start : {
                        MisterApi.getInstance().tryStartMatch();
                        break;
                    }
                    case R.id.menu_meet_try_match_stop : {
                        MisterApi.getInstance().tryStopMatch();
                        break;
                    }
                    case R.id.menu_meet_self_mineinfo:{
                        startActivity(InfoActivity.newIntent(mainActivity,InfoActivity.INFO_TYPE_MINE));
                        break;
                    }
                }
                return true;
            }
        });
    }
}
