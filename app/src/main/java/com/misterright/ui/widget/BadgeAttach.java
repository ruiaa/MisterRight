package com.misterright.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.misterright.util.ConvertUtil;

/**
 *  Created by Allen Liu on 2016/7/15.
 *  https://github.com/AlexLiuSheng/BadgeView
 */

public class BadgeAttach extends View {

    /**
     * 数字paint
     */
    private Paint numberPaint;
    /**
     * 背景Paint
     */
    private Paint backgroundPaint;
    public static final int SHAPE_CIRCLE=1;
    public static final int SHAPE_RECTANGLE=2;
    public static final int SHAPE_OVAL=3;
    public static final int SHAPTE_ROUND_RECTANGLE=4;
    public static final int SHAPE_SQUARE=5;
    private  int currentShape=SHAPE_CIRCLE;
    private int defaultTextColor = Color.WHITE;
    private int defaultTextSize;
    private int defaultBackgroundColor = Color.RED;
    private String showText = "";
    private int badgeGravity = Gravity.RIGHT | Gravity.TOP;
    private int leftMargin=0;
    private int topMargin=0;
    private int bottomMargin=0;
    private int rightMargin=0;
    private boolean isBind=false;

    private int topOffset=0;
    private int bottomOffset=0;
    private int rightOffset=0;
    private int leftOffset=0;


    public BadgeAttach(Context context) {
        super(context);
        init(context);
    }

