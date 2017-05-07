package com.misterright.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.misterright.R;
import com.misterright.databinding.ActivityMainBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.model.StatusMonitor;
import com.misterright.model.entity.MisterStatus;
import com.misterright.ui.base.BaseActivity;
import com.misterright.ui.community.CommunityFragment;
import com.misterright.ui.mine.MeFragment;
import com.misterright.ui.status.KnowFragment;
import com.misterright.ui.status.LoveFragment;
import com.misterright.ui.status.MeetFragment;
import com.misterright.ui.status.MeetIntoMatchFragment;
import com.misterright.ui.status.MeetMatchFragment;
import com.misterright.util.LogUtil;
import com.misterright.util.bus.LogoutEvent;
import com.misterright.util.bus.MeetStatusChangeEvent;
import com.misterright.util.bus.RxBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class MainActivity extends BaseActivity {

//    @BindView(R.id.main_nav_bar)
//    BottomNavigationBar navBar;
//    public BadgeItem badgeItem0;

    public ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private Fragment statusFragment = null;
    private Fragment communityFragment = null;
    private Fragment meFragment = null;
    private Fragment currentFragment = null;
    private boolean haveNoCommitStatusFragment = false;

    private boolean isActive = false;

    private int selectTab=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);

       // initBottomBar();
        binding.setSelect(selectTab);

        monitorStatus();
        RxBus.getDefault().toObservable(LogoutEvent.class).subscribe(logoutEvent -> {
            //此处应清空栈
            startActivity(new Intent(this, logoutEvent.targetActivity));
            StatusMonitor.stop();
            clearSubscription();
            finish();
        });


        fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            statusFragment = createStatusFragment();
            currentFragment = statusFragment;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.main_frame, currentFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFragment != null && currentFragment.isHidden()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.show(currentFragment);
            transaction.commit();
        }
        if (currentFragment != null) {
            currentFragment.onResume();
        }
        isActive = true;
    }

    @Override
    protected void onPause() {
        isActive = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        FileStorage.cleanVoiceCache();
        super.onDestroy();
    }

    private void initBottomBar() {

       /* badgeItem0 = new BadgeItem();
        badgeItem0.hide(false);
        navBar.addItem(new BottomNavigationItem(R.mipmap.tab_meet_off, R.string.meet).setBadgeItem(badgeItem0))
                .addItem(new BottomNavigationItem(R.mipmap.tab_community_off, R.string.community))
                .addItem(new BottomNavigationItem(R.mipmap.tab_user_off, R.string.me))
//                .setInActiveColor(R.color.blue)
                .setActiveColor(R.color.blue)
                .initialise();

        navBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                //未选中 -> 选中
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.hide(currentFragment);
                switch (position) {
                    case 0: {
                        transaction.show(statusFragment);
                        currentFragment = statusFragment;
                        break;
                    }
                    case 1: {
                        if (communityFragment == null) {
                            communityFragment = CommunityFragment.newInstance();
                            transaction.add(R.id.main_frame, communityFragment);
                        } else {
                            transaction.show(communityFragment);
                        }
                        currentFragment = communityFragment;
                        break;
                    }
                    case 2: {
                        if (meFragment == null) {
                            meFragment = MeFragment.newInstance();
                            transaction.add(R.id.main_frame, meFragment);
                        } else {
                            transaction.show(meFragment);
                        }
                        currentFragment = meFragment;
                        break;
                    }
                }
                transaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });*/
    }

    @OnClick({R.id.main_tab_meet, R.id.main_tab_community, R.id.main_tab_mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_tab_meet:{
                switchTab(0);
                break;
            }
            case R.id.main_tab_community:{
                switchTab(1);
                break;
            }
            case R.id.main_tab_mine:{
                switchTab(2);
                break;
            }
        }
    }

    private void switchTab(int p){
        if (p==selectTab) return;

        selectTab=p;
        binding.setSelect(selectTab);
        //未选中 -> 选中
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(currentFragment);
        switch (selectTab) {
            case 0: {
                transaction.show(statusFragment);
                currentFragment = statusFragment;
                break;
            }
            case 1: {
                if (communityFragment == null) {
                    communityFragment = CommunityFragment.newInstance();
                    transaction.add(R.id.main_frame, communityFragment);
                } else {
                    transaction.show(communityFragment);
                }
                currentFragment = communityFragment;
                break;
            }
            case 2: {
                if (meFragment == null) {
                    meFragment = MeFragment.newInstance();
                    transaction.add(R.id.main_frame, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                currentFragment = meFragment;
                break;
            }
        }
        transaction.commit();
    }

    private void monitorStatus() {
        Subscription subscription = RxBus.getDefault()
                .toObservable(MeetStatusChangeEvent.class)
                .subscribe(event -> {
                    MisterData.save();
                    if (!isActive) {
                        haveNoCommitStatusFragment = true;
                        return;
                    }
                    Fragment oldStatusFragment = statusFragment;
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    statusFragment = createStatusFragment();
                    transaction.add(R.id.main_frame, statusFragment);
                    if (currentFragment == oldStatusFragment) {
                        currentFragment = statusFragment;
                    } else {
                        transaction.hide(statusFragment);
                    }
                    transaction.remove(oldStatusFragment);
                    transaction.commit();
                });
        addSubscription(subscription);
    }

    private Fragment createStatusFragment() {
        switch (MisterData.getInstance().status.pageStatus) {
            case MisterStatus.MEET: {
                switch (MisterData.getInstance().status.meetStatus) {
                    case MisterStatus.MEET_SELF: {
                        return MeetFragment.newInstance();
                    }
                    case MisterStatus.MEET_JOIN: {

                        return MeetIntoMatchFragment.newInstance();
                    }
                    case MisterStatus.MEET_MATCH: {
                        return MeetMatchFragment.newInstance();
                    }
                    default: {
                        return MeetFragment.newInstance();
                    }
                }
            }
            case MisterStatus.KNOW: {
                return KnowFragment.newInstance();
            }
            case MisterStatus.LOVE: {
                return LoveFragment.newInstance();
            }
            default: {
                return MeetFragment.newInstance();
            }
        }
    }


    @Override
    protected void onPostResume() {
        if (haveNoCommitStatusFragment) {
            Fragment oldStatusFragment = statusFragment;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            statusFragment = createStatusFragment();
            transaction.add(R.id.main_frame, statusFragment);
            if (currentFragment == oldStatusFragment) {
                currentFragment = statusFragment;
            } else {
                transaction.hide(statusFragment);
            }
            transaction.remove(oldStatusFragment);
            transaction.commit();
            haveNoCommitStatusFragment = false;
        }
        super.onPostResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtil.i("onSaveInstanceState--");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_meet_try_match_start: {
                MisterApi.getInstance().tryStartMatch();
                break;
            }
            case R.id.menu_meet_try_match_stop: {
                MisterApi.getInstance().tryStopMatch();
                break;
            }
        }
        return true;
    }

}

