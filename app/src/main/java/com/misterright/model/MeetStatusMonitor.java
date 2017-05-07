package com.misterright.model;

import android.databinding.ObservableLong;
import android.os.CountDownTimer;

import com.misterright.http.MisterApi;
import com.misterright.model.entity.MisterStatus;
import com.misterright.util.LogUtil;
import com.misterright.util.bus.MeetMatchChangeEvent;
import com.misterright.util.bus.MeetStatusChangeEvent;
import com.misterright.util.bus.MeetTimeChangeEvent;
import com.misterright.util.bus.RxBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ruiaa on 2016/11/2.
 */

public class MeetStatusMonitor {

    private static MeetStatusMonitor instance = null;

    private MisterApi misterApi;
    private ScheduledExecutorService executor;
    public ObservableLong waitingCount = new ObservableLong();
    private CountDownTimer countDownTimer = null;
    private String currentStatus;

    private MeetStatusMonitor() {
        waitingCount.set(0);
        misterApi = MisterApi.getInstance();
        currentStatus = MisterData.getInstance().status.meetStatus;
    }

    private static MeetStatusMonitor getInstance() {
        if (instance == null) {
            synchronized (MeetStatusMonitor.class) {
                if (instance == null) {
                    instance = new MeetStatusMonitor();
                }
            }
        }
        return instance;
    }

    public void startMonitor() {

        waitingCount.set(0);
        misterApi = MisterApi.getInstance();
        currentStatus = MisterData.getInstance().status.meetStatus;


        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::workerForMeet, 0, 10L, TimeUnit.SECONDS);


        RxBus.getDefault().toObservable(MeetTimeChangeEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(meetTimeChangeEvent -> {
                    if (meetTimeChangeEvent.time > 0) {
                        reSetCountDownTimer(meetTimeChangeEvent.time);
                    } else {
                        reSetCountDownTimer(MisterData.getInstance().status.leftTime);
                    }
                });
        RxBus.getDefault().post(new MeetTimeChangeEvent());

        LogUtil.i("startMonitor--");
    }

    public void stopMonitor() {
        if (executor != null) executor.shutdown();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    private void reSetCountDownTimer(long time) {
        /*
         *
         *  主线程
         *
         */
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        countDownTimer = new CountDownTimer(time * 1000, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                waitingCount.set(millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if (MisterStatus.MEET_SELF.equals(currentStatus)) {
                    RxBus.getDefault().post(new MeetTimeChangeEvent(59));
                    waitStatusForJoin();
                } else if (MisterStatus.MEET_JOIN.equals(currentStatus)) {
                    RxBus.getDefault().post(new MeetTimeChangeEvent(59));
                    waitStatusForMatch();
                } else if (MisterStatus.MEET_MATCH.equals(currentStatus)) {
                    waitStatusForAnotherMatch();
                    RxBus.getDefault().post(new MeetTimeChangeEvent(59));
                }

            }
        }.start();

    }

    private void workerForMeet() {
        MisterStatus status = misterApi.getStatusSync();
        if (status == null) {
            return;
        }
        if (MisterStatus.KNOW.equals(status.pageStatus)) {
            MisterData.getInstance().status = status;
            RxBus.getDefault().post(new MeetStatusChangeEvent(status.pageStatus));
            stopMonitor();
        } else if (!status.meetStatus.equals(currentStatus)) {
            currentStatus = status.meetStatus;
            MisterData.getInstance().status = status;
            RxBus.getDefault().post(new MeetTimeChangeEvent());
            RxBus.getDefault().post(new MeetStatusChangeEvent(status.meetStatus));
        } else if (status.leftTime != waitingCount.get()) {
            MisterData.getInstance().status = status;
            RxBus.getDefault().post(new MeetTimeChangeEvent());
        }
    }


    public void waitStatusForJoin() {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                MisterStatus status = misterApi.getStatusSync();
                if (status != null
                        && status.pageStatus.equals(MisterStatus.MEET)
                        && status.meetStatus.equals(MisterStatus.MEET_JOIN)) {
                    MisterData.getInstance().status = status;
                    RxBus.getDefault().post(new MeetTimeChangeEvent());
                    RxBus.getDefault().post(new MeetStatusChangeEvent(status.meetStatus));
                    break;
                } else {
                    try {
                        Thread.sleep(100L);
                    } catch (Exception e) {
                        LogUtil.e("waitKnowStatus--", e);
                    }
                }
            }
        }).start();
    }

    public void waitStatusForMatch() {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                MisterStatus status = misterApi.getStatusSync();
                if (status != null
                        && status.pageStatus.equals(MisterStatus.MEET)
                        && !status.meetStatus.equals(MisterStatus.MEET_JOIN)) {
                    MisterData.getInstance().status = status;
                    RxBus.getDefault().post(new MeetTimeChangeEvent());
                    RxBus.getDefault().post(new MeetStatusChangeEvent(status.meetStatus));
                    break;
                } else {
                    try {
                        Thread.sleep(100L);
                    } catch (Exception e) {
                        LogUtil.e("waitKnowStatus--", e);
                    }
                }
            }
        }).start();
    }

    public void waitStatusForAnotherMatch() {
        final long uid = MisterData.getInstance().status.matchUserInfos.get(0).uid;
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                MisterStatus status = misterApi.getStatusSync();
                if (status != null && status.pageStatus.equals(MisterStatus.MEET)
                        && status.meetStatus.equals(MisterStatus.MEET_MATCH)
                        && uid != status.matchUserInfos.get(0).uid) {
                    MisterData.getInstance().status = status;
                    RxBus.getDefault().post(new MeetMatchChangeEvent());
                    break;
                } else if (status != null && status.pageStatus.equals(MisterStatus.MEET)
                        && status.meetStatus.equals(MisterStatus.MEET_SELF)) {
                    MisterData.getInstance().status = status;
                    RxBus.getDefault().post(new MeetTimeChangeEvent());
                    RxBus.getDefault().post(new MeetStatusChangeEvent(status.meetStatus));
                    break;
                }else if(status != null && status.pageStatus.equals(MisterStatus.KNOW)){
                    MisterData.getInstance().status = status;
                    RxBus.getDefault().post(new MeetStatusChangeEvent(status.pageStatus));
                } else {
                    try {
                        Thread.sleep(100L);
                    } catch (Exception e) {
                        LogUtil.e("waitKnowStatus--", e);
                    }
                }
            }
        }).start();
    }

    public void waitStatusForKnow() {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                MisterStatus status = misterApi.getStatusSync();
                LogUtil.i("waitLikeStatus--" + status.pageStatus);
                if (status != null && status.pageStatus.equals(MisterStatus.KNOW)) {
                    MisterData.getInstance().status = status;
                    MeetStatusMonitor.getInstance().stopMonitor();
                    MisterApi.getInstance()
                            .getOtherInfo(MisterData.getInstance().status.pairInfo.targetUid)
                            .subscribe(userInfo -> {
                                MisterData.getInstance().otherUser=userInfo;
                                RxBus.getDefault().post(new MeetStatusChangeEvent(status.pageStatus));
                            });
                    break;
                } else {
                    try {
                        Thread.sleep(100L);
                    } catch (Exception e) {
                        LogUtil.e("waitKnowStatus--", e);
                    }
                }
            }
        }).start();
    }

}
