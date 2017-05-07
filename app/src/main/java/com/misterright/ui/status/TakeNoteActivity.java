package com.misterright.ui.status;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import com.misterright.R;
import com.misterright.databinding.ActivityTakeNoteBinding;
import com.misterright.http.MisterApi;
import com.misterright.model.entity.MisterNote;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.ui.widget.NineImg;
import com.misterright.util.LogUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.bus.NewNoteEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.net.UploadQiniu;
import com.misterright.util.storage.FileStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class TakeNoteActivity extends ToolbarActivity {

    private static final int TAKE_NOTE_PICK_IMG = 11;
    private static final int TAKE_NOTE_PICK_IMG_MULTI = 12;

    private static final int MAX_PICK_IMG_COUNT = 9;

    private EditText editText;
    private NineImg nineImg;
    private ArrayList<String> imgPaths;
    private List<String> imgKeys;
    private Map<String, String> imgKeyToPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTakeNoteBinding binding = (ActivityTakeNoteBinding) DataBindingUtil
                .setContentView(this, R.layout.activity_take_note);
        fixKeyboardAdjustResizeNoEffect();

        editText = binding.takeNoteContent;
        nineImg = binding.takeNoteImg;
        nineImg.setOnItemClickListener((position, view) -> {
            pickImgMulti();
        });
        nineImg.setOnSelectorClickListener((position, view) -> {
            pickImgMulti();
        });
        imgPaths = new ArrayList<>();
        imgKeyToPath = new HashMap<>();
        imgKeys = new ArrayList<>();

        initToolbar();
        setTitle(R.string.know);
        setToolbarLeftShow(v -> {
            onBackPressed();
        });
        setToolbarRightText(R.string.release,v -> {
            releaseNote();
        });
    }

    private void releaseNote() {
        String content = editText.getText().toString();
        for (String s : imgPaths) {
            if (imgKeyToPath.containsKey(s)) {
                imgKeys.add(imgKeyToPath.get(s));
            }
        }
        if (!content.isEmpty() || !imgKeys.isEmpty()) {
            MisterApi.getInstance()
                    .sendNote(new MisterNote(content, imgKeys))
                    .subscribe(
                            actionResult -> {
                                RxBus.getDefault().postDelay(new NewNoteEvent(1,true),1000);
                            },
                            throwable -> {
                                LogUtil.e("releaseNote--", throwable);
                            }
                    );

            finish();
        } else {
            ToastUtil.showLong(R.string.donot_write_content);
        }

    }

    private void pickImgMulti() {
        MultiImageSelector.create(this)
                .showCamera(true) // 是否显示相机. 默认为显示
                .count(MAX_PICK_IMG_COUNT) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                //.single() // 单选模式
                .multi() // 多选模式, 默认模式;
                .origin(imgPaths) // 默认已选择图片. 只有在选择模式为多选时有效
                .start(this, TAKE_NOTE_PICK_IMG_MULTI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == TAKE_NOTE_PICK_IMG && data != null) {
            Uri uri = data.getData();
            String p = FileStorage.getPath(this, uri);
            if (p != null) {
                //nineImg.addImgLocalPath(p);
                //imgPaths.add(p);
                //imgKeys.add(UploadQiniu.getInstance().upload(p));
            }
        } else if (requestCode == TAKE_NOTE_PICK_IMG_MULTI && data != null) {
            ArrayList<String> newPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            imgPaths = newPaths;
            nineImg.resetAllImgPaths(imgPaths);
            for (String s : newPaths) {
                if (!imgKeyToPath.containsKey(s)) {
                    String key= UploadQiniu.getInstance().upload(s);
                    if (key!=null) {
                        imgKeyToPath.put(s, key);
                    }
                }
            }
        }
    }


    private void initImgFlow() {
/*        nineImg.addImgRes(android.R.drawable.ic_menu_camera)
                .setOnClickListener(v -> {
                    //pickImgMulti();
                    pickImg();
                });*/



    }

    private void pickImg() {
        Intent intent =
                //new Intent(Intent.ACTION_PICK);
                new Intent(Intent.ACTION_GET_CONTENT);
        //new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //多图
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        intent.setType("image/*");
        startActivityForResult(intent, TAKE_NOTE_PICK_IMG);
    }
}
