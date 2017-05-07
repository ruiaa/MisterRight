package com.misterright.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.misterright.R;
import com.misterright.databinding.ActivityPictureBinding;
import com.misterright.ui.base.ToolbarActivity;
import com.misterright.util.LogUtil;
import com.misterright.util.ToastUtil;
import com.misterright.util.bus.DeleteImgEvent;
import com.misterright.util.bus.RxBus;
import com.misterright.util.storage.FileStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PictureActivity extends ToolbarActivity {

    private static final String FROM = "from";
    private static final String IMAGE_URL = "img_url";
    private static final String IMG_POSITION = "position";
    private static final String MENU_TYPE = "type";
    public static final int TYPE_SAVE = 1;
    public static final int TYPE_DELETE = 2;

    @BindView(R.id.activity_picture_image)
    ImageView imageView;
    @BindView(R.id.activity_picture)
    View rootLayout;
    private String from = "";
    private int type;
    private PhotoViewAttacher photoViewAttacher;
    private ArrayList<String> imgUrls;
    private int position = 0;
    private int size = 0;

    public static Intent newIntent(Context context, ArrayList<String> urls, int position, int type, String from) {
        if (urls==null||urls.isEmpty()) return null;
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putStringArrayListExtra(IMAGE_URL, urls);
        intent.putExtra(IMG_POSITION, position);
        intent.putExtra(MENU_TYPE, type);
        intent.putExtra(FROM, from);
        return intent;
    }

    public static Intent newIntent(Context context, String url,int type){
        if (url==null||url.isEmpty()) return null;
        ArrayList<String> list=new ArrayList<>();
        list.add(url);
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putStringArrayListExtra(IMAGE_URL, list);
        intent.putExtra(IMG_POSITION, 0);
        intent.putExtra(MENU_TYPE, type);
        intent.putExtra(FROM, "one");
        return intent;
    }

    public static Intent newIntentForSave(Context context, String url){
        return newIntent(context,url,TYPE_SAVE);
    }

    private void parseIntent() {
        imgUrls = getIntent().getStringArrayListExtra(IMAGE_URL);
        if (imgUrls == null || imgUrls.isEmpty()) {
            finish();
            LogUtil.e("parseIntent--no img");
        }
        size = imgUrls.size();

        position = getIntent().getIntExtra(IMG_POSITION, 0);
        type = getIntent().getIntExtra(MENU_TYPE, TYPE_SAVE);
        from = getIntent().getStringExtra(FROM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPictureBinding binding=DataBindingUtil.setContentView(this,R.layout.activity_picture);
        ButterKnife.bind(this);
        parseIntent();

        initPhotoAttacher();
        loadImg(0);

        setToolbar();
    }

    private void setToolbar() {
        initToolbar();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setToolbarLeftShow(v -> onBackPressed());
        toolbar.inflateMenu(type == TYPE_SAVE ? R.menu.menu_picture_save : R.menu.menu_picture_delete);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_picture_save: {
                    savePicture();
                    return true;
                }
                case R.id.menu_picture_delete: {
                    deletePicture();
                    return true;
                }
            }
            return true;
        });
    }

    private void loadImg(int offset) {
        position = position + offset;
        if (position < 0) position = size - 1;
        position = position % size;
        imageView.setImageResource(R.drawable.black);
        photoViewAttacher.update();
        Glide.with(this)
                .load(imgUrls.get(position))
                .dontAnimate()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        imageView.setImageDrawable(resource);
                        photoViewAttacher.update();
                    }
                });
    }

    private void initPhotoAttacher() {
        photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.setOnViewTapListener((view, v, v1) -> hideOrShowToolbar());
        photoViewAttacher.setOnSingleFlingListener((e1, e2, velocityX, velocityY) -> {

            if (velocityX > 1500) {
                loadImg(-1);
                return true;
            } else if (velocityX < -1500) {
                loadImg(1);
                return true;
            } else {
                return false;
            }
        });
    }

    private void deletePicture() {
        RxBus.getDefault().post(new DeleteImgEvent(from, position, imgUrls.get(position)));
        if (size <= 1) {
            finish();
        } else {
            size = size - 1;
            imgUrls.remove(position);
            loadImg(0);
        }
    }

    private void savePicture() {
        Subscription s = saveImageAndGetPathObservable(this, imgUrls.get(position), "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        uri -> {
                            String msg = String.format(getString(R.string.tip_picture_has_save_to), FileStorage.getImgSaveDir());
                            ToastUtil.showShort(msg);
                        },
                        error -> ToastUtil.showLong(error.getMessage() + "\n再试试..."));
        addSubscription(s);
    }

    public static Observable<Uri> saveImageAndGetPathObservable(Context context, String url, String title) {
        return Observable
                .create(new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(final Subscriber<? super Bitmap> subscriber) {
                        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource == null) {
                                    subscriber.onError(new Exception("无法下载到图片"));
                                }
                                subscriber.onNext(resource);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(bitmap -> {
                    File appDir = new File(FileStorage.getImgSaveDir());
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    String fileName;
                    if (title==null||title.isEmpty()){
                        fileName="img-"+System.currentTimeMillis()/1000+ ".jpg";
                    }else {
                        fileName = title.replace('/', '-') + ".jpg";
                    }
                    File file = new File(appDir, fileName);
                    try {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        assert bitmap != null;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Uri uri = Uri.fromFile(file);
                    // 通知图库更新
                    Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                    context.sendBroadcast(scannerIntent);

                    return Observable.just(uri);
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photoViewAttacher != null) {
            photoViewAttacher.cleanup();
        }
    }



/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (type==TYPE_SAVE) {
            getMenuInflater().inflate(R.menu.menu_picture_save, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_picture_delete,menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_picture_save: {
                savePicture();
                return true;
            }
            case R.id.menu_picture_delete:{
                deletePicture();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }*/
}
