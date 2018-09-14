package com.app.customview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.app.customview.R;

import java.util.logging.Logger;

import static android.view.View.MeasureSpec.AT_MOST;

public class ClockView extends View {

    private final static int DEFAULT_EDGE_COLOR = Color.parseColor("#83CBB3");
    private final static int DEFAULT_EAR_AND_FOOT_COLOR = Color.parseColor("#f57a22");
    private final static int DEFAULT_SCALE_AND_HANDS_COLOR = Color.BLACK;

    private final static double ARC_LENGTH = Math.PI * 2;
    private final static double SCALE_ARC_LENGTH_OFFSET = ARC_LENGTH / 12d;

    private Paint mClockCirclePaint, mEarPaint, mCenterCirclePaint, mScalePaint;

    private float mCenterX, mCenterY, mClockRadius, mCenterCircleRadius, mClockStrokeWidth, mEarLeftAngel, mEarRightAngel;

    private int mEdgeColor = DEFAULT_EDGE_COLOR;
    private int mEarColor = DEFAULT_EAR_AND_FOOT_COLOR;
    private int mCenterPointColor = DEFAULT_EAR_AND_FOOT_COLOR;
    private int mScaleColor = DEFAULT_SCALE_AND_HANDS_COLOR;

    private float mStartScaleLength;
    private float mStopScaleLength;
    private double mScaleStartX, mScaleStartY, mScaleStopX, mScaleStopY;

    private RectF mEarRectF;
    private Path mEarPath, mEarDstPath;
    private Matrix mMatrix;

    private PointF mEarRightEndPointF, mEarLeftEndPointF;

    public ClockView(Context context) {
        super(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(attrs);
        init();

    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        parseAttrs(attrs);

        init();
    }

    private void init() {
        mClockCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClockCirclePaint.setStyle(Paint.Style.STROKE);
        mClockCirclePaint.setColor(mEdgeColor);

        mEarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEarPaint.setStyle(Paint.Style.FILL);
        mEarPaint.setColor(mEarColor);

        mCenterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterCirclePaint.setStyle(Paint.Style.FILL);
        mCenterCirclePaint.setColor(mCenterPointColor);

        mScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScalePaint.setStyle(Paint.Style.STROKE);
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setStrokeJoin(Paint.Join.ROUND);
        mScalePaint.setColor(mScaleColor);


        mEarRectF = new RectF();
        mEarPath = new Path();
        mEarDstPath = new Path();
        mMatrix = new Matrix();

        mEarRightEndPointF = new PointF();
        mEarLeftEndPointF = new PointF();

        mEarRightAngel = (float) (300 * Math.PI / 180);
        mEarLeftAngel = (float) (240 * Math.PI / 180);
    }

    public void parseAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ClockView);
        mEdgeColor = typedArray.getColor(R.styleable.ClockView_edge_color, DEFAULT_EDGE_COLOR);
        mEarColor = typedArray.getColor(R.styleable.ClockView_ear_color, DEFAULT_EAR_AND_FOOT_COLOR);

        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX = w / 2;
        mCenterY = h / 2;

        int minSize = Math.min(w, h);

        mClockRadius = minSize / 3f;

        mClockStrokeWidth = mClockRadius / 7f;
        mClockCirclePaint.setStrokeWidth(mClockStrokeWidth);

        mScalePaint.setStrokeWidth(mClockStrokeWidth / 3f);

        mCenterCircleRadius = mClockStrokeWidth / 2f;

        mStartScaleLength = mClockRadius * 5.2f / 7f;
        mStopScaleLength = mClockRadius * 5.6f / 7f;

        mEarRectF.set(mCenterX - mClockRadius / 3, mCenterY - mClockRadius / 3,
                mCenterX + mClockRadius / 3, mCenterY + mClockRadius / 3);
        mEarPath.reset();
        mEarPath.addArc(mEarRectF, 180, 180);


        initCoordinates();
    }

    private void initCoordinates() {
        float earRightEndX = (float) ((mClockRadius + mClockStrokeWidth) * Math.cos(mEarRightAngel) + mCenterX);
        float earRightEndY = (float) ((mClockRadius + mClockStrokeWidth) * Math.sin(mEarRightAngel) + mCenterY);
        mEarRightEndPointF.set(earRightEndX, earRightEndY);

        float earLeftEndX = (float) ((mClockRadius + mClockStrokeWidth) * Math.cos(mEarLeftAngel) + mCenterX);
        float earLeftEndY = (float) ((mClockRadius + mClockStrokeWidth) * Math.sin(mEarLeftAngel) + mCenterY);
        mEarLeftEndPointF.set(earLeftEndX, earLeftEndY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        if (modeWidth == AT_MOST && modeHeight == AT_MOST) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawClock(canvas);
        drawEars(canvas);
    }

    private void drawClock(Canvas canvas) {
        // 圆环
        canvas.drawCircle(mCenterX, mCenterY, mClockRadius, mClockCirclePaint);
        for (float i = 0; i < ARC_LENGTH; i += SCALE_ARC_LENGTH_OFFSET) {
            mScaleStartX = mStartScaleLength * Math.cos(i) + mCenterX;
            mScaleStartY = mStartScaleLength * Math.sin(i) + mCenterY;

            mScaleStopX = mStopScaleLength * Math.cos(i) + mCenterX;
            mScaleStopY = mStopScaleLength * Math.sin(i) + mCenterY;

            canvas.drawLine((float) mScaleStartX, (float) mScaleStartY, (float) mScaleStopX, (float) mScaleStopY, mScalePaint);
        }

        // 圆心
        canvas.drawCircle(mCenterX, mCenterY, mCenterCircleRadius, mCenterCirclePaint);
    }

    private void drawEars(Canvas canvas) {
        mMatrix.reset();
        mEarDstPath.reset();

        mMatrix.preRotate(30, mCenterX, mCenterY);
        mMatrix.postTranslate(mEarRightEndPointF.x - mCenterX, mEarRightEndPointF.y - mCenterY);
        mEarPath.transform(mMatrix, mEarDstPath);
        canvas.drawPath(mEarDstPath, mEarPaint);

        mMatrix.reset();
        mEarDstPath.reset();

        mMatrix.preRotate(-30, mCenterX, mCenterY);
        mMatrix.postTranslate(mEarLeftEndPointF.x - mCenterX, mEarLeftEndPointF.y - mCenterY);
        mEarPath.transform(mMatrix, mEarDstPath);
        canvas.drawPath(mEarDstPath, mEarPaint);
    }
}
