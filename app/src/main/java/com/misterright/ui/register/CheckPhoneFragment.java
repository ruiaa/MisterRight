package com.misterright.ui.register;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.misterright.MisterConfig;
import com.misterright.R;
import com.misterright.databinding.FragmentRegisterCheckPhoBinding;
import com.misterright.http.MisterApi;
import com.misterright.ui.DialogHelper;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.storage.KeyStorage;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/11/19.
 */

public class CheckPhoneFragment extends ToolbarFragment {
    private static final String INVITE_UID = "invite_uid";

    @BindView(R.id.register_check_pho_input_pho)
    EditText inputPho;
    @BindView(R.id.register_check_pho_input_valid_code)
    EditText inputValidCode;
    @BindView(R.id.register_check_pho_input_password)
    EditText inputPwd;
    @BindView(R.id.register_check_pho_input_pwd_sure)
    EditText inputPwdSure;


    private LoginActivity loginActivity;
    private FragmentRegisterCheckPhoBinding binding;
    private DialogHelper dialogHelper;
    private String inviteUid;
    private ObservableInt counter;
    private Subscription subscriptionCounter;

    public CheckPhoneFragment() {

    }

    public static CheckPhoneFragment newInstance(String inviteUid) {
        CheckPhoneFragment fragment = new CheckPhoneFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INVITE_UID, inviteUid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inviteUid = getArguments().getString(INVITE_UID);
        }
        loginActivity = (LoginActivity) getActivity();
        dialogHelper=loginActivity.dialogHelper;
        counter=new ObservableInt();
        counter.set(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_check_pho, container, false);
        ButterKnife.bind(this, binding.getRoot());
        init();
        return binding.getRoot();
    }

    private void init() {
        initToolbar(binding.getRoot(), loginActivity);
        setToolbarTransparent();
        setTitle(ResUtil.getString(R.string.register) + "(2/3)");
        setToolbarLeftShow(v -> {
            loginActivity.fragmentStack.turnBackFragment();
        });

        binding.setCounter(counter);
    }

    private boolean checkPho(){
        if (inputPho.getText().toString().length()< MisterConfig.PHONE_NUM) {
            dialogHelper.showTextPositiveDialog(R.string.hint_input_phone_number,null);
            return false;
        }
        return true;
    }

    private boolean checkInput() {
        if (inputPho.getText().toString().length()< MisterConfig.PHONE_NUM) {
            dialogHelper.showTextPositiveDialog(R.string.hint_input_phone_number,null);
            return false;
        }
        if (inputValidCode.getText().toString().length()<MisterConfig.VALID_CODE){
            dialogHelper.showTextPositiveDialog(R.string.hint_input_valid_code,null);
            return false;
        }
        if (inputPwd.getText().toString().length()<MisterConfig.PWD_COUNT_MIN){
            dialogHelper.showTextPositiveDialog(R.string.hint_input_password,null);
            return false;
        }
        if (!inputPwd.getText().toString().equals(inputPwdSure.getText().toString())){
            dialogHelper.showTextPositiveDialog(R.string.tip_pwd_two_difference,null);
            return false;
        }
        return true;
    }

    private void uploadInput(final String pho,final String pwd,final String code, final String inviteUid) {
        dialogHelper.waitingProgressDialog(R.string.tip_phone_checking);
        MisterApi.getInstance()
                .registerUser(pho,pwd,code,inviteUid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        registerResult -> {
                            dialogHelper.hideWaitingProgressDialog();
                            if (registerResult.result){
                                loginActivity.fragmentStack.open(AddInfoFragment.newInstance(pho,pwd));

                                LogUtil.i("uploadInput--"+ KeyStorage.contains(MisterConfig.KEY_MISTER_DATA));
                                loginActivity.login(pho,pwd,null)
                                        .subscribe(aBoolean -> {
                                            LogUtil.i("uploadInput--"+ KeyStorage.contains(MisterConfig.KEY_MISTER_DATA));
                                        },throwable -> {
                                            LogUtil.i("uploadInput--"+ KeyStorage.contains(MisterConfig.KEY_MISTER_DATA));
                                            LogUtil.e("uploadInput--",throwable);
                                        });

                            }else {
                                dialogHelper.showTextPositiveDialog(R.string.tip_pho_valid_worry_no_effect,null);
                            }
                        },
                        throwable -> {
                            dialogHelper.hideWaitingProgressDialog();
                            ToastUtil.showShort("网络错误，请重试");
                            LogUtil.e("uploadInput--",throwable);
                        }
                );

/*        dialogHelper.hideWaitingProgressDialog();
        loginActivity.fragmentStack.open(AddInfoFragment.newInstance(pho,pwd));*/
    }

    private void getValidCode(final String pho){
        MisterApi.getInstance()
                .getValidCode(pho)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        registerResult -> {
                            if (!registerResult.result){
                                dialogHelper.showTextPositiveDialog(R.string.tip_pho_worry_no_effect,null);
                            }
                        },
                        throwable -> {
                            ToastUtil.showShort("网络错误，请重试");
                            LogUtil.e("getValidCode--",throwable);
                        }
                );
    }

    @OnClick(R.id.register_check_pho_get_valid_code)
    public void onGetValidCodeClick() {
        if (!checkPho()) return;

        if (subscriptionCounter!=null&&!subscriptionCounter.isUnsubscribed()){
            subscriptionCounter.unsubscribe();
        }
        counter.set(60);
        subscriptionCounter= Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .map(aLong -> aLong+1)
                .subscribe(aLong -> {
                    if (aLong>=60){
                        if (!subscriptionCounter.isUnsubscribed()){
                            subscriptionCounter.unsubscribe();
                        }
                    }
                    counter.set(60-aLong.intValue());
                });
        getValidCode(inputPho.getText().toString());
    }

    @OnClick(R.id.register_check_pho_next_step)
    public void onNextStepClick() {
        if (checkInput()){
            uploadInput(inputPho.getText().toString(),
                    inputPwdSure.getText().toString(),
                    inputValidCode.getText().toString(),
                    inviteUid);
        }
    }
}
