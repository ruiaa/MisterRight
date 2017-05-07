package com.misterright.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.misterright.R;

/**
 * Created by ruiaa on 2016/11/15.
 */

public class InviteFriendCircleView extends View {

    private float radius=0;
    private float radius_1=0; //最外圈
    private float thickness_1=0;
    private Paint paint_1;
    private float radius_2=0; //中间圈
    private float thickness_2=0;
    private Paint paint_2;
    private float radius_3=0; //内圈
    private float thickness_3=0;
    private Paint paint_3;

    public InviteFriendCircleView(Context context) {
        super(context);
    }

    public InviteFriendCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.InviteFriendCircleView);
        radius=a.getDimensionPixelSize(R.styleable.InviteFriendCircleView_invite_radius, 0);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        radius=Math.min(radius,Math.min(MeasureSpec.getSize(widthMeasureSpec)/2,MeasureSpec.getSize(heightMeasureSpec)/2));
        setMeasuredDimension((int)radius*2,(int)radius*2);
    }

    private void setAllDimen(){
        //直径 dp 233 (24) 186 (18) 152(4)
        //半径233px 厚度 46 35 6
        if (radius!=0){
            //厚度
            thickness_1=radius*(46/233);
            thickness_2=radius*(35/233);
            thickness_3=radius*(6/233);
            //中间径
            radius_1=radius-thickness_1/2;
            radius_2=radius-thickness_1-thickness_2/2;
            radius_3=radius-thickness_1-thickness_2-thickness_3/2;
        }
    }

    private void initPaint(){

    }
}
