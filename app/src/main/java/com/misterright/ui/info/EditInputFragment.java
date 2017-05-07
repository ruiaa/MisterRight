package com.misterright.ui.info;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentInfoEditInputBinding;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.AppUtil;
import com.misterright.util.bus.OnEditInputFragmentComplete;
import com.misterright.util.bus.RxBus;

/**
 * Created by ruiaa on 2016/11/11.
 */

public class EditInputFragment extends ToolbarFragment {

    private static final String INPUT_TYPE="title";
    private static final String INPUT_HINT="hint";
    private static final String INPUT_ORIGIN="origin";

    private InfoActivity infoActivity;
    private FragmentInfoEditInputBinding binding;

    private String title="";
    private String hint="";
    private String origin="";

    public EditInputFragment(){

    }

    public static EditInputFragment newInstance(String title,String hint,String origin) {
        EditInputFragment fragment = new EditInputFragment();
        Bundle bundle=new Bundle();
        bundle.putString(INPUT_TYPE,title);
        bundle.putString(INPUT_HINT,hint);
        bundle.putString(INPUT_ORIGIN,origin);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            title = getArguments().getString(INPUT_TYPE);
            hint=getArguments().getString(INPUT_HINT);
            origin=getArguments().getString(INPUT_ORIGIN);
        }
        infoActivity=(InfoActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_info_edit_input,container,false);
        initToolbar(binding.getRoot(),infoActivity);
        setTitle(title);
        setToolbarRightText(R.string.finish, v -> {
            onTurnBack();
            RxBus.getDefault().post(new OnEditInputFragmentComplete(title,binding.editInput.getText().toString()));
        });

        binding.editInput.setHint(hint);
        binding.editInput.setText(origin);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtil.showKeyBoard(binding.editInput);
    }



    @Override
    protected boolean canTurnBack() {
        return true;
    }

    @Override
    protected void onTurnBack() {
        AppUtil.hideKeyBoard(binding.editInput);
        infoActivity.turnBackFragment();
    }
}

