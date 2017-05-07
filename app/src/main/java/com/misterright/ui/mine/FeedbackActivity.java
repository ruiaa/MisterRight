package com.misterright.ui.mine;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.misterright.R;
import com.misterright.databinding.ActivityMeFeedbackBinding;
import com.misterright.http.MisterApi;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.util.AppUtil;
import com.misterright.util.ResUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends ToolbarActivity {

    public static final int FEEDBACK_TEXT_MAX=100;
    public static final int FEEDBACK_TEXT_MIN=10;

    @BindView(R.id.me_feedback_edit)
    EditText feedbackEdit;

    private ActivityMeFeedbackBinding binding;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_me_feedback);
        ButterKnife.bind(this);

        initToolbar();
        setTitle(R.string.feedback);
        setToolbarLeftShow(v -> {
            onBackPressed();
        });
        setToolbarRightText(R.string.commit, v -> {
            if (checkFeedbackText()){
                MisterApi.getInstance().feedback(feedbackEdit.getText().toString());
            }
        });

        dialog= new MaterialDialog.Builder(this)
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.sure)
                .onPositive((dialog1, which) -> {
                    AppUtil.showKeyBoard(feedbackEdit);
                })
                .build();
    }

    private boolean checkFeedbackText(){
        int size=feedbackEdit.getText().length();
        if (size<FEEDBACK_TEXT_MIN){
            dialog.setContent(String.format(ResUtil.getString(R.string.text_too_little),FEEDBACK_TEXT_MIN));
            dialog.show();
            return false;
        }else if (size>FEEDBACK_TEXT_MAX){
            dialog.setContent(String.format(ResUtil.getString(R.string.text_too_much),FEEDBACK_TEXT_MAX));
            dialog.show();
            return false;
        }else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtil.showKeyBoard(feedbackEdit);
    }
}
