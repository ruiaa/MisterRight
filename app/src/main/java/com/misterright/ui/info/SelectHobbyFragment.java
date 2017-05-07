package com.misterright.ui.info;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.BR;
import com.misterright.R;
import com.misterright.databinding.FragmentInfoSelectHobbyBinding;
import com.misterright.model.entity.MapInfo;
import com.misterright.ui.adapter.SimpleRecyclerAdapter;
import com.misterright.ui.base.ToolbarFragment;
import com.misterright.util.LogUtil;
import com.misterright.util.ResUtil;
import com.misterright.util.bus.OnEditInputFragmentComplete;
import com.misterright.util.bus.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by ruiaa on 2016/11/11.
 */

public class SelectHobbyFragment extends ToolbarFragment {

    private static final String HOBBY_TYPE = "type";
    public static final int SPORT = 1;
    public static final int DIET = 2;
    public static final int DRINK = 3;
    public static final int BOOK = 4;
    public static final int VIDEO = 5;
    public static final int LEISURE = 6;


    @BindView(R.id.info_select_hobby_label)
    RecyclerView labelRecycler;

    private InfoActivity infoActivity;
    private FragmentInfoSelectHobbyBinding binding;
    private int hobbyType;
    private MapInfo.Hobby newHobby;
    private List<String> oldHobbyList;
    private SimpleRecyclerAdapter<String> adapter;
    private List<String> labelList;
    final private List<String> selectedLabelList=new ArrayList<>();

    public SelectHobbyFragment() {

    }

    public static SelectHobbyFragment newInstance(int hobbyType) {
        SelectHobbyFragment fragment = new SelectHobbyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(HOBBY_TYPE, hobbyType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hobbyType = getArguments().getInt(HOBBY_TYPE, SPORT);
        }
        infoActivity = (InfoActivity) getActivity();
        newHobby=infoActivity.newMapInfoForEditFrag.hobby;

        initLabelList();

        Subscription subscription= RxBus.getDefault().toObservable(OnEditInputFragmentComplete.class)
                .subscribe(event->{
                    if (ResUtil.getString(R.string.edit_new_hobby_label).equals(event.title)){
                        labelList.add(0,event.input);
                        selectedLabelList.add(event.input);
                        if (adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
        infoActivity.addSubscription(subscription);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info_select_hobby, container, false);
        ButterKnife.bind(this, binding.getRoot());

        initRecycler();

        initToolbar(binding.getRoot(),infoActivity);
        setToolbarRightText(R.string.save,v -> {
            oldHobbyList.clear();
            oldHobbyList.addAll(selectedLabelList);
            infoActivity.turnBackFragment();
        });

        return binding.getRoot();
    }

    private void initLabelList(){
        switch (hobbyType){
            case SPORT:{
                if (newHobby.sport==null){
                    newHobby.sport=new ArrayList<>();
                }
                oldHobbyList=newHobby.sport;
                labelList=newHobby.getDefaultSport();
                break;
            }
            case DIET : {
                if (newHobby.diet==null){
                    newHobby.diet=new ArrayList<>();
                }
                oldHobbyList=newHobby.diet;
                labelList=newHobby.getDefaultDiet();
                break;
            }
            case DRINK : {
                if (newHobby.drink==null){
                    newHobby.drink=new ArrayList<>();
                }
                oldHobbyList=newHobby.drink;
                labelList=newHobby.getDefaultDrink();
                break;
            }
            case BOOK : {
                if (newHobby.book==null){
                    newHobby.book=new ArrayList<>();
                }
                oldHobbyList=newHobby.book;
                labelList=newHobby.getDefaultBook();
                break;
            }
            case VIDEO : {
                if (newHobby.video==null){
                    newHobby.video=new ArrayList<>();
                }
                oldHobbyList=newHobby.video;
                labelList=newHobby.getDefaultVideo();
                break;
            }
            case LEISURE : {
                if (newHobby.leisure==null){
                    newHobby.leisure=new ArrayList<>();
                }
                oldHobbyList=newHobby.leisure;
                labelList=newHobby.getDefaultLeisure();
                break;
            }
            default:{
                if (newHobby.leisure==null){
                    newHobby.leisure=new ArrayList<>();
                }
                oldHobbyList=newHobby.leisure;
                labelList=newHobby.getDefaultLeisure();
            }
        }

        selectedLabelList.addAll(oldHobbyList);
    }

    private void initRecycler(){
        adapter=new SimpleRecyclerAdapter<String>(
                infoActivity,
                R.layout.item_choose_hobby,
                labelList,
                (holder, position, model) -> {
                    holder.getBinding().setVariable(BR.hobbyLabel,model);
                    holder.getBinding().setVariable(BR.hobbyIsSelect,selectedLabelList.contains(model));
                }
        );
        adapter.setItemListenerBinding((holder, position, model) -> {
           holder.getBinding().getRoot().setOnClickListener(v -> {
               if (selectedLabelList.contains(model)){
                   selectedLabelList.remove(model);
               }else {
                   selectedLabelList.add(model);
               }
               holder.getBinding().setVariable(BR.hobbyIsSelect,selectedLabelList.contains(model));
           });
        });

        labelRecycler.setLayoutManager(new LinearLayoutManager(infoActivity));
        labelRecycler.setAdapter(adapter);
    }

    @OnClick(R.id.info_select_hobby_new_label)
    public void onClick() {
        infoActivity.openEditInputFragment(ResUtil.getString(R.string.edit_new_hobby_label),"","");
    }

    @Override
    protected boolean canTurnBack() {
        return true;
    }

    @Override
    protected void onTurnBack() {
        infoActivity.turnBackFragment();
    }
}
