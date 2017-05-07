package com.misterright.ui.info;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.misterright.BuildConfig;
import com.misterright.R;
import com.misterright.databinding.FragmentInfoEditInfoBinding;
import com.misterright.databinding.ViewInfoDetailBinding;
import com.misterright.model.entity.MapInfo;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.bus.OnEditInputFragmentComplete;
import com.misterright.util.bus.RxBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.LinkagePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import rx.Subscription;

/**
 * Created by ruiaa on 2016/11/11.
 */

public class EditInfoFragment extends ToolbarFragment {

    private InfoActivity infoActivity;
    private FragmentInfoEditInfoBinding binding;
    private ViewInfoDetailBinding detailBinding;
    private MapInfo newMapInfo;
    private LinkagePicker agePicker;
    private LinkagePicker heightPicker;
    private LinkagePicker gradlePicker;
    private AddressPicker addressPicker;
    private View openingPicker;

    public EditInfoFragment() {

    }

    public static EditInfoFragment newInstance() {
        EditInfoFragment fragment = new EditInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoActivity = (InfoActivity) getActivity();
        newMapInfo = infoActivity.newMapInfoForEditFrag;

        Subscription subscription = RxBus.getDefault().toObservable(OnEditInputFragmentComplete.class)
                .subscribe(event -> {
                    if (ResUtil.getString(R.string.nickname).equals(event.title)) {
                        newMapInfo.baseInfo.nickname = new String[]{event.input};
                        newMapInfo.nickName= event.input;
                        if (binding != null) {
                            initEditInfo();
                        }
                    }
                });
        infoActivity.addSubscription(subscription);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info_edit_info, container, false);
        ButterKnife.bind(this, binding.getRoot());
        detailBinding = binding.infoEditDetail;
        initEditInfo();

        initToolbar(binding.getRoot(), infoActivity);
        setToolbarRightText(ResUtil.getString(R.string.save), v -> {
            infoActivity.newMineFragMapInfo=newMapInfo;
            infoActivity.turnBackFragment();
        });
        setToolbarLeftShow(v -> {
            infoActivity.turnBackFragment();
        });


