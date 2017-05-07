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
import com.misterright.model.entity.MapInfo;
import com.misterright.ui.PictureActivity;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.ui.widget.ImgViewGlide;
import com.misterright.ui.widget.NineImg;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.bus.DeleteImgEvent;
import com.misterright.util.bus.OnEditInputFragmentComplete;
import com.misterright.util.bus.RxBus;
import com.misterright.util.net.UploadQiniu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/11/11.
 */

public class MineInfoFragment extends ToolbarFragment {

    public static final int PICK_IMG_FOR_ALBUM = 301;
    public static final int PICK_IMG_FOR_HEAD = 302;

    @BindView(R.id.info_detail_img)
    NineImg albumImg;
    @BindView(R.id.info_detail_head)
    ImgViewGlide headImg;

    private InfoActivity infoActivity;
    private FragmentInfoMineBinding binding;
    private MapInfo newMapInfo;
    private ArrayList<String> completeAlbum;


    public MineInfoFragment() {

    }

    public static MineInfoFragment newInstance() {
        MineInfoFragment fragment = new MineInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoActivity = (InfoActivity) getActivity();
        newMapInfo = infoActivity.newMineFragMapInfo;
        completeAlbum = newMapInfo.getCompleteAlbum();

        Subscription subscription = RxBus.getDefault().toObservable(OnEditInputFragmentComplete.class)
                .subscribe(event -> {
                    if (ResUtil.getString(R.string.introduction).equals(event.title)) {
                        newMapInfo.introduction = event.input;
                        if (binding != null) {
                            initMine();
                        }
                    }
                });
        infoActivity.addSubscription(subscription);
        Subscription subscription2=RxBus.getDefault().toObservable(DeleteImgEvent.class)
                .subscribe(deleteImgEvent -> {
                    if ("MineInfoFragment".equals(deleteImgEvent.from)){
                        onDeleteImg(deleteImgEvent.position,deleteImgEvent.url);
                    }
                });
        infoActivity.addSubscription(subscription2);

        MisterApi.getInstance().getMineInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userInfo -> {
                            if (binding != null) {
                                initMine();
                            }
                        },
                        throwable -> {
                            LogUtil.e("onCreateView--", throwable);
                        }
                );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info_mine, container, false);
        ButterKnife.bind(this, binding.getRoot());

        headImg.setQiniu(MisterData.getInstance().globalInfo.userInfo.mapInfo.headUrl);
        albumImg.setWithSelector(true);
        albumImg.setOnSelectorClickListener((position, view) -> openSelectImg());
        albumImg.setOnItemClickListener((position, view) -> openBigPicture(position));


        initToolbar(binding.getRoot(), infoActivity);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setToolbarLeftShow(v -> {
            MisterData.getInstance().globalInfo.userInfo.mapInfo = newMapInfo;
            infoActivity.finish();
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        initMine();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MisterApi.getInstance().reSetInfo(newMapInfo);
        MisterData.getInstance().globalInfo.userInfo.mapInfo = newMapInfo;
        super.onDestroy();
    }

    private void initMine() {
        newMapInfo = infoActivity.newMineFragMapInfo;
        binding.setEditable(true);
        binding.setMapInfo(newMapInfo);
        binding.infoDetailImg.resetAllImgPaths(completeAlbum);
    }

    @OnClick({R.id.info_detail_base_edit, R.id.info_detail_fs_edit, R.id.info_detail_hobby_edit})
    public void openEditListener() {
        infoActivity.openEditInfoFragment();
    }

    @OnClick(R.id.info_detail_intro_edit)
    public void openIntroEditListener() {
        infoActivity.openEditInputFragment(ResUtil.getString(R.string.introduction),
                "",
                newMapInfo.introduction
        );
    }

    @OnClick(R.id.info_detail_head)
    public void onHeadListener() {
        openSelectHead();
    }

    private void openBigPicture(int p) {
        infoActivity.startActivity(PictureActivity.newIntent(
                infoActivity,
                completeAlbum,
                p,
                PictureActivity.TYPE_DELETE,
                "MineInfoFragment"));
    }

    private void openSelectImg() {
        MultiImageSelector.create()
                .showCamera(true) // 是否显示相机. 默认为显示
                .count(8 - newMapInfo.getAlbumSize()) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                .multi() // 多选模式, 默认模式;
                .start(infoActivity, PICK_IMG_FOR_ALBUM);
    }

    private void openSelectHead() {
        MultiImageSelector.create()
                .showCamera(true) // 是否显示相机. 默认为显示
                .multi()
                .count(1)
                .start(infoActivity, PICK_IMG_FOR_HEAD);
    }

    public void onSelectImg(ArrayList<String> paths) {
        if (paths == null) return;
        for (String s : paths) {
            if (s != null) {
                String key = UploadQiniu.getInstance().upload(s);
                if (key != null) {
                    completeAlbum.add(s);
                    newMapInfo.addAlbum(key);
                }
            }
        }
    }

    public void onDeleteImg(int position, String url) {
        if (completeAlbum.size() > position && completeAlbum.get(position).equals(url)) {
            completeAlbum.remove(position);
            newMapInfo.album.remove(position);
        }
    }

    public void onSelectImgForHead(String path) {
        if (path != null) {
            String key = UploadQiniu.getInstance().upload(path);
            if (key != null) {
                headImg.setImgUrl(path);
                newMapInfo.headUrl = key;
            }
        }
    }
}
