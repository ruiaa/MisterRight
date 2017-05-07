package com.misterright.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.misterright.MisterConfig;
import com.misterright.R;
import com.misterright.databinding.FragmentRegisterAddInfoBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.entity.MapInfo;
import com.misterright.ui.DialogHelper;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.ui.widget.ImgViewGlide;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.net.UploadQiniu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/11/19.
 */

public class AddInfoFragment extends ToolbarFragment {
    private static final String PHONE = "phone";
    private static final String PWD = "pwd";
    private static final int SELECT_ID_CARD_REQUEST_CODE = 44;

    @BindView(R.id.register_add_info_input_nickname)
    EditText inputNickname;
    @BindView(R.id.register_add_info_input_sex)
    TextView inputSex;
    @BindView(R.id.register_add_info_input_birthday)
    TextView inputBirthday;
    @BindView(R.id.register_add_info_input_school)
    TextView inputSchool;
    @BindView(R.id.register_add_info_input_gradle)
    TextView inputGradle;
    @BindView(R.id.register_add_info_input_idcard)
    ImgViewGlide inputIdcard;

    private LoginActivity loginActivity;
    private FragmentRegisterAddInfoBinding binding;
    private DialogHelper dialogHelper;
    private String pho;
    private String pwd;
    private String idCardUrlKey="oo";
    private Calendar birCalendar;

    private OptionPicker sexPicker;
    private boolean sexHadSelect = false;
    private DatePicker birthdayPicker;
    private boolean birthdayHadSelect = false;
    private OptionPicker schoolPicker;
    private boolean schoolHadSelect = false;
    private OptionPicker gradlePicker;
    private boolean gradleHadSelect = false;


    public AddInfoFragment() {

    }

