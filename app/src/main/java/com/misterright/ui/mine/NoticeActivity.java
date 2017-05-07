package com.misterright.ui.mine;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.misterright.R;
import com.misterright.databinding.ActivityMeNoticeBinding;
import com.misterright.ui.base.ToolbarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeActivity extends ToolbarActivity {

    @BindView(R.id.me_notice_list)
    RecyclerView noticeList;
    private ActivityMeNoticeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_me_notice);
        ButterKnife.bind(this);

        initToolbar();
        setTitle(R.string.notice);
        setToolbarLeftShow(v -> {
            onBackPressed();
        });
    }
}
