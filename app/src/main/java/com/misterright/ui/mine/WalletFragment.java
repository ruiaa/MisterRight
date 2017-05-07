package com.misterright.ui.mine;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentWalletBinding;
import com.misterright.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruiaa on 2016/11/14.
 */

public class WalletFragment extends BaseFragment {

    private FragmentWalletBinding binding;
    private WalletActivity walletActivity;

    public WalletFragment() {

    }

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walletActivity=(WalletActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);
        ButterKnife.bind(this, binding.getRoot());

        binding.setCoinCount(99);
        return binding.getRoot();
    }

    @OnClick(R.id.fragment_wallet_match_two)
    public void onMatchTwoClick() {
        walletActivity.fragmentStack.open(WalletMatchTwoFragment.newInstance());
    }
}
