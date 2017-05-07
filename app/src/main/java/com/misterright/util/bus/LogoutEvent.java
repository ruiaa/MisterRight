package com.misterright.util.bus;

/**
 * Created by ruiaa on 2016/11/14.
 */

public class LogoutEvent {
    public Class<?>  targetActivity;

    public LogoutEvent(Class<?> targetActivity) {
        this.targetActivity = targetActivity;
    }
}
