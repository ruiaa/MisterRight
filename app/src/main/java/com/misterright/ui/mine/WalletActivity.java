package com.misterright.ui.mine;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.misterright.R;
import com.misterright.databinding.ActivityMeWalletBinding;
import com.misterright.ui.FragmentStack;
import com.misterright.ui.base.ToolbarActivity;

public class WalletActivity extends ToolbarActivity {

    public FragmentStack fragmentStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMeWalletBinding binding= DataBindingUtil.setContentView(this, R.layout.activity_me_wallet);

        initToolbar();
        setToolbarLeftShow(v -> {
            fragmentStack.turnBackFragment();
        });
        setTitle(R.string.wallet);

        fragmentStack=new FragmentStack(this,R.id.me_wallet_frame);
        if (savedInstanceState==null){
            fragmentStack.open(WalletFragment.newInstance());
        }
    }

}
