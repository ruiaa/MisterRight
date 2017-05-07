package com.misterright.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.misterright.R;
import com.misterright.http.MisterApi;
import com.misterright.model.MisterData;
import com.misterright.model.entity.MisterStatus;
import com.misterright.ui.PictureActivity;
import com.misterright.util.LogUtil;
import com.misterright.util.net.ImgSizeGlideModule;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by ruiaa on 2016/10/18.
 */

public class ImgViewGlide extends ImageView {

    protected boolean isCircle = false;
    protected boolean isSquare = false;
    protected float heightFromWidth = -1f;
    protected int blurDegree = -1;
    protected boolean autoBlur = false;
    protected boolean autoOpenBig=false;
    protected int corner = -1;
    protected RoundedCornersTransformation.CornerType cornerType = RoundedCornersTransformation.CornerType.ALL;


    private int resId = -1;
    private String path;


    public ImgViewGlide(Context context) {
        super(context);
    }

    public ImgViewGlide(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
    }

    public ImgViewGlide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImgViewGlide);

        isCircle = a.getBoolean(R.styleable.ImgViewGlide_is_circle, false);
        isSquare = a.getBoolean(R.styleable.ImgViewGlide_is_square, false);
        heightFromWidth = a.getFloat(R.styleable.ImgViewGlide_height_from_width, -1);

        blurDegree = a.getInteger(R.styleable.ImgViewGlide_blur_degree, -1);
        if (blurDegree > 25) blurDegree = 25;
        autoBlur = a.getBoolean(R.styleable.ImgViewGlide_auto_blur, false);
        if (autoBlur) {
            blurDegree = getAutoBlurDegree();
        } else {
            blurDegree = -1;
        }

        corner = a.getDimensionPixelSize(R.styleable.ImgViewGlide_corner, 0);
        cornerType = getCornerType(a.getInteger(R.styleable.ImgViewGlide_corner_type, 0));

        resId = a.getResourceId(R.styleable.ImgViewGlide_img_res_id, -1);
        path = a.getString(R.styleable.ImgViewGlide_img_path);


        if (resId != -1) {
            setImgResId(this, resId);
        } else if (path != null) {
            setImgUrl(this, path);
        }

        autoOpenBig=a.getBoolean(R.styleable.ImgViewGlide_auto_open_big,false);
        autoOpenBigByClick();
        a.recycle();
    }

    public void setAutoBlur(boolean autoBlur) {
        this.autoBlur = autoBlur;
        if (autoBlur) {
            blurDegree = getAutoBlurDegree();
        } else {
            blurDegree = -1;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isSquare) {
            int s = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
            setMeasuredDimension(s, s);
        } else if (heightFromWidth != -1f) {
            int w = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(w, (int) (w * heightFromWidth));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void autoOpenBigByClick(){
        if (autoOpenBig){
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (path==null) return;
                    ArrayList<String> list=new ArrayList<>();
                    list.add(path);
                    getContext().startActivity(PictureActivity.newIntent(
                            getContext(),
                            list,
                            0,
                            PictureActivity.TYPE_SAVE,
                            "autoOpenSave"
                    ));
                }
            });
        }
    }

    @BindingAdapter("autoOpenBigImg")
    public static void setAutoOpenBigImg(ImgViewGlide img,String s){
        if (s==null) return;
        img.setOnClickListener(new OnClickListener() {
            final  String bigpath=s;
            @Override
            public void onClick(View v) {
                LogUtil.i("onClick--");
                if (bigpath==null) return;
                ArrayList<String> list=new ArrayList<>();
                list.add(bigpath);
                img.getContext().startActivity(PictureActivity.newIntent(
                        img.getContext(),
                        list,
                        0,
                        PictureActivity.TYPE_SAVE,
                        "autoOpenSave"
                ));
            }
        });
    }

    public void openBigImg(String p){
        if (p==null) return;
        ArrayList<String> list=new ArrayList<>();
        list.add(p);
        getContext().startActivity(PictureActivity.newIntent(
                getContext(),
                list,
                0,
                PictureActivity.TYPE_SAVE,
                "autoOpenSave"
        ));
    }

    private DrawableRequestBuilder setTransformation(DrawableRequestBuilder builder) {
        List<Transformation<Bitmap>> transformationList = new ArrayList<>();
        transformationList.add(new CenterCrop(getContext()));
        if (isSquare) {
            transformationList.add(new CropSquareTransformation(getContext()));
        }
        if (isCircle) {
            transformationList.add(new CropCircleTransformation(getContext()));
        } else if (corner > 0) {
            transformationList.add(new RoundedCornersTransformation(getContext(), corner, 0, cornerType));
        }
        if (blurDegree > 0) {
            blurDegree = blurDegree > 25 ? 25 : blurDegree;
            transformationList.add(new BlurTransformation(getContext(), blurDegree));
        }
        Transformation<Bitmap>[] t = new Transformation[transformationList.size()];
        transformationList.toArray(t);
        return builder.bitmapTransform(t);
    }

    public String getPath() {
        return path;
    }

    public int getResId() {
        return resId;
    }

    @BindingAdapter("imageUrl")
    public static void setImgUrl(ImgViewGlide imageView, String s) {
        if (s == null) return;
        DrawableRequestBuilder builder =
                Glide.with(imageView.getContext())
                        .load(s)
                        .crossFade();
        imageView.setTransformation(builder).into(imageView);
        imageView.path = s;
    }

    public void setImgUrl(String s) {
        setImgUrl(this, s);
    }

    @BindingAdapter("imageResId")
    public static void setImgResId(ImgViewGlide imageView, int s) {

        DrawableRequestBuilder builder =
                Glide.with(imageView.getContext())
                        .load(s)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade();
        imageView.setTransformation(builder).into(imageView);
        imageView.resId = s;
    }

    public void setImgResId(int i) {
        setImgResId(this, i);
    }

    @BindingAdapter("imageUrlOrId")
    public static void setImgSource(ImgViewGlide imgViewGlide,Object o){
        imgViewGlide.setImgSource(o);
    }

    public void setImgSource(Object o) {
        if (o instanceof String) {
            setImgUrl((String) o);
        } else if (o instanceof Integer) {
            setImgResId((Integer) o);
        }
    }


    @BindingAdapter("head")
    public static void setHead(ImgViewGlide imageView, String key) {
        setQiniu(imageView, key);
    }

    @BindingAdapter("autoHead")
    public static void setAutoHead(ImgViewGlide imgViewGlide, boolean isMine) {
        if ((imgViewGlide.path!=null&&!imgViewGlide.path.isEmpty())&&imgViewGlide.autoBlur!=isMine) return;
        if (isMine) {
            imgViewGlide.setAutoBlur(false);
            imgViewGlide.setQiniu(MisterData.getInstance().globalInfo.userInfo.getHeadUrl());
        } else {
            imgViewGlide.setAutoBlur(true);
            imgViewGlide.setQiniu(MisterData.getInstance().otherUser.getHeadUrl());
        }
       // LogUtil.i("setAutoHead--"+isMine);
    }

    @BindingAdapter("qiniu")
    public static void setQiniu(ImgViewGlide imageView, String key) {
        if (key == null || key.isEmpty()) return;
        String s = MisterApi.QINIU_BASE_URL + key;
        DrawableRequestBuilder builder =
                Glide.with(imageView.getContext())
                        .load(new ImgSizeGlideModule.MyImgSizeModel(s))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade();
        imageView.setTransformation(builder).into(imageView);
        imageView.path = s;
    }

    public void setQiniu(String key) {
        setQiniu(this, key);
    }


    public static int getAutoBlurDegree() {
        if (MisterStatus.KNOW.equals(MisterData.getInstance().status.pageStatus)) {
            int day = (int) (System.currentTimeMillis() / 1000 - MisterData.getInstance().status.pairInfo.startTime) / (24 * 60 * 60);
            if (day >= 25 || day < 0) {
                return -1;
            } else {
                return 25 - day;
            }
        } else {
            return -1;
        }
    }

    public static RoundedCornersTransformation.CornerType getCornerType(int i) {
        switch (i) {
            case 0: {
                return RoundedCornersTransformation.CornerType.ALL;
            }
            case 1: {
                return RoundedCornersTransformation.CornerType.TOP_LEFT;
            }
            case 2: {
                return RoundedCornersTransformation.CornerType.TOP_RIGHT;
            }
            case 3: {
                return RoundedCornersTransformation.CornerType.BOTTOM_RIGHT;
            }
            case 4: {
                return RoundedCornersTransformation.CornerType.BOTTOM_LEFT;
            }
            case 12: {
                return RoundedCornersTransformation.CornerType.TOP;
            }
            case 34: {
                return RoundedCornersTransformation.CornerType.BOTTOM;
            }
            case 14: {
                return RoundedCornersTransformation.CornerType.LEFT;
            }
            case 23: {
                return RoundedCornersTransformation.CornerType.RIGHT;
            }
            case 234: {
                return RoundedCornersTransformation.CornerType.OTHER_TOP_LEFT;
            }
            case 134: {
                return RoundedCornersTransformation.CornerType.OTHER_TOP_RIGHT;
            }
            case 123: {
                return RoundedCornersTransformation.CornerType.OTHER_BOTTOM_LEFT;
            }
            case 124: {
                return RoundedCornersTransformation.CornerType.OTHER_BOTTOM_RIGHT;
            }
            case 13: {
                return RoundedCornersTransformation.CornerType.DIAGONAL_FROM_TOP_LEFT;
            }
            case 24: {
                return RoundedCornersTransformation.CornerType.DIAGONAL_FROM_TOP_RIGHT;
            }
            default: {
                return RoundedCornersTransformation.CornerType.ALL;
            }
        }
    }

/*    public static void setImgResId(ImgViewGlide imageView, int s) {
        if (imageView.isCircle && imageView.isBlur) {
            Glide.with(imageView.getContext())
                    .load(s)
                    .crossFade()
                    .bitmapTransform(new CropCircleTransformation(imageView.getContext()), new BlurTransformation(imageView.getContext()))
                    .into(imageView);
        } else if (imageView.isCircle) {
            Glide.with(imageView.getContext())
                    .load(s)
                    .crossFade()
                    .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                    .into(imageView);
        } else if (imageView.isBlur) {
            Glide.with(imageView.getContext())
                    .load(id)
                    .crossFade()
                    .bitmapTransform(new BlurTransformation(imageView.getContext()))
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(s)
                    .crossFade()
                    .centerCrop()
                    .into(imageView);
        }
    }*/
}
