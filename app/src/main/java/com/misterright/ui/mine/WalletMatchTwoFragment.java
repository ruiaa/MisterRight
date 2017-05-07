package com.misterright.ui.mine;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentWalletMatchTwoBinding;
import com.misterright.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruiaa on 2016/11/14.
 */

public class WalletMatchTwoFragment extends BaseFragment {

    private FragmentWalletMatchTwoBinding binding;
    private int selectedIndex = 0;

    public WalletMatchTwoFragment() {

    }

    public static WalletMatchTwoFragment newInstance() {
        WalletMatchTwoFragment fragment = new WalletMatchTwoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet_match_two, container, false);
        ButterKnife.bind(this, binding.getRoot());
        init();
        return binding.getRoot();
    }

    private void init(){
        binding.setSelectIndex(selectedIndex);
    }

    @OnClick(R.id.fragment_wallet_match_two_sure)
    public void onSureBuyClick() {
    }

    @OnClick({R.id.fragment_wallet_match_two_one_day,
            R.id.fragment_wallet_match_two_one_month,
            R.id.fragment_wallet_match_two_three_month})
    public void onBuyTypeSelect(View view) {
        switch (view.getId()) {
            case R.id.fragment_wallet_match_two_one_month: {
                selectedIndex=0;
                break;
            }
            case R.id.fragment_wallet_match_two_three_month: {
                selectedIndex=1;
                break;
            }
            case R.id.fragment_wallet_match_two_one_day: {
                selectedIndex=2;
                break;
            }
        }
        init();
    }

    public static int getBuyTypeIcon(int day) {
        if (day == 1) {
            return R.mipmap.wallet_day_one;
        } else if (day / 30 == 1) {
            return R.mipmap.wallet_month_one;
        } else if (day / 30 == 3) {
            return R.mipmap.wallet_month_three;
        } else {
            return R.mipmap.wallet_day_one;
        }
    }

    public static String getBuyTypeTip(int day) {
        if (day == 1) {
            return "1T";
        } else if (day / 30 == 1) {
            return "10T";
        } else if (day / 30 == 3) {
            return "30T";
        } else {
            return "1T";
        }
    }

    public static int getHeadImg() {
        return R.mipmap.wallet_buy_banner_img;
    }

}
