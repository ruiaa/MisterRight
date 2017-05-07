package com.misterright.ui.mine;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.misterright.R;
import com.misterright.databinding.ActivityMeInviteBinding;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.util.AppUtil;
import com.misterright.util.ResUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteCodeActivity extends ToolbarActivity {

    private ActivityMeInviteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_me_invite);
        ButterKnife.bind(this);

        initToolbar();
        setTitle(R.string.invite_friend);
        setToolbarLeftShow(v -> {
            onBackPressed();
        });

    }


    @OnClick(R.id.me_invite_copy_cody)
    public void onCopyClick() {
        AppUtil.copyToClipBoard("12934",String.format(ResUtil.getString(R.string.copy_invite_code_ok),"12934"));
    }

    @OnClick(R.id.me_invite_get_new_code)
    public void onGetCodeClick() {

    }
}
