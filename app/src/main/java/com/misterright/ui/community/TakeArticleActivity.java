package com.misterright.ui.community;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.EditText;

import com.misterright.MisterConfig;
import com.misterright.R;
import com.misterright.databinding.ActivityCommunityTakeArticleBinding;
import com.misterright.http.MisterApi;
import com.misterright.ui.DialogHelper;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.ui.widget.NineImg;
import com.misterright.util.net.UploadQiniu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class TakeArticleActivity extends ToolbarActivity {
    private static final String TOPIC="topic";
    private static final int TAKE_ARTICLE_SELECT_IMG = 55;
    private static final int TAKE_ARTICLE_MAX_IMG_COUNT = 9;
    private static final int TAKE_ARTICLE_MIN_TITLE = 2;

    @BindView(R.id.community_take_article_title)
    EditText articleTitle;
    @BindView(R.id.community_take_article_content)
    EditText articleContent;
    @BindView(R.id.community_take_article_img_selector)
    NineImg nineImg;

    private ActivityCommunityTakeArticleBinding binding;
    private DialogHelper dialogHelper;
    private ArrayList<String> imgPaths;
    private Map<String, String> imgKey;
    private String topic;

    public static Intent newIntent(Context context,String topic){
        Intent intent=new Intent(context,TakeArticleActivity.class);
        intent.putExtra(TOPIC,topic);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_take_article);
        ButterKnife.bind(this);
        topic=getIntent().getStringExtra(TOPIC);

        dialogHelper = new DialogHelper(this);
        imgPaths = new ArrayList<>();
        imgKey = new HashMap<>();

        setToolbar();
        setNineImg();
    }

    private void setToolbar() {
        initToolbar();
        setTitle(R.string.release_post);
        setToolbarLeftShow(v -> {
            TakeArticleActivity.this.finish();
        });
        setToolbarRightText(R.string.release, v -> {
            if (checkInput()) {
                releaseArticle();
                TakeArticleActivity.this.finish();
            }
        });

    }

    private void setNineImg(){
        nineImg.setOnItemClickListener((position, view) -> {
            selectImg();
        });
        nineImg.setOnSelectorClickListener((position, view) -> {
            selectImg();
        });
    }

    private void selectImg(){
        MultiImageSelector.create()
                .showCamera(true) // 是否显示相机. 默认为显示
                .count(TAKE_ARTICLE_MAX_IMG_COUNT) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                //.single() // 单选模式
                .multi() // 多选模式, 默认模式;
                .origin(imgPaths) // 默认已选择图片. 只有在选择模式为多选时有效
                .start(this, TAKE_ARTICLE_SELECT_IMG);
    }

    private boolean checkInput() {
        if (articleTitle.getText().length() < TAKE_ARTICLE_MIN_TITLE) {
            dialogHelper.showTextPositiveDialog(R.string.community_article_title_too_short);
            return false;
        } else {
            return true;
        }
    }

    private void releaseArticle() {
        ArrayList<String> imgKeyList=new ArrayList<>();
        for (String s : imgPaths) {
            if (imgKey.containsKey(s)) {
                imgKeyList.add(imgKey.get(s));
            }
        }
        String title=articleTitle.getText().toString()+"";
        String detailText=articleContent.getText().toString()+"";
        String outlineText;
        if (detailText.length()> MisterConfig.COMMUNITY_OUTLINE_MAC_COUNT){
            outlineText=detailText.substring(0,MisterConfig.COMMUNITY_OUTLINE_MAC_COUNT-1);
        }else {
            outlineText=detailText;
        }
        MisterApi.getInstance().postCommunityArticleAndSendEvent(topic,title,outlineText,detailText,imgKeyList);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == TAKE_ARTICLE_SELECT_IMG && data != null) {
            ArrayList<String> newPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            imgPaths = newPaths;
            //nineImg.resetAllImgPaths(imgPaths);
            for (String s : newPaths) {
                if (!imgKey.containsKey(s)) {
                    String key = UploadQiniu.getInstance().upload(s);
                    if (key != null) {
                        imgKey.put(s, key);
                    }
                }
            }
            nineImg.resetAllImgPaths(imgPaths);
        }
    }
}
