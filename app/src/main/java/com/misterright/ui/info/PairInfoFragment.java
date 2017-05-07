package com.misterright.ui.info;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.databinding.FragmentInfoMineBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.ui.PictureActivity;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.ui.widget.ImgViewGlide;
import com.misterright.ui.widget.NineImg;
import com.misterright.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/11/11.
 */

public class PairInfoFragment extends ToolbarFragment {


    @BindView(R.id.info_detail_head)
    ImgViewGlide headImg;
    @BindView(R.id.info_detail_img)
    NineImg nineImg;
    private FragmentInfoMineBinding binding;
    private InfoActivity infoActivity;

    public PairInfoFragment() {

    }

    public static PairInfoFragment newInstance() {
        PairInfoFragment fragment = new PairInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoActivity=(InfoActivity)getActivity();
        MisterApi.getInstance().getPairUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userInfo -> {
                            if (binding!=null){
                                initPair();
                            }
                        },
                        throwable -> {
                            LogUtil.e("onCreate--", throwable);
                        }
                );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info_mine, container, false);
        ButterKnife.bind(this, binding.getRoot());

        initPair();

        initToolbar(binding.getRoot(),infoActivity);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setToolbarLeftShow(v -> {
            infoActivity.finish();
        });

        return binding.getRoot();
    }

    private void initPair() {
        binding.setEditable(false);
        binding.setMapInfo(MisterData.getInstance().otherUser.mapInfo);
        headImg.setQiniu(MisterData.getInstance().otherUser.mapInfo.headUrl);
        nineImg.resetAllImgPaths(MisterData.getInstance().otherUser.mapInfo.getCompleteAlbum());
        nineImg.setOnItemClickListener((position, view) -> {
            infoActivity.startActivity(PictureActivity.newIntent(infoActivity,MisterData.getInstance().otherUser.mapInfo.getCompleteAlbum(),position,PictureActivity.TYPE_SAVE,"PairInfoFragment"));
        });
    }
}
