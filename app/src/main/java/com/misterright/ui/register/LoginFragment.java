package com.misterright.ui.register;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.misterright.R;
import com.misterright.databinding.FragmentRegisterLoginBinding;
import com.misterright.ui.MainActivity;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/11/19.
 */

public class LoginFragment extends ToolbarFragment {

    @BindView(R.id.login_input_phone)
    EditText inputPhone;
    @BindView(R.id.login_input_pwd)
    EditText inputPwd;
    @BindView(R.id.login_login)
    Button loginButton;
    private LoginActivity loginActivity;
    private FragmentRegisterLoginBinding binding;

    public LoginFragment() {

    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity = (LoginActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_login, container, false);
        ButterKnife.bind(this, binding.getRoot());
        init();
        //pair 13138705415
        inputPhone.setText("15202019649");

        //mx3
        //inputPhone.setText("13430208680"); 有问题
        //inputPhone.setText("15728675460");
        //mx6
        //inputPhone.setText("13138471489");
        return binding.getRoot();
    }

    private void init() {
        initToolbar(binding.getRoot(), loginActivity);
        setToolbarTransparent();
        setToolbarLeftShow(v -> {
            loginActivity.fragmentStack.turnBackFragment();
        });
    }

    @OnClick(R.id.login_login)
    public void onLoginClick() {
        loginUsePwd(inputPhone.getText().toString(), inputPwd.getText().toString());
        loginButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.login_forget_pwd)
    public void onForgetPwdClick() {
        loginActivity.fragmentStack.open(FindPwdFragment.newInstance());
    }

    @OnTextChanged(R.id.login_input_phone)
    public void onInputPhone(Editable editable){

    }


    private int errorLoginCount = 0;


    private void loginFail(){
        loginButton.setVisibility(View.VISIBLE);
    }

    private void loginUsePwd(final String phone, final String pwd) {
        LogUtil.i("loginUsePwd--");

        loginActivity
                .login(phone, pwd, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        aBoolean -> {
                            if (aBoolean) {
                                startActivity(new Intent(loginActivity, MainActivity.class));
                                loginActivity.finish();
                            } else {
                                loginFail();
                            }
                        }, throwable -> {
                            loginFail();
                            LogUtil.e("loginUsePwd--", throwable);
                        }
                );
    }
}
