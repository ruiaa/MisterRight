package com.misterright.model;

import android.databinding.ObservableLong;

import com.misterright.http.MisterApi;
import com.misterright.model.entity.MisterStatus;
import com.misterright.util.LogUtil;
import com.misterright.util.bus.MeetDislikeEvent;
import com.misterright.util.bus.MeetJoinEvent;
import com.misterright.util.bus.MeetLikeEvent;
import com.misterright.util.bus.MeetMatchChangeEvent;
import com.misterright.util.bus.MeetStatusChangeEvent;
import com.misterright.util.bus.RxBus;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ruiaa on 2016/11/5.
 */

public class StatusMonitor {

    private static StatusMonitor instance = null;

    private CompositeSubscription compositeSubscription;
    public ObservableLong waitingCount;
    private boolean hasAction = false;

    private StatusMonitor() {
        if (waitingCount==null) {
            waitingCount = new ObservableLong();
        }
        waitingCount.set(60);
    }

    public static StatusMonitor getInstance() {
        if (instance == null) {
            synchronized (StatusMonitor.class) {
                if (instance == null) {
                    instance = new StatusMonitor();
                }
            }
        }
        return instance;
    }

    public static void stop() {
        if (instance != null) {
            instance.stopMonitor();
        }
    }

    public void startMonitor() {
        if (compositeSubscription!=null&&!compositeSubscription.isUnsubscribed()){
            compositeSubscription.isUnsubscribed();
        }

        compositeSubscription = new CompositeSubscription();
/*        Subscription joinSub = RxBus.getDefault().toObservable(MeetJoinEvent.class)
                .subscribe(meetJoinEvent -> {
                    MisterApi.getInstance()
                            .matchJoin()
                            .subscribe(responseBody -> {

                            }, throwable -> {
                                LogUtil.e("StatusMonitor--", throwable);
                                rePost(meetJoinEvent);
                            });
                });*/
        Subscription joinSub = RxBus.getDefault().toObservable(MeetJoinEvent.class)
                .flatMap(meetJoinEvent -> {
                    return MisterApi.getInstance().matchJoin();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        actionResult -> {
                            LogUtil.i("startMonitor--meetJoinEvent");
                        }, throwable -> {
                            LogUtil.e("startMonitor--meetJoinEvent", throwable);
                        }
                );
/*        Subscription likeSub = RxBus.getDefault().toObservable(MeetLikeEvent.class)
                .subscribe(meetLikeEvent -> {
                    MisterApi.getInstance()
                            .matchLike(String.valueOf(meetLikeEvent.targetUid))
                            .subscribe(responseBody -> {

                            }, throwable -> {
                                LogUtil.e("StatusMonitor--", throwable);
                                rePost(meetLikeEvent);
                            });
                });*/
        Subscription likeSub = RxBus.getDefault().toObservable(MeetLikeEvent.class)
                .flatMap(meetLikeEvent -> {
                    return MisterApi.getInstance().matchLike(String.valueOf(meetLikeEvent.targetUid));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        actionResult -> {
                            LogUtil.i("startMonitor--meetLikeEvent");
                        }, throwable -> {
                            LogUtil.e("statusMonitor--", throwable);
                        }
                );
/*        Subscription dislikeSub = RxBus.getDefault().toObservable(MeetDislikeEvent.class)
                .subscribe(meetDislikeEvent -> {
                    MisterApi.getInstance()
                            .matchDislike(String.valueOf(meetDislikeEvent.targetUid))
                            .subscribe(responseBody -> {
                                hasAction = true;
                            }, throwable -> {
                                LogUtil.e("StatusMonitor--", throwable);
                                rePost(meetDislikeEvent);
                            });
                });*/
        Subscription dislikeSub = RxBus.getDefault().toObservable(MeetDislikeEvent.class)
                .flatMap(meetDislikeEvent -> {
                    return MisterApi.getInstance().matchDislike(String.valueOf(meetDislikeEvent.targetUid));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        actionResult -> {
                            LogUtil.i("startMonitor--meetDislikeEvent");
                            hasAction = true;
                        }, throwable -> {
                            LogUtil.e("statusMonitor--meetDislikeEvent", throwable);
                        }
                );

        Subscription timeSub = Observable
                .interval(1000L, TimeUnit.MILLISECONDS)
                .map(aLong -> {

                    /*if (aLong%2==0){*/
                    if (waitingCount.get() != 0) {
                        waitingCount.set(waitingCount.get() - 1);
                    }
                    LogUtil.v("startMonitor--      waitingCount" + waitingCount.get());


                    if (hasAction) {
                        return MisterApi.getInstance().getStatusSync();
                    } else if (waitingCount.get() == 0 || aLong % 10 == 0) {
                        return MisterApi.getInstance().getStatusSync();
                    } else {
                        return null;
                    }

                })
                .filter(misterStatus -> misterStatus != null)
                .subscribe(
                        misterStatus -> {
                            LogUtil.v("startMonitor--leftTime" + misterStatus.leftTime);

                            hasAction = false;

                            waitingCount.set(misterStatus.leftTime);
                            judgeAndPostStatusChange(misterStatus);

                        },
                        throwable -> {
                            LogUtil.e("startMonitor--", throwable);
                        }
                );


        compositeSubscription.add(joinSub);
        compositeSubscription.add(likeSub);
        compositeSubscription.add(dislikeSub);
        compositeSubscription.add(timeSub);
    }

    public void stopMonitor() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }

    private void judgeAndPostStatusChange(MisterStatus newStatus) {
        if (newStatus.pageStatus.equals(MisterStatus.MEET)) {

            if (!newStatus.meetStatus.equals(MisterData.getInstance().status.meetStatus)) {
                MisterData.getInstance().status = newStatus;
                RxBus.getDefault().post(new MeetStatusChangeEvent());

            } else if (newStatus.meetStatus.equals(MisterStatus.MEET_MATCH)
                    && newStatus.matchUserInfos.get(0).uid
                    != MisterData.getInstance().status.matchUserInfos.get(0).uid) {
                MisterData.getInstance().status = newStatus;
                RxBus.getDefault().post(new MeetMatchChangeEvent());
            }

        } else if (newStatus.pageStatus.equals(MisterStatus.KNOW)) {
            stopMonitor();
            MisterData.getInstance().status = newStatus;
            MisterApi.getInstance()
                    .getOtherInfo(MisterData.getInstance().status.pairInfo.targetUid)
                    .subscribe(
                            userInfo -> {
                                MisterData.getInstance().otherUser = userInfo;
                                RxBus.getDefault().post(new MeetStatusChangeEvent());
                            },
                            throwable -> {
                                LogUtil.e("judgeAndPostStatusChange--", throwable);
                            }
                    );
        }
        MisterData.getInstance().status = newStatus;
    }


    private void rePost(Object o) {
        RxBus.getDefault().postDelay(o, 5000);
    }

}
