package com.misterright.ui.info;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.misterright.R;
import com.misterright.model.MisterData;
import com.misterright.model.entity.MapInfo;
import com.misterright.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class InfoActivity extends BaseActivity {

    private static final String INFO_TYPE="type";
    private static final String INFO_MATCH_POSITION="position";
    public static final int INFO_TYPE_MINE=1;
    public static final int INFO_TYPE_PAIR=2;
    public static final int INFO_TYPE_MATCH=3;



    private ArrayList<Fragment> fragmentStack=new ArrayList<>();
    private FragmentManager fragmentManager;
    private int type;

    public MapInfo newMapInfoForEditFrag;
    public MapInfo newMineFragMapInfo;

    public static Intent newIntent(Context context,int type){
        Intent intent=new Intent(context,InfoActivity.class);
        intent.putExtra(INFO_TYPE,type);
        return intent;
    }

    public static Intent newIntent(Context context,int type,int position){
        Intent intent=new Intent(context,InfoActivity.class);
        intent.putExtra(INFO_TYPE,type);
        intent.putExtra(INFO_MATCH_POSITION,position);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        type=getIntent().getIntExtra(INFO_TYPE,INFO_TYPE_MINE);
        fragmentManager=getFragmentManager();
        if (type==INFO_TYPE_MINE){
            openMineInfoFragment();
        }else if(type==INFO_TYPE_PAIR){
            openPairInfoFragment();
        }else if(type==INFO_TYPE_MATCH){
            openMatchInfoFragment();
        }
    }

    private void openMatchInfoFragment(){
        int p=getIntent().getIntExtra(INFO_MATCH_POSITION,0);
        MatchInfoFragment fragment=MatchInfoFragment.newInstance(p);
        openFirstFragment(fragment);
    }

    private void openMineInfoFragment(){
        newMineFragMapInfo =MisterData.getInstance().globalInfo.userInfo.mapInfo.getClone();
        MineInfoFragment fragment=MineInfoFragment.newInstance();
        openFirstFragment(fragment);
    }

    private void openPairInfoFragment(){
        PairInfoFragment fragment=PairInfoFragment.newInstance();
        openFirstFragment(fragment);
    }



    public void openEditInfoFragment(){
        newMapInfoForEditFrag = newMineFragMapInfo.getClone();
        if (newMapInfoForEditFrag.baseInfo==null){
            newMapInfoForEditFrag.baseInfo=new MapInfo.BaseInfo();
        }
        if (newMapInfoForEditFrag.friendStandard==null){
            newMapInfoForEditFrag.friendStandard=new MapInfo.FriendStandard();
        }
        if (newMapInfoForEditFrag.hobby==null){
            newMapInfoForEditFrag.hobby=new MapInfo.Hobby();
        }
        EditInfoFragment fragment=EditInfoFragment.newInstance();
        openNextFragment(fragment);
    }

    public void openEditInputFragment(String title,String hint,String origin){
        EditInputFragment fragment=EditInputFragment.newInstance(title,hint,origin);
        openNextFragment(fragment);
    }

    public void openSelectHobbyFragment(int hobbyType){
        SelectHobbyFragment fragment=SelectHobbyFragment.newInstance(hobbyType);
        openNextFragment(fragment);
    }

    private void openFirstFragment(Fragment fragment){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.add(R.id.info_framelayout,fragment);
        transaction.commit();
        fragmentStack.add(fragment);
    }

    private void openNextFragment(Fragment fragment){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.add(R.id.info_framelayout,fragment);
        transaction.hide(fragmentStack.get(fragmentStack.size()-1));
        transaction.commit();
        fragmentStack.add(fragment);
    }

    public void turnBackFragment(){
        int size=fragmentStack.size();
        if (size>=2){
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.remove(fragmentStack.get(size-1));
            transaction.show(fragmentStack.get(size-2));
            transaction.commit();
            fragmentStack.get(size-2).onResume();
            fragmentStack.remove(size-1);
        }else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK&&data!=null){
            if (requestCode==MineInfoFragment.PICK_IMG_FOR_ALBUM){
                ((MineInfoFragment)fragmentStack.get(0)).onSelectImg(data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT));
            }else if (requestCode==MineInfoFragment.PICK_IMG_FOR_HEAD){
                List<String> list=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (list!=null&&!list.isEmpty()){
                    ((MineInfoFragment)fragmentStack.get(0)).onSelectImgForHead(list.get(0));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
