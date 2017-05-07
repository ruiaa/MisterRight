package com.misterright.util.bus;

/**
 * Created by ruiaa on 2016/11/3.
 */

public class MeetStatusChangeEvent {

    private String status;

    public MeetStatusChangeEvent(String status) {
        this.status = status;
    }

    public MeetStatusChangeEvent() {
    }
}
