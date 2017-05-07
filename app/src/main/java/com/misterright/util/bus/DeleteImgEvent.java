package com.misterright.util.bus;

/**
 * Created by ruiaa on 2016/11/16.
 */

public class DeleteImgEvent {
    public String from;
    public int position;
    public String url;

    public DeleteImgEvent(String from, int position, String url) {
        this.from = from;
        this.position = position;
        this.url = url;
    }
}
