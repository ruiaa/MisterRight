package com.misterright.ui.mine;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentMeBinding;
import com.misterright.ui.DialogHelper;
import com.misterright.ui.MainActivity;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.ResUtil;
import com.misterright.util.storage.FileStorage;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruiaa on 2016/10/30.
 */

public class MeFragment extends ToolbarFragment {

    public static final int ICON_ACCOUNT = R.mipmap.icon_me_account;
    public static final int ICON_WALLET = R.mipmap.icon_me_wallet;
    public static final int ICON_INVITE = R.mipmap.icon_me_invite_friend;
    public static final int ICON_SETTING = R.mipmap.icon_me_setting;
    public static final int ICON_NOTICE = R.mipmap.icon_me_notice;
    public static final int ICON_CLEAN = R.mipmap.icon_me_clean_cache;
    public static final int ICON_FEEDBACK = R.mipmap.icon_me_feedback;

    private MainActivity mainActivity;
    private FragmentMeBinding binding;
    private DialogHelper dialogHelper=null;

    public MeFragment() {

    }

    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
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
        binding = (FragmentMeBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false);
        ButterKnife.bind(this, binding.getRoot());
        initToolbar(binding.getRoot(),mainActivity);
        setTitle(R.string.me);
        return binding.getRoot();
    }

    @OnClick({R.id.me_account, R.id.me_wallet, R.id.me_invite, R.id.me_notice,
            R.id.me_clean_local_cache, R.id.me_feedback})
    public void onItemClick(View view) {
        switch (view.getId()){
            case R.id.me_account:{
                mainActivity.startActivity(new Intent(mainActivity,AccountActivity.class));
                break;
            }
            case R.id.me_wallet : {
                mainActivity.startActivity(new Intent(mainActivity,WalletActivity.class));
                break;
            }
            case R.id.me_invite : {
                mainActivity.startActivity(new Intent(mainActivity,InviteCodeActivity.class));
                break;
            }
            case R.id.me_notice : {
                mainActivity.startActivity(new Intent(mainActivity,NoticeActivity.class));
                break;
            }
            case R.id.me_clean_local_cache : {
                if (dialogHelper==null){
                    dialogHelper=new DialogHelper(mainActivity);
                }
                dialogHelper.showTextPositiveDialog(cleanCache());
                break;
            }
            case R.id.me_feedback : {
                mainActivity.startActivity(new Intent(mainActivity,FeedbackActivity.class));
                break;
            }
        }
    }

    private String cleanCache(){
        double size= FileStorage.cleanImgCache()+FileStorage.cleanVoiceCache();
        if (size<1024){
            return ResUtil.format(R.string.clean_local_cache_finish_and_size," "+size+"B");
        }else if ((size=size/1024)<1024){
            return ResUtil.format(R.string.clean_local_cache_finish_and_size," "+size+"KB");
        }else if ((size=size/1024)<1024){
            return ResUtil.format(R.string.clean_local_cache_finish_and_size," "+size+"MB");
        }else if ((size=size/1024)<1024){
            return ResUtil.format(R.string.clean_local_cache_finish_and_size," "+size+"GB");
        }else {
            size=size/1024;
            return ResUtil.format(R.string.clean_local_cache_finish_and_size," "+size+"TB");
        }
    }
}