    public static AddInfoFragment newInstance(String pho, String pwd) {
        AddInfoFragment fragment = new AddInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PHONE, pho);
        bundle.putString(PWD, pwd);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pho = getArguments().getString(PHONE);
            pwd = getArguments().getString(PWD);
        }
        loginActivity = (LoginActivity) getActivity();
        dialogHelper = loginActivity.dialogHelper;
        birCalendar = Calendar.getInstance();
        birCalendar.set(2000, 1, 1, 0, 0, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_add_info, container, false);
        init();
        ButterKnife.bind(this, binding.getRoot());
        return binding.getRoot();
    }

    private void init() {
        initToolbar(binding.getRoot(), loginActivity);
        setToolbarTransparent();
        setTitle(ResUtil.getString(R.string.register) + "(3/3)");
        setToolbarLeftShow(v -> {
            loginActivity.fragmentStack.turnBackFragment();
        });
    }

    private void uploadInfo(final String nickname, final boolean sex, final String bir, final String school, final String gradle, final String idcardKey) {
        MisterApi.getInstance()
                .registerBaseinfo(pho, pwd, nickname, sex, bir, "student", school, gradle, idcardKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        registerResult -> {
                            dialogHelper.hideWaitingProgressDialog();
                            if (registerResult.result){
                                dialogHelper.waitingProgressDialog(R.string.tip_logining);
                                loginActivity.loginUsePwdOpenMain(pho,pwd);
                            }else {
                                dialogHelper.showTextPositiveDialog(R.string.tip_register_fail,null);
                            }
                        },
                        throwable -> {
                            dialogHelper.hideWaitingProgressDialog();
                            ToastUtil.showShort("网络错误，请重试");
                            LogUtil.e("uploadInfo--", throwable);
                        }
                );
        LogUtil.i("uploadInfo--" + pho + "**" + pwd + "**" + nickname + "**" + sex + "**" + bir + "**" + "student" + "**" + school + "**" + gradle + "**" + idcardKey);
    }

    private boolean checkInput() {
        if (inputNickname.getText().toString().length() < MisterConfig.NICKNAME_MIN) {
            dialogHelper.showTextPositiveDialog(ResUtil.format(R.string.tip_nickname_too_short, MisterConfig.NICKNAME_MIN), null);
            return false;
        }
        if (!sexHadSelect) {
            dialogHelper.showTextPositiveDialog(R.string.tip_choose_sex, null);
            return false;
        }
        if (!birthdayHadSelect) {
            dialogHelper.showTextPositiveDialog(R.string.tip_choose_birthday, null);
            return false;
        }
        if (!schoolHadSelect) {
            dialogHelper.showTextPositiveDialog(R.string.tip_choose_school, null);
            return false;
        }
        if (!gradleHadSelect) {
            dialogHelper.showTextPositiveDialog(R.string.tip_choose_gradle, null);
            return false;
        }
        if (idCardUrlKey == null||idCardUrlKey.isEmpty()) {
            dialogHelper.showTextPositiveDialog(R.string.tip_choose_idcard, null);
            return false;
        }
        return true;
    }

    @OnClick(R.id.register_add_info_register)
    public void onRegisterClick() {
        if (checkInput()) {
            dialogHelper.waitingProgressDialog(R.string.tip_registering);
            uploadInfo(inputNickname.getText().toString(),
                    inputSex.getText().toString().contains(ResUtil.getString(R.string.male)),
                    String.valueOf(birCalendar.getTimeInMillis() / 1000),
                    inputSchool.getText().toString(),
                    inputGradle.getText().toString(),
                    idCardUrlKey);
        }
    }


    @OnClick(R.id.register_add_info_sex)
    public void inputSex() {
        if (sexPicker == null) {
            sexPicker = new OptionPicker(loginActivity, new String[]{"   " + ResUtil.getString(R.string.male) + "   "
                    , "   " + ResUtil.getString(R.string.female) + "   "});
            sexPicker.setOnOptionPickListener((position, option) -> {
                ResUtil.setTextAppearance(inputSex, R.style.register_input_text_content);
                inputSex.setText(position == 0 ? R.string.male : R.string.female);
                sexHadSelect = true;
            });
        }
        sexPicker.show();
    }

    @OnClick(R.id.register_add_info_birthday)
    public void inputBirthday() {
        if (birthdayPicker == null) {
            Calendar calendar = Calendar.getInstance();
            birthdayPicker = new DatePicker(loginActivity, DatePicker.YEAR_MONTH_DAY);
            birthdayPicker.setRangeStart(1980, 1, 1);
            birthdayPicker.setRangeEnd(calendar.get(Calendar.YEAR) - 18, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            birthdayPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                @Override
                public void onDatePicked(String year, String month, String day) {
                    ResUtil.setTextAppearance(inputBirthday, R.style.register_input_text_content);
                    inputBirthday.setText(year + "-" + month + "-" + day);
                    birCalendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(day));
                    birthdayHadSelect = true;
                }
            });
        }
        birthdayPicker.show();
    }

    @OnClick(R.id.register_add_info_school)
    public void inputSchool() {
        if (schoolPicker == null) {
            schoolPicker = new OptionPicker(loginActivity, new String[]{"华工", "华农", "华师"});
            schoolPicker.setOnOptionPickListener((position, option) -> {
                ResUtil.setTextAppearance(inputSchool, R.style.register_input_text_content);
                inputSchool.setText(option);
                schoolHadSelect = true;
            });
        }
        schoolPicker.show();
    }

    @OnClick(R.id.register_add_info_gradle)
    public void inputGradle() {
        if (gradlePicker == null) {
            gradlePicker = new OptionPicker(loginActivity, new ArrayList<>(MapInfo.getGradesDefault()));
            gradlePicker.setTextSize(16);
            gradlePicker.setOnOptionPickListener((position, option) -> {
                ResUtil.setTextAppearance(inputGradle, R.style.register_input_text_content);
                inputGradle.setText(option);
                gradleHadSelect = true;
            });
        }
        gradlePicker.show();
    }

    @OnClick(R.id.register_add_info_input_idcard)
    public void inputIdcard() {
        MultiImageSelector.create()
                .showCamera(true)
                .single()
                .start(loginActivity, SELECT_ID_CARD_REQUEST_CODE);
    }

    public boolean onSelectIdcardImgActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) return true;
        if (requestCode == SELECT_ID_CARD_REQUEST_CODE) {
            List<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (list != null && !list.isEmpty()) {
                uploadIdcard(list.get(0));
            }
            return true;
        } else {
            return false;
        }
    }

    private String uploadIdcard(String path) {
        if (path != null) {
            String key =UploadQiniu.getInstance().upload(path);
            inputIdcard.setPadding(1, 1, 1, 1);
            inputIdcard.setImgUrl(path);
            idCardUrlKey = key;
            return key;
        }
        return null;
    }
}
