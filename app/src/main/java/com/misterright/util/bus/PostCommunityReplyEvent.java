package com.misterright.util.bus;

/**
 * Created by ruiaa on 2016/11/27.
 */

public class PostCommunityReplyEvent {
    public String commentId;

    public PostCommunityReplyEvent(String commentId) {
        this.commentId = commentId;
    }
}
