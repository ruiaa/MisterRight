package com.misterright.ui.community;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.TextView;

import com.misterright.R;
import com.misterright.databinding.ActivityCommunityArticleOutlineListBinding;
import com.misterright.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArticleOutlineListActivity extends BaseActivity {
    private static final String TOPIC="topic";

    @BindView(R.id.community_article_outline_list_hot)
    TextView articleHot;
    @BindView(R.id.community_article_outline_list_new)
    TextView articleNew;

    private ActivityCommunityArticleOutlineListBinding binding;
    private FragmentManager fragmentManager;
    private Fragment hotArticleListFragment;
    private Fragment newArticleListFragment;
    private boolean everShowNew=false;
    private String topic;

    public static Intent newIntent(Context context, String topic){
        Intent intent=new Intent(context,ArticleOutlineListActivity.class);
        intent.putExtra(TOPIC,topic);
        return intent;
    }

    private void parseIntent(){
        topic=getIntent().getStringExtra(TOPIC);
        if (topic==null||topic.isEmpty()){
            topic="love_share";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_article_outline_list);
        ButterKnife.bind(this);

        fragmentManager=getFragmentManager();
        hotArticleListFragment=ArticleOutlineListFragment.newInstance(ArticleOutlineListFragment.HOT_TYPE,topic);
        newArticleListFragment=ArticleOutlineListFragment.newInstance(ArticleOutlineListFragment.NEW_TYPE,topic);
        if (savedInstanceState==null){
            FragmentTransaction t=fragmentManager.beginTransaction();
            t.add(R.id.community_article_outline_list_frame,hotArticleListFragment);
            t.commit();
        }
        articleHot.setSelected(true);
        articleNew.setSelected(false);
    }

    @OnClick(R.id.toolbar_left)
    public void onBack(){
        onBackPressed();
    }

    @OnClick(R.id.toolbar_right)
    public void onOpenWrite(){
        startActivity(TakeArticleActivity.newIntent(this,topic));
    }

    @OnClick(R.id.community_article_outline_list_hot)
    public void onSelectHot(){
        if (articleHot.isSelected()) return;

        FragmentTransaction t=fragmentManager.beginTransaction();
        t.show(hotArticleListFragment);
        t.hide(newArticleListFragment);
        t.commit();

        articleHot.setSelected(true);
        articleNew.setSelected(false);
    }

    @OnClick(R.id.community_article_outline_list_new)
    public void onSelectNew(){
        if (articleNew.isSelected()) return;

        FragmentTransaction t=fragmentManager.beginTransaction();
        t.hide(hotArticleListFragment);
        if (everShowNew){
            t.show(newArticleListFragment);
        }else {
            t.add(R.id.community_article_outline_list_frame,newArticleListFragment);
        }
        t.commit();

        everShowNew=true;
        articleHot.setSelected(false);
        articleNew.setSelected(true);
    }

}
