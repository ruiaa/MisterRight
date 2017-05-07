package com.misterright.ui.register;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.misterright.MisterConfig;
import com.misterright.R;
import com.misterright.databinding.FragmentRegisterCheckInviteBinding;
import com.misterright.http.MisterApi;
import com.misterright.ui.DialogHelper;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/11/19.
 */

public class CheckInviteFragment extends ToolbarFragment {

    @BindView(R.id.register_check_invite_input_invite_code)
    EditText inputInviteCode;
    private LoginActivity loginActivity;
    private DialogHelper dialogHelper;
    private FragmentRegisterCheckInviteBinding binding;
    private String code;

    public CheckInviteFragment() {

    }

    public static CheckInviteFragment newInstance() {
        CheckInviteFragment fragment = new CheckInviteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity = (LoginActivity) getActivity();
        dialogHelper=loginActivity.dialogHelper;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_check_invite, container, false);
        init();
        ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }

    private void init() {
        initToolbar(binding.getRoot(), loginActivity);
        setToolbarTransparent();
        setTitle(ResUtil.getString(R.string.register) + "(1/3)");
        setToolbarLeftShow(v -> {
            loginActivity.fragmentStack.turnBackFragment();
        });
    }

    private boolean checkInviteCode() {
        code=inputInviteCode.getText().toString();
        if (code.length()< MisterConfig.INVITE_CODE){
            dialogHelper.showTextPositiveDialog(R.string.tip_invite_code_count_worry,null);
            return false;
        }else {
            return true;
        }
    }

    private void upLoadInviteCode(final String c){
        dialogHelper.waitingProgressDialog(R.string.tip_invite_code_checking);
        MisterApi.getInstance()
                .checkInviteCode(c)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        registerResult -> {
                            dialogHelper.hideWaitingProgressDialog();
                            if (registerResult.inviterUid==null||registerResult.inviterUid.isEmpty()){
                                dialogHelper.showTextPositiveDialog(R.string.tip_invite_code_worry,null);
                            }else {
                                loginActivity.fragmentStack.open(CheckPhoneFragment.newInstance(registerResult.inviterUid));
                            }
                        },
                        throwable -> {
                            dialogHelper.hideWaitingProgressDialog();
                            ToastUtil.showShort("网络错误，请重试");
                            LogUtil.e("upLoadInviteCode--",throwable);
                        }
                );
/*        loginActivity.fragmentStack.open(CheckPhoneFragment.newInstance(c));
        dialogHelper.hideWaitingProgressDialog();*/
    }

    @OnClick(R.id.register_check_invite_next)
    public void onNextStepClick() {
        if (checkInviteCode()){
            upLoadInviteCode(code);
        }
    }
}