    public BadgeAttach(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BadgeAttach(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        defaultTextSize = dip2px(context, 1);
        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(defaultTextColor);
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setTextSize(defaultTextSize);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(defaultBackgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = badgeGravity;
        setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //  int w = MeasureSpec.getSize(widthMeasureSpec);
        //   int h = MeasureSpec.getSize(heightMeasureSpec);
        // int size = w > h ? h : w;
        // int measure = MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST);
        // super.onMeasure(measure, measure);
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF=new RectF(0,0,getMeasuredWidth(),getMeasuredHeight());
        Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
        float textH = fontMetrics.descent - fontMetrics.ascent;
        switch(currentShape){
            case SHAPE_CIRCLE:
                canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, getMeasuredWidth() / 2, backgroundPaint);
                canvas.drawText(showText, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), numberPaint);
                break;
            case SHAPE_OVAL:

                canvas.drawOval(rectF,backgroundPaint);
                canvas.drawText(showText, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), numberPaint);
                break;
            case SHAPE_RECTANGLE:
                canvas.drawRect(rectF,backgroundPaint);
                canvas.drawText(showText, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), numberPaint);
                break;
            case SHAPE_SQUARE:
                int sideLength= Math.min(getMeasuredHeight(),getMeasuredWidth());
                RectF squareF=new RectF(0,0,sideLength,sideLength);
                canvas.drawRect(squareF,backgroundPaint);
                canvas.drawText(showText, sideLength/ 2f, sideLength / 2f + (textH / 2f - fontMetrics.descent), numberPaint);
                break;
            case SHAPTE_ROUND_RECTANGLE:
                canvas.drawRoundRect(rectF,dip2px(getContext(),5),dip2px(getContext(),5),backgroundPaint);
                canvas.drawText(showText, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), numberPaint);
                break;
        }

    }

    private int dip2px(Context context, int dip) {
        return ConvertUtil.dp2px(dip);
    }
    private  int sp2px(Context context, float spValue) {
        return ConvertUtil.sp2px(spValue);
    }

    public BadgeAttach setShape(int shape){
        currentShape=shape;
        invalidate();
        return this;
    }
    /**
     * @param w dip
     * @param h dip this unit is dip
     * @return
     */
    public BadgeAttach setWidthAndHeight(int w, int h) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.width = dip2px(getContext(), w);
        params.height = dip2px(getContext(), h);
        setLayoutParams(params);
        return this;
    }

    /**
     *
     * @param sp dip
     * @return
     */
    public BadgeAttach setWidth(int sp) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.width = dip2px(getContext(), sp);
        setLayoutParams(params);
        return this;

    }

    public BadgeAttach setHeight(int sp) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.height = dip2px(getContext(), sp);
        setLayoutParams(params);
        return this;
    }

    /**
     *
     * @param sp  the unit is sp
     * @return
     */
    public BadgeAttach setTextSize(int sp) {
        defaultTextSize = sp2px(getContext(), sp);
        numberPaint.setTextSize(sp2px(getContext(), sp));
        invalidate();
        return this;
    }

    public BadgeAttach setTextColor(int color) {
        defaultTextColor = color;
        numberPaint.setColor(color);
        invalidate();
        return this;
    }

    public BadgeAttach setBadgeBackground(int color) {
        defaultBackgroundColor = color;
        backgroundPaint.setColor(color);
        invalidate();
        return this;
    }

    /**
     * if shape is dot ,func is not effect
     * @param count
     * @return
     */
    public BadgeAttach setBadgeCount(int count) {
        showText = String.valueOf(count);
        invalidate();
        return this;
    }

    /**
     * if shape is dot ,func is not effect
     * @param count
     * @return
     */
    public BadgeAttach setBadgeCount(String count) {
        showText = count;
        invalidate();
        return this;
    }
    /**
     * set bindview margin that you can change badges positon,
     * but if you set margin ,the width of view or height of view will be changed
     * @param left  the unit is dip
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public BadgeAttach setMargin(int left, int top, int right, int bottom){
        leftMargin=dip2px(getContext(),left);
        bottomMargin=dip2px(getContext(),bottom);
        topMargin=dip2px(getContext(),top);
        rightMargin=dip2px(getContext(),right);
        return  this;
    }
    /**
     * set gravity must be before @link bind() method
     *
     * @param gravity
     * @return
     */
    public BadgeAttach setBadgeGravity(int gravity) {
        badgeGravity = gravity;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.gravity = gravity;
        setLayoutParams(params);
        return this;
    }

    public BadgeAttach setOffset(int left, int top, int right, int bottom){
        leftOffset=dip2px(getContext(),left);
        topOffset=dip2px(getContext(),top);
        rightOffset=dip2px(getContext(),right);
        bottomOffset=dip2px(getContext(),bottom);
        return this;
    }

    public BadgeAttach bind(View view) {
        if (getParent() != null)
            ((ViewGroup) getParent()).removeView(this);
        if (view == null)
            return this;
        if (view.getParent() instanceof FrameLayout&&isBind==true) {
            ((FrameLayout) view.getParent()).addView(this);
        } else if (view.getParent() instanceof ViewGroup) {
            ViewGroup parentContainer = (ViewGroup) view.getParent();
            int viewIndex = ((ViewGroup) view.getParent()).indexOfChild(view);
            ((ViewGroup) view.getParent()).removeView(view);
            FrameLayout container = new FrameLayout(getContext());
            ViewGroup.LayoutParams containerParams = view.getLayoutParams();

            int beforeWidth=containerParams.width;
            int beforeHeight=containerParams.height;
            containerParams.width=containerParams.width+leftMargin+rightMargin;
            containerParams.height=containerParams.height+topMargin+bottomMargin;
            container.setLayoutParams(containerParams);
            container.setId(view.getId());
            FrameLayout.LayoutParams newViewParams=new FrameLayout.LayoutParams(beforeWidth, beforeHeight);
            newViewParams.leftMargin=leftMargin;
            newViewParams.topMargin=topMargin;
            newViewParams.rightMargin=rightMargin;
            newViewParams.bottomMargin=bottomMargin;
            view.setLayoutParams(newViewParams);
            container.addView(view);
            container.addView(this);
            parentContainer.addView(container, viewIndex);
            isBind=true;
        } else if (view.getParent() == null) {
            Log.e("badgeview", "View must have a parent");
        }
        return this;
    }

    public boolean unbind() {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
            return true;
        }
        return false;
    }

    public BadgeAttach  show(){
        this.setVisibility(VISIBLE);
        return this;
    }

    public BadgeAttach  hide(){
        this.setVisibility(INVISIBLE);
        return this;
    }

    public String getBadgeCount() {
        return showText;
    }


    public static BadgeAttach createDot(Context context){
        return  new BadgeAttach(context).setWidthAndHeight(10,10).setTextSize(0).setBadgeGravity(Gravity.RIGHT| Gravity.TOP).setShape(BadgeAttach.SHAPE_CIRCLE);
    }
    public static BadgeAttach createCircle(Context context){
        return  new BadgeAttach(context).setWidthAndHeight(14,14).setTextSize(12).setBadgeGravity(Gravity.RIGHT| Gravity.TOP).setShape(BadgeAttach.SHAPE_CIRCLE);
    }
    public static BadgeAttach createRectangle(Context context){
        return  new BadgeAttach(context).setWidthAndHeight(25,20).setTextSize(12).setBadgeGravity(Gravity.RIGHT| Gravity.TOP).setShape(BadgeAttach.SHAPE_RECTANGLE);
    }
    public static BadgeAttach createOval(Context context){
        return  new BadgeAttach(context).setWidthAndHeight(25,20).setTextSize(12).setBadgeGravity(Gravity.RIGHT| Gravity.TOP).setShape(BadgeAttach.SHAPE_OVAL);
    }
    public static BadgeAttach createSquare(Context context){
        return  new BadgeAttach(context).setWidthAndHeight(20,20).setTextSize(12).setBadgeGravity(Gravity.RIGHT| Gravity.TOP).setShape(BadgeAttach.SHAPE_SQUARE);
    }
    public static BadgeAttach createRoundRect(Context context){
        return  new BadgeAttach(context).setWidthAndHeight(25,20).setTextSize(12).setBadgeGravity(Gravity.RIGHT| Gravity.TOP).setShape(BadgeAttach.SHAPTE_ROUND_RECTANGLE);
    }
    public static BadgeAttach create(Context context){
        return  new BadgeAttach(context);
    }

}
