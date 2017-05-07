package com.misterright.util.bus;

/**
 * Created by ruiaa on 2016/11/5.
 */

public class MeetDislikeEvent {

    public  long targetUid;

    public MeetDislikeEvent(long targetUid) {
        this.targetUid = targetUid;
    }
}
