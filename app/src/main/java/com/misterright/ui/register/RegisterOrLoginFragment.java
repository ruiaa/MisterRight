package com.misterright.ui.register;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentRegisterRegisterOrLoginBinding;
import com.misterright.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruiaa on 2016/11/19.
 */

public class RegisterOrLoginFragment extends BaseFragment {

    private LoginActivity loginActivity;
    private FragmentRegisterRegisterOrLoginBinding binding;

    public RegisterOrLoginFragment() {

    }

    public static RegisterOrLoginFragment newInstance() {
        RegisterOrLoginFragment fragment = new RegisterOrLoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity=(LoginActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_register_or_login, container, false);
        ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }



    @OnClick(R.id.register_or_login_register)
    public void onRegisterClick() {
        loginActivity.fragmentStack.open(CheckInviteFragment.newInstance());
    }

    @OnClick(R.id.register_or_login_login)
    public void onLoginClick() {
        loginActivity.fragmentStack.open(LoginFragment.newInstance());
    }
}
