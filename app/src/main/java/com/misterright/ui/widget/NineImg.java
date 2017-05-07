package com.misterright.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterright.R;
import com.misterright.ui.PictureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiaa on 2016/11/7.
 */

public class NineImg extends ViewGroup {

    private static final int DEFAULT_HORIZONTAL_SPACING = 5;
    private static final int DEFAULT_VERTICAL_SPACING = 5;

    private int verticalSpacing;
    private int horizontalSpacing;
    private int grideWidth = 0;
    private int grideHeight = 0;
    private int rows;
    private int columns;
    private float widthRatioToParent = 2;
    private float singleImageHeightRatio = 1;

    private int maxCount=9;
    private boolean withSelector=false;

    private LayoutInflater inflater;
    private Context context;

    private List<Object> imgPaths;
    private List<ImgViewGlide> imgViewGlides;
    private OnItemClickListener onItemClickListener;
    private OnSelectorClickListener onSelectorClickListener;

    public NineImg(Context context) {
        super(context);
        init();
    }

    public NineImg(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NineImg);
        horizontalSpacing = a.getDimensionPixelSize(R.styleable.NineImg_nineimg_h_spacing, DEFAULT_HORIZONTAL_SPACING);
        verticalSpacing = a.getDimensionPixelSize(R.styleable.NineImg_nineimg_w_spacing, DEFAULT_VERTICAL_SPACING);
        maxCount = a.getInteger(R.styleable.NineImg_nineimg_max_count, 9);
        withSelector = a.getBoolean(R.styleable.NineImg_nineimg_with_selector, false);
        if (withSelector && imgPaths.isEmpty()) {
            imgPaths.add(R.mipmap.icon_select_img_padding);
            setVisibility(VISIBLE);
            setImgSource();
        }
        a.recycle();
    }

    private void init() {
        imgPaths = new ArrayList<>();
        imgViewGlides = new ArrayList<>();

        context = getContext();
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (imgPaths == null || imgPaths.isEmpty()) {
            setMeasuredDimension(0, 0);
            return;
        }
        int size = Math.min(imgPaths.size(), imgViewGlides.size());

        int sugMinWidth = getSuggestedMinimumWidth();
        int minWidth = getPaddingLeft() + getPaddingRight() + sugMinWidth;
        int totalWidth = resolveSizeAndState(minWidth, widthMeasureSpec, 0);
        int availableWidth = totalWidth - getPaddingLeft() - getPaddingRight();

        if (size == 1) {
            if (maxCount == 8) {
                grideWidth = availableWidth / 4;
            }else if(withSelector){
                grideWidth = availableWidth / 3;
            } else {
                grideWidth = availableWidth * 2 / 3;
            }
            grideHeight = grideWidth;
        } else {
            if (maxCount==8){
                grideWidth = (availableWidth - horizontalSpacing * (columns - 1)) / 4;
            }else {
                grideWidth = (availableWidth - horizontalSpacing * (columns - 1)) / 3;
            }
            grideHeight = grideWidth;
        }

        int height = rows * grideHeight + (rows - 1) * horizontalSpacing + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(totalWidth, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (imgPaths == null || imgPaths.isEmpty()) return;
        int visibleSize = Math.min(imgPaths.size(), imgViewGlides.size());
        int viewSize = imgViewGlides.size();

        int tmpWidth = grideWidth;
        int tmpHeight = grideHeight;

        for (int i = 0; i < viewSize; ++i) {
            if (i < visibleSize) {
                l = i % columns * (tmpWidth + horizontalSpacing) + getPaddingLeft();
                t = i / columns * (tmpHeight + verticalSpacing) + getPaddingTop();
                r = l + tmpWidth;
                b = t + tmpHeight;
                imgViewGlides.get(i).layout(l, t, r, b);
                imgViewGlides.get(i).setVisibility(VISIBLE);
            } else {
                imgViewGlides.get(i).setVisibility(GONE);
            }
        }
    }


    @BindingAdapter("nineImgPaths")
    public static void setNineImgPaths(NineImg nineImg, List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            nineImg.setVisibility(GONE);
        } else {
            nineImg.setVisibility(VISIBLE);
            nineImg.resetAllImgPaths(paths);
        }
    }

    @BindingAdapter("autoOpenSave")
    public static void setAutoOpenSave(NineImg nineImg,boolean b){
        if (nineImg.onItemClickListener==null&&b){
            nineImg.setOnItemClickListener((position, view) -> {
                if (nineImg.imgPaths==null) return;
                ArrayList<String> list=toStringList(nineImg.imgPaths);
                if (list.isEmpty()) return;
                nineImg.getContext().startActivity(PictureActivity.newIntent(
                        nineImg.context,
                        list,
                        position,
                        PictureActivity.TYPE_SAVE,
                        "autoOpenSave"
                ));
            });
        }
    }

    public static ArrayList<String> toStringList(@NonNull List<Object> list){
        ArrayList<String> arrayList=new ArrayList<>();
        for(Object o:list){
            if (o instanceof String){
                arrayList.add((String) o);
            }
        }
        return arrayList;
    }

    private void setImgSource() {
        int size = Math.min(imgPaths.size(), maxCount);
        initRowAndColum(size);
        int viewSize = imgViewGlides.size();
        for (int i = 0; i < size; i++) {
            if (i >= viewSize) {
                ImgViewGlide imgViewGlide = (ImgViewGlide) inflater.inflate(R.layout.item_image, this, false);
                imgViewGlide.setOnClickListener(new OnClickListener() {
                    final int position = imgViewGlides.size();

                    @Override
                    public void onClick(View v) {
                        if (withSelector && position == imgPaths.size() - 1){
                            if (onSelectorClickListener!=null){
                                onSelectorClickListener.onSelectorClick(position,v);
                            }
                        }else {
                            if (onItemClickListener != null) {
                                onItemClickListener.onClickItem(position, v);
                            }
                        }
                        /*if (onItemClickListener != null) {
                            onItemClickListener.onClickItem(position, v);
                        }
                        if (withSelector && position == imgPaths.size() - 1 && onSelectorClickListener != null) {
                            onSelectorClickListener.onSelectorClick(position, v);
                        }*/
                    }
                });
                addView(imgViewGlide);
                imgViewGlides.add(imgViewGlide);
            }
            imgViewGlides.get(i).setImgSource(imgPaths.get(i));
        }
        invalidate();
    }

    public List<Object> getImgPaths() {
        return imgPaths;
    }

    public void resetAllImgPaths(List<String> imgPaths) {
        if (imgPaths == null) return;
        this.imgPaths.clear();
        this.imgPaths.addAll(imgPaths);
        if (withSelector) {
            this.imgPaths.add(R.mipmap.icon_select_img_padding);
        }
        setImgSource();
    }

    public void addImgPath(Object path, int position) {
        if (withSelector && imgPaths.isEmpty()) {
            imgPaths.add(R.mipmap.icon_select_img_padding);
        }
        if ((path instanceof String) || (path instanceof Integer)) {
            if (withSelector) {
                if (position >= imgPaths.size() - 1) {
                    imgPaths.add(imgPaths.size() - 1, path);
                } else {
                    imgPaths.add(position, path);
                }
            } else {
                if (position >= imgPaths.size()) {
                    imgPaths.add(path);
                } else {
                    imgPaths.add(position, path);
                }
            }
            setImgSource();
        }
    }

    public void addImgPath(Object path) {
        addImgPath(path, imgPaths.size());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnSelectorClickListener(OnSelectorClickListener onSelectorClickListener) {
        this.onSelectorClickListener = onSelectorClickListener;
    }

    public void setWithSelector(boolean withSelector) {
        this.withSelector = withSelector;
        if (withSelector && imgPaths.isEmpty()) {
            imgPaths.add(R.mipmap.icon_select_img_padding);
            setVisibility(VISIBLE);
            setImgSource();
        }
    }

    private void initRowAndColum(int size) {
        if (maxCount == 9) {
            rows = (size - 1) / 3 + 1;
            columns = (size - 1) % 3 + 1;
            if (size == 4) {
                rows = 2;
                columns = 2;
            } else {
                columns = 3;
            }


        } else {
            if (size < 5) {
                rows = 1;
            } else {
                rows = 2;
            }
            columns = 4;
        }
    }

    public interface OnItemClickListener {
        void onClickItem(int position, View view);
    }

    public interface OnSelectorClickListener {
        void onSelectorClick(int position, View view);
    }

}
