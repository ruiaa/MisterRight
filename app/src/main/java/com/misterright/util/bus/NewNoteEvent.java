package com.misterright.util.bus;

/**
 * Created by ruiaa on 2016/11/17.
 */

public class NewNoteEvent {
    public int newCount = 0;
    public boolean forceRefresh=false;

    public NewNoteEvent(int newCount) {
        this.newCount = newCount;
        this.forceRefresh=false;
    }

    public NewNoteEvent(int newCount,boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
        this.newCount = newCount;
    }
}
