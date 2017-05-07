package com.misterright.util.bus;

import com.misterright.model.entity.CommunityArticle;

/**
 * Created by ruiaa on 2016/11/27.
 */

public class PostCommunityArticleEvent {
    public CommunityArticle article;

    public PostCommunityArticleEvent(CommunityArticle article) {
        this.article = article;
    }
}