        if (BuildConfig.DEBUG){
            toolbar.setOnLongClickListener(v -> {
                tryTryTryAddInfo();
                initEditInfo();
                return true;
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        initEditInfo();
        super.onResume();
    }

    private void initEditInfo() {
        newMapInfo = infoActivity.newMapInfoForEditFrag;
        binding.setMapInfo(newMapInfo);
    }


    @OnClick({R.id.info_detail_sport,
            R.id.info_detail_diet,
            R.id.info_detail_drink,
            R.id.info_detail_book,
            R.id.info_detail_video,
            R.id.info_detail_leisure})
    public void onHobbyClick(View v) {
        int hobbyType = SelectHobbyFragment.SPORT;
        switch (v.getId()) {
            case R.id.info_detail_sport: {
                hobbyType = SelectHobbyFragment.SPORT;
                break;
            }
            case R.id.info_detail_diet: {
                hobbyType = SelectHobbyFragment.DIET;
                break;
            }
            case R.id.info_detail_drink: {
                hobbyType = SelectHobbyFragment.DRINK;
                break;
            }
            case R.id.info_detail_book: {
                hobbyType = SelectHobbyFragment.BOOK;
                break;
            }
            case R.id.info_detail_video: {
                hobbyType = SelectHobbyFragment.VIDEO;
                break;
            }
            case R.id.info_detail_leisure: {
                hobbyType = SelectHobbyFragment.LEISURE;
                break;
            }
        }
        infoActivity.openSelectHobbyFragment(hobbyType);
    }

    @OnClick({R.id.info_detail_fs_body_height, R.id.info_detail_base_body_height})
    public void onBodyHeight(View v) {
        openingPicker = v;
        if (heightPicker == null) {
            ArrayList<String> heightFirstList;
            ArrayList<ArrayList<String>> heightSecondList;
            heightFirstList = new ArrayList<>();
            heightSecondList = new ArrayList<>();
            heightFirstList.add(ResUtil.getString(R.string.no_limit));

            for (int i = 120; i <= 210; i++) {
                ArrayList<String> secondList = new ArrayList<>();
                secondList.add(ResUtil.getString(R.string.no_limit));
                for (int j = i; j <= 210; j++) {
                    secondList.add("" + j + "cm");
                }

                heightFirstList.add("" + i + "cm");
                heightSecondList.add(secondList);
                if (i == 120) {
                    heightSecondList.add(secondList);
                }
            }


            heightPicker = new LinkagePicker(infoActivity, heightFirstList, heightSecondList);
            heightPicker.setLineColor(Color.TRANSPARENT);
            heightPicker.setTextColor(ResUtil.getColor(R.color.text_blue),ResUtil.getColor(R.color.text_grey_4));
            heightPicker.setOnLinkageListener(new LinkagePicker.OnLinkageListener() {
                @Override
                public void onPicked(String first, String second, String third) {
                    switch (openingPicker.getId()) {
                        case R.id.info_detail_fs_body_height: {
                            newMapInfo.friendStandard.height = new String[]{first, second};
                            break;
                        }
                        case R.id.info_detail_base_body_height: {
                            newMapInfo.baseInfo.height = new String[]{first, second};
                            break;
                        }
                    }
                    initEditInfo();
                }
            });
        }

        heightPicker.show();
    }

    @OnClick({R.id.info_detail_fs_age})
    public void onAge(View v) {
        openingPicker = v;
        if (agePicker == null) {
            ArrayList<String> ageFirstList;
            ArrayList<ArrayList<String>> ageSecondList;
            ageFirstList = new ArrayList<>();
            ageSecondList = new ArrayList<>();
            ageFirstList.add(ResUtil.getString(R.string.no_limit));

            for (int i = 18; i <= 99; i++) {
                ArrayList<String> secondList = new ArrayList<>();
                secondList.add(ResUtil.getString(R.string.no_limit));
                for (int j = i; j <= 99; j++) {
                    secondList.add("" + j + ResUtil.getString(R.string.year_of_age));
                }

                ageFirstList.add("" + i + ResUtil.getString(R.string.year_of_age));
                ageSecondList.add(secondList);
                if (i == 18) {
                    ageSecondList.add(secondList);
                }
            }

            agePicker = new LinkagePicker(infoActivity, ageFirstList, ageSecondList);
            agePicker.setLineColor(Color.TRANSPARENT);
            agePicker.setTextColor(ResUtil.getColor(R.color.text_blue),ResUtil.getColor(R.color.text_grey_4));
            agePicker.setOnLinkageListener(new LinkagePicker.OnLinkageListener() {
                @Override
                public void onPicked(String first, String second, String third) {
                    newMapInfo.friendStandard.age = new String[]{first, second};
                    initEditInfo();
                }
            });
        }

        agePicker.show();
    }

    @OnClick(R.id.info_detail_fs_gradle)
    public void onGradle(View v) {
        openingPicker = v;
        if (gradlePicker == null) {
            ArrayList<String> gradleFirstList;
            ArrayList<ArrayList<String>> gradleSecondList;
            gradleFirstList = new ArrayList<>();
            gradleSecondList = new ArrayList<>();
            gradleFirstList.add(ResUtil.getString(R.string.no_limit));
            gradleFirstList.addAll(MapInfo.getGradesDefault());

            for (int i = 0; i <= MapInfo.getGradesDefault().size() - 1; i++) {
                ArrayList<String> secondList = new ArrayList<>();
                secondList.add(ResUtil.getString(R.string.no_limit));
                secondList.addAll(MapInfo.getGradesDefault(i));

                gradleSecondList.add(secondList);
                if (i == 0) {
                    gradleSecondList.add(secondList);
                }
            }

            gradlePicker = new LinkagePicker(infoActivity, gradleFirstList, gradleSecondList);
            gradlePicker.setLineColor(Color.TRANSPARENT);
            gradlePicker.setTextColor(ResUtil.getColor(R.color.text_blue),ResUtil.getColor(R.color.text_grey_4));
            gradlePicker.setOnLinkageListener(new LinkagePicker.OnLinkageListener() {
                @Override
                public void onPicked(String first, String second, String third) {
                    newMapInfo.friendStandard.grade = new String[]{first, second};
                    initEditInfo();
                }
            });
        }
        gradlePicker.show();
    }

    @OnClick(R.id.info_detail_base_nickname)
    public void onNickname(View v) {
        infoActivity.openEditInputFragment(ResUtil.getString(R.string.nickname), "",
                newMapInfo.baseInfo.nickname == null ? "" : newMapInfo.baseInfo.getNicknameStr());
    }

    @OnClick({R.id.info_detail_base_live_area, R.id.info_detail_base_hometown})
    public void onLocal(View v) {
        openingPicker = v;
        if (addressPicker == null) {
            try {
                String json = ConvertUtils.toString(infoActivity.getAssets().open("city.json"));
                Gson gson = new Gson();
                Province[] provinces = gson.fromJson(json, Province[].class);
                ArrayList<Province> data = new ArrayList<>(Arrays.asList(provinces));
                addressPicker = new AddressPicker(infoActivity, data);
                //addressPicker.setHideProvince(true);//加上此句举将只显示地级及县级
                addressPicker.setHideCounty(true);//加上此句举将只显示省级及地级
                //addressPicker.setColumnWeight(2/8.0, 3/8.0, 3/8.0);//省级、地级和县级的比例为2:3:3

                addressPicker.setLineColor(Color.TRANSPARENT);
                addressPicker.setTextColor(ResUtil.getColor(R.color.text_blue),ResUtil.getColor(R.color.text_grey_4));

                addressPicker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        if (province == null) return;
                        String[] strings;
                        if (MapInfo.BaseInfo.isOnlyOneLevel(province.getAreaName())) {
                            strings = new String[]{province.getAreaName().replaceAll("市","").replaceAll("省",""),""};
                        }else if (city == null) {
                            strings = new String[]{province.getAreaName().replaceAll("省",""),""};
                        } else {
                            strings = new String[]{province.getAreaName().replaceAll("省",""), city.getAreaName()};
                        }

                        switch (openingPicker.getId()) {
                            case R.id.info_detail_base_live_area: {
                                newMapInfo.baseInfo.location = strings;
                                break;
                            }
                            case R.id.info_detail_base_hometown: {
                                newMapInfo.baseInfo.hometown = strings;
                                break;
                            }
                        }
                        initEditInfo();
                    }
                });
            } catch (IOException e) {
                LogUtil.e("chooseLocal--", e);
            }
        }

        if (addressPicker != null) {
            addressPicker.show();
        }
    }


    private void tryTryTryAddInfo(){
        newMapInfo.baseInfo.nickname=new String[]{"嗯嗯嗯"};
        newMapInfo.baseInfo.age=new String[]{"20","23"};
        newMapInfo.baseInfo.school=new String[]{"大学大学大学大学"};
        newMapInfo.baseInfo.college=new String[]{"学院学院学院学院"};
        newMapInfo.baseInfo.grade=new String[]{"大一","大二"};
    }

}
