package com.app.customview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class AnimNumView extends View {

    protected static final int DEFAULT_DURATION = 350;

    //控件 paddingLeft paddingTop + paint的width
    protected int mLeft, mTop;
    //宽高
    protected int mWidth, mHeight;

    //加减的圆的Path的Region
    protected Region mAddRegion, mDelRegion;
    protected Path mAddPath, mDelPath;

    /**
     * 加按钮
     */
    protected Paint mAddPaint;
    //加按钮是否开启fill模式 默认是stroke(xml)(false)
    protected boolean isAddFillMode;
    //加按钮的背景色前景色(xml)
    protected int mAddEnableBgColor;
    protected int mAddEnableFgColor;
    //加按钮不可用时的背景色前景色(xml)
    protected int mAddDisableBgColor;
    protected int mAddDisableFgColor;

    /**
     * 减按钮
     */
    protected Paint mDelPaint;
    //按钮是否开启fill模式 默认是stroke(xml)(false)
    protected boolean isDelFillMode;
    //按钮的背景色前景色(xml)
    protected int mDelEnableBgColor;
    protected int mDelEnableFgColor;

    //最大数量和当前数量(xml)
    protected int mMaxCount = 99;
    protected int mCount;

    //圆的半径
    protected float mRadius;
    //圆圈的宽度
    protected float mCircleWidth;
    //线的宽度
    protected float mLineWidth;


    /**
     * 两个圆之间的间距(xml)
     */
    protected float mGapBetweenCircle;
    //绘制数量的textSize
    protected float mTextSize;
    protected Paint mTextPaint;
    protected Paint.FontMetrics mFontMetrics;


    //动画的基准值 动画：减 0~1, 加 1~0 ,
    // 普通状态下都显示时是0
    protected ValueAnimator mAnimAdd, mAniDel;
    protected float mAnimFraction;

    protected int mPerAnimDuration = DEFAULT_DURATION;

    //点击回调
    protected OnAddMinusListener onAddMinusListener;

    public AnimNumView setOnAddMinusListener(OnAddMinusListener onAddMinusListener) {
        this.onAddMinusListener = onAddMinusListener;
        return this;
    }

    public AnimNumView(Context context) {
        super(context);
    }

    public AnimNumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {

        initDefaultValue(context);

        mAddRegion = new Region();
        mDelRegion = new Region();
        mAddPath = new Path();
        mDelPath = new Path();

        mAddPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAddPaint.setStyle(Paint.Style.FILL);
        mDelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDelPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mFontMetrics = mTextPaint.getFontMetrics();


        //动画 +
        mAnimAdd = ValueAnimator.ofFloat(1, 0);
        mAnimAdd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimAdd.setDuration(mPerAnimDuration);

        //动画 +
        mAnimAdd = ValueAnimator.ofFloat(1, 0);
        mAnimAdd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimAdd.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        mAnimAdd.setDuration(mPerAnimDuration);

        //动画 -
        mAniDel = ValueAnimator.ofFloat(0, 1);
        mAniDel.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAniDel.setDuration(mPerAnimDuration);
    }

    public void initDefaultValue(Context context) {
        mGapBetweenCircle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, context.getResources().getDisplayMetrics());

        // 加号
        mAddEnableBgColor = Color.parseColor("#3190E8");
        mAddEnableFgColor = Color.parseColor("#ffffff");

        // 减号
        mDelEnableBgColor = Color.parseColor("#3190E8");
        mDelEnableFgColor = Color.parseColor("#3190E8");

        // 圆
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12.5f, getResources().getDisplayMetrics());
        mCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics());
        mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14.5f, getResources().getDisplayMetrics());

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLeft = (int) (getPaddingLeft() + mCircleWidth);
        mTop = (int) (getPaddingTop() + mCircleWidth);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (wMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                //不超过父控件给的范围内，自由发挥
                int computeSize = (int) (getPaddingLeft() + mRadius * 2 +/* mGap * 2 + mTextPaint.measureText(mCount + "")*/mGapBetweenCircle + mRadius * 2 + getPaddingRight() + mCircleWidth * 2);
                wSize = computeSize < wSize ? computeSize : wSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                //自由发挥
                computeSize = (int) (getPaddingLeft() + mRadius * 2 + /*mGap * 2 + mTextPaint.measureText(mCount + "")*/mGapBetweenCircle + mRadius * 2 + getPaddingRight() + mCircleWidth * 2);
                wSize = computeSize;
                break;
        }
        switch (hMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                int computeSize = (int) (getPaddingTop() + mRadius * 2 + getPaddingBottom() + mCircleWidth * 2);
                hSize = computeSize < hSize ? computeSize : hSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                computeSize = (int) (getPaddingTop() + mRadius * 2 + getPaddingBottom() + mCircleWidth * 2);
                hSize = computeSize;
                break;
        }


        setMeasuredDimension(wSize, hSize);

        //先暂停所有动画
        cancelAllAnim();
        //复用时会走这里，所以初始化一些UI显示的参数
        initAnimSettingsByCount();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //动画 mAnimFraction ：减 0~1, 加 1~0 ,
        //动画位移Max,
        float animOffsetMax = (mRadius * 2 + /*mGap * 2 + mTextPaint.measureText(mCount + "")*/mGapBetweenCircle);
        //透明度动画的基准
        int animAlphaMax = 255;
        int animRotateMax = 360;

        //左边
        //背景 圆
        mDelPaint.setColor(mDelEnableBgColor);
        mDelPaint.setAlpha((int) (animAlphaMax * (1 - mAnimFraction)));
        mDelPaint.setStrokeWidth(mCircleWidth);
        mDelPath.reset();
        //改变圆心的X坐标，实现位移
        mDelPath.addCircle(animOffsetMax * mAnimFraction + mLeft + mRadius, mTop + mRadius, mRadius, Path.Direction.CW);
        mDelRegion.setPath(mDelPath, new Region(mLeft, mTop, mWidth - getPaddingRight(), mHeight - getPaddingBottom()));
        //canvas.drawCircle(mAnimOffset + mLeft + mRadius, mTop + mRadius, mRadius, mPaint);
        canvas.drawPath(mDelPath, mDelPaint);

        mDelPaint.setStrokeWidth(mLineWidth);
        //旋转动画
        canvas.save();
        canvas.translate(animOffsetMax * mAnimFraction + mLeft + mRadius, mTop + mRadius);
        canvas.rotate((int) (animRotateMax * (1 - mAnimFraction)));
        canvas.drawLine(-mRadius / 2, 0,
                +mRadius / 2, 0,
                mDelPaint);
        canvas.restore();

        //数量
        canvas.save();
        //平移动画
        //旋转动画,旋转中心点，x 是绘图中心,y 是控件中心
        canvas.rotate(360 * mAnimFraction,
                /*mGap*/ mGapBetweenCircle / 2 + mLeft + mRadius * 2,
                mTop + mRadius);
        //透明度动画
        mTextPaint.setAlpha((int) (255 * (1 - mAnimFraction)));
        //是没有动画的普通写法,x left, y baseLine
        canvas.drawText(mCount + "", mGapBetweenCircle / 2 - mTextPaint.measureText(mCount + "") / 2 + mLeft + mRadius * 2, mTop + mRadius - (mFontMetrics.top + mFontMetrics.bottom) / 2, mTextPaint);
        canvas.restore();

        //右边
        //背景 圆
        mAddPaint.setColor(mAddEnableBgColor);
        mAddPaint.setStrokeWidth(mCircleWidth);
        float left = mLeft + mRadius * 2 + mGapBetweenCircle;
        mAddPath.reset();
        mAddPath.addCircle(left + mRadius, mTop + mRadius, mRadius, Path.Direction.CW);
        mAddRegion.setPath(mAddPath, new Region(mLeft, mTop, mWidth - getPaddingRight(), mHeight - getPaddingBottom()));
        canvas.drawPath(mAddPath, mAddPaint);
        //圆心
        mAddPaint.setColor(mAddEnableFgColor);
        mAddPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(left + mRadius / 2, mTop + mRadius, left + mRadius / 2 + mRadius, mTop + mRadius, mAddPaint);
        canvas.drawLine(left + mRadius, mTop + mRadius / 2, left + mRadius, mTop + mRadius / 2 + mRadius, mAddPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mAddRegion.contains((int) event.getX(), (int) event.getY())) {
                    onAddClick();
                    return true;
                } else if (mDelRegion.contains((int) event.getX(), (int) event.getY())) {
                    onMinusClick();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initAnimSettingsByCount() {
        if (mCount == 0) {
            // 0 不显示 数字和-号
            mAnimFraction = 1;
        } else {
            mAnimFraction = 0;
        }
    }


    /**
     * 暂停所有动画
     */
    private void cancelAllAnim() {
        if (mAnimAdd != null && mAnimAdd.isRunning()) {
            mAnimAdd.cancel();
        }
        if (mAniDel != null && mAniDel.isRunning()) {
            mAniDel.cancel();
        }
    }

    public void onAddClick() {
        if (mCount < mMaxCount) {
            mCount++;
            onAddSuccess();
            if (null != onAddMinusListener) {
                onAddMinusListener.onAddSuccess(mCount);
            }
        } else {
            if (null != onAddMinusListener) {
                onAddMinusListener.onMinusFailed(mCount, OnAddMinusListener.FailType.COUNT_MIN);
            }
        }
    }

    public void onMinusClick() {
        if (mCount > 0) {
            mCount--;
            onMinusSuccess();
            if (null != onAddMinusListener) {
                onAddMinusListener.onMinusSuccess(mCount);
            }
        } else {
            if (null != onAddMinusListener) {
                onAddMinusListener.onMinusFailed(mCount, OnAddMinusListener.FailType.COUNT_MIN);
            }
        }
    }

    public void onAddSuccess() {
        if (mCount == 1) {
            cancelAllAnim();
            mAnimAdd.start();
        } else {
            mAnimFraction = 0;
            invalidate();
        }
    }

    public void onMinusSuccess() {
        if (mCount == 0) {
            cancelAllAnim();
            mAniDel.start();
        } else {
            mAnimFraction = 0;
            invalidate();
        }
    }

    interface OnAddMinusListener {
        enum FailType {
            COUNT_MAX, COUNT_MIN
        }

        void onAddSuccess(int count);

        void onAddFailed(int count, FailType failTyle);

        void onMinusSuccess(int count);

        void onMinusFailed(int count, FailType failTyle);
    }
}