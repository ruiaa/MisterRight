package com.misterright.ui.mine;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.misterright.MisterConfig;
import com.misterright.R;
import com.misterright.databinding.ActivityMeAccountBinding;
import com.misterright.model.MisterData;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.ui.register.LoginActivity;
import com.misterright.util.bus.LogoutEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.storage.KeyStorage;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMeAccountBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_me_account);
        ButterKnife.bind(this);

        binding.setAccount(MisterData.getInstance().phone);


        initToolbar();
        setTitle(R.string.account);
    }

    @Override
    protected boolean canTurnBack() {
        return true;
    }

    @OnClick(R.id.me_account_change_pwd)
    public void onChangePwdClick() {
        startActivity(new Intent(this,ResetPwdActivity.class));
    }

    @OnClick(R.id.me_account_logout)
    public void onLogoutClick() {
        KeyStorage.delete(MisterConfig.KEY_MISTER_DATA);
        RxBus.getDefault().post(new LogoutEvent(LoginActivity.class));
        KeyStorage.delete(MisterConfig.KEY_MISTER_DATA);
        finish();
    }
}
