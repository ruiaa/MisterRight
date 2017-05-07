package com.misterright.ui.mine;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.misterright.R;
import com.misterright.databinding.ActivityMeResetpwdBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.ui.register.LoginActivity;
import com.misterright.util.AppUtil;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.bus.LogoutEvent;
import com.misterright.util.bus.RxBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;

public class ResetPwdActivity extends ToolbarActivity {

    public static final int MAX_PWD_COUNT = 16;
    public static final int MIN_PWD_COUNT = 6;
    public static final int MAX_WORRY_COUNT = 100;

    @BindView(R.id.resetpwd_old_password)
    EditText oldPassword;
    @BindView(R.id.resetpwd_new_password)
    EditText newPassword;
    @BindView(R.id.resetpwd_new_password_repeat)
    EditText newPasswordRepeat;

    private ActivityMeResetpwdBinding binding;
    private MaterialDialog dialogOldWorry;
    private MaterialDialog dialogReseting;
    private MaterialDialog dialogNoPwd;
    private int errorCount = 0;
    private int errorResetCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_me_resetpwd);
        ButterKnife.bind(this);

        initToolbar();
        setToolbarLeftShow(v -> {
            finish();
        });
        setToolbarRightText(R.string.finish, v -> {
            if (checkOldPwd() && checkNewPwd() && checkRepeat()) {
                resetPwd();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtil.showKeyBoard(oldPassword);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtil.hideKeyBoard(binding.getRoot());
    }

    private void resetPwd() {
        if (dialogReseting == null) {
            dialogReseting = new MaterialDialog.Builder(this)
                    .title(R.string.reseting_password)
                    .progress(true, 0)
                    .build();
            dialogReseting.setCanceledOnTouchOutside(false);
        }
        dialogReseting.show();
        MisterApi.getInstance()
                .reSetPwd(oldPassword.getText().toString(), newPasswordRepeat.getText().toString())
                .delay(1000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resetPwdResult -> {
                            dialogReseting.dismiss();
                            if (resetPwdResult.result) {
                                ToastUtil.showLong(R.string.reset_password_success);
                                finish();
                            } else {
                                pwdNoEffect();
                            }
                        },
                        throwable -> {
                            errorResetCount++;
                            if (errorResetCount>5){
                                dialogReseting.dismiss();
                                pwdNoEffect();
                            }else {
                                resetPwd();
                            }
                            LogUtil.e("resetPwd--", throwable);
                        }
                );
    }

    private void pwdNoEffect() {
        if (dialogNoPwd == null) {
            dialogNoPwd = new MaterialDialog.Builder(this)
                    .title(R.string.reset_password_no_effect)
                    .positiveText(R.string.sure)
                    .onPositive((dialog, which) -> {
                        RxBus.getDefault().post(new LogoutEvent(LoginActivity.class));
                        finish();
                    })
                    .build();
            dialogNoPwd.setCanceledOnTouchOutside(false);
        }
        dialogNoPwd.show();
    }

    private boolean checkOldPwd() {
        if (MisterData.getInstance().pwd.equals(oldPassword.getText().toString())) {
            return true;
        } else {
            errorCount++;
            if (dialogOldWorry == null) {
                dialogOldWorry = new MaterialDialog.Builder(this)
                        .content(ResUtil.getString(R.string.worry_old_password)
                                + "\n"
                                + String.format(ResUtil.getString(R.string.worry_old_password_count), errorCount)
                        )
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(R.string.sure)
                        .onPositive((dialog, which) -> {
                            AppUtil.showKeyBoard(oldPassword);
                        })
                        .build();
            }
            dialogOldWorry.show();
            return false;
        }
    }

    private boolean checkNewPwd() {
        if (newPassword.getText().toString().length() >= MIN_PWD_COUNT) {
            return true;
        } else {
            ToastUtil.showShort(String.format(ResUtil.getString(R.string.worry_new_password_less), MIN_PWD_COUNT));
            AppUtil.showKeyBoard(newPassword);
            return false;
        }
    }

    private boolean checkRepeat() {
        if (newPassword.getText().toString().equals(newPasswordRepeat.getText().toString())) {
            return true;
        } else {
            ToastUtil.showShort(R.string.worry_reset_password_difference);
            AppUtil.showKeyBoard(newPasswordRepeat);
            return false;
        }
    }

    @OnTextChanged(R.id.resetpwd_old_password)
    public void onOldPwdInput(Editable editable) {
        LogUtil.i("onOldPwdInput--" + editable.toString());
    }

    @OnTextChanged(R.id.resetpwd_new_password)
    public void onNewPwdInput(Editable editable) {

    }

    @OnTextChanged(R.id.resetpwd_new_password_repeat)
    public void onRepeatInput(Editable editable) {

    }

    @OnFocusChange(R.id.resetpwd_old_password)
    public void onOldPwdFocus(boolean b) {
        if (!b) {
            checkOldPwd();
        }
    }

    @OnFocusChange(R.id.resetpwd_new_password)
    public void onNewPwdFocus(boolean b) {

    }

    @OnFocusChange(R.id.resetpwd_new_password_repeat)
    public void onRepeatFocus(boolean b) {
        if (b) {
            checkNewPwd();
        }
    }


}
