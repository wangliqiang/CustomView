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
import android.view.View;
import android.view.ViewGroup;

import com.app.customview.R;

import static android.view.View.MeasureSpec.AT_MOST;

public class ClockView extends View {

    private final static int DEFAULT_HEAD_COLOR = Color.parseColor("#6B2831");
    private final static int DEFAULT_EDGE_COLOR = Color.parseColor("#83CBB3");
    private final static int DEFAULT_EAR_AND_FOOT_COLOR = Color.parseColor("#f57a22");
    private final static int DEFAULT_SCALE_AND_HANDS_COLOR = Color.BLACK;
    private final static int DEFAULT_SECOND_HAND_COLOR = Color.RED;

    private final static double ARC_LENGTH = Math.PI * 2;
    private final static double SCALE_ARC_LENGTH_OFFSET = ARC_LENGTH / 12d;

    private final static double CLOCK_HOUR_HAND_ARC_LENGTH = SCALE_ARC_LENGTH_OFFSET;
    private static final double CLOCK_MINUTE_HAND_ARC_LENGTH = ARC_LENGTH / 60d;
    private static final double CLOCK_SECOND_HAND_ARC_LENGTH = CLOCK_MINUTE_HAND_ARC_LENGTH;


    private static final double CLOCK_START_ARC = 90 * Math.PI / 180d;

    private Paint mClockCirclePaint, mEarPaint, mCenterCirclePaint, mScalePaint,
            mHeadLinePaint, mFootPaint, mHourHandPaint, mMinHandPaint, mSecondHandPaint;

    private float mCenterX, mCenterY, mClockRadius, mCenterCircleRadius, mClockStrokeWidth, mEarLeftAngel, mEarRightAngel, mHeadCircleRadius,
            mFootRightAngel, mFootLeftAngel, mFootCalculateRadius;

    private int mEdgeColor = DEFAULT_EDGE_COLOR;
    private int mEarColor = DEFAULT_EAR_AND_FOOT_COLOR;
    private int mCenterPointColor = DEFAULT_EAR_AND_FOOT_COLOR;
    private int mScaleColor = DEFAULT_SCALE_AND_HANDS_COLOR;
    private int mHeadColor = DEFAULT_HEAD_COLOR;
    private int mFootColor = DEFAULT_EAR_AND_FOOT_COLOR;
    private int mHourHandColor = DEFAULT_SCALE_AND_HANDS_COLOR;
    private int mMinuteHandColor = DEFAULT_SCALE_AND_HANDS_COLOR;
    private int mSecondHandColor = DEFAULT_SECOND_HAND_COLOR;

    private float mStartScaleLength;
    private float mStopScaleLength;
    private double mScaleStartX, mScaleStartY, mScaleStopX, mScaleStopY;

    private float mStopHeadLength;

    private RectF mEarRectF;
    private Path mEarPath, mEarDstPath;
    private Matrix mMatrix;

    private PointF mEarRightEndPointF, mEarLeftEndPointF;

    private PointF mFootRightStartPointF, mFootRightEndPointF,
            mFootLeftStartPointF, mFootLeftEndPointF;

    private int mHour, mMinute, mSecond;

    private float mStopHourHandLength, mStopMinHandLength, mStopSecondHandLength;

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

        mHeadLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeadLinePaint.setStyle(Paint.Style.STROKE);
        mHeadLinePaint.setColor(mHeadColor);

        mFootPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFootPaint.setStyle(Paint.Style.STROKE);
        mFootPaint.setStrokeCap(Paint.Cap.ROUND);
        mFootPaint.setStrokeJoin(Paint.Join.ROUND);
        mFootPaint.setColor(mFootColor);

        mHourHandPaint = new Paint();
        mHourHandPaint.setStrokeCap(Paint.Cap.ROUND);
        mHourHandPaint.setStrokeJoin(Paint.Join.ROUND);
        mHourHandPaint.setAntiAlias(true);
        mHourHandPaint.setColor(mHourHandColor);

        mMinHandPaint = new Paint();
        mMinHandPaint.setStrokeCap(Paint.Cap.ROUND);
        mMinHandPaint.setStrokeJoin(Paint.Join.ROUND);
        mMinHandPaint.setAntiAlias(true);
        mMinHandPaint.setColor(mMinuteHandColor);

        mSecondHandPaint = new Paint();
        mSecondHandPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecondHandPaint.setStrokeJoin(Paint.Join.ROUND);
        mSecondHandPaint.setAntiAlias(true);
        mSecondHandPaint.setColor(mSecondHandColor);


        mEarRectF = new RectF();
        mEarPath = new Path();
        mEarDstPath = new Path();
        mMatrix = new Matrix();

        mEarRightEndPointF = new PointF();
        mEarLeftEndPointF = new PointF();

        mEarRightAngel = (float) (300 * Math.PI / 180);
        mEarLeftAngel = (float) (240 * Math.PI / 180);


        mFootRightStartPointF = new PointF();
        mFootRightEndPointF = new PointF();
        mFootLeftStartPointF = new PointF();
        mFootLeftEndPointF = new PointF();

        mFootRightAngel = (float) (60 * Math.PI / 180);
        mFootLeftAngel = (float) (120 * Math.PI / 180);

    }

    public void parseAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ClockView);
        mEdgeColor = typedArray.getColor(R.styleable.ClockView_edge_color, DEFAULT_EDGE_COLOR);
        mEarColor = typedArray.getColor(R.styleable.ClockView_ear_color, DEFAULT_EAR_AND_FOOT_COLOR);
        mHeadColor = typedArray.getColor(R.styleable.ClockView_head_color, DEFAULT_HEAD_COLOR);
        mFootColor = typedArray.getColor(R.styleable.ClockView_ear_color, DEFAULT_EAR_AND_FOOT_COLOR);
        mHourHandColor = typedArray.getColor(R.styleable.ClockView_hour_hand_color, DEFAULT_SCALE_AND_HANDS_COLOR);
        mMinuteHandColor = typedArray.getColor(R.styleable.ClockView_minute_hand_color, DEFAULT_SCALE_AND_HANDS_COLOR);
        mSecondHandColor = typedArray.getColor(R.styleable.ClockView_second_hand_color, DEFAULT_SECOND_HAND_COLOR);
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

        mStopHeadLength = mCenterY - mClockRadius - mClockStrokeWidth * 1.5f;

        mHeadLinePaint.setStrokeWidth(mClockStrokeWidth * 2 / 3f);
        mHeadCircleRadius = mClockStrokeWidth / 4f;

        mStartScaleLength = mClockRadius * 5.2f / 7f;
        mStopScaleLength = mClockRadius * 5.6f / 7f;

        mEarRectF.set(mCenterX - mClockRadius / 3, mCenterY - mClockRadius / 3,
                mCenterX + mClockRadius / 3, mCenterY + mClockRadius / 3);
        mEarPath.reset();
        mEarPath.addArc(mEarRectF, 180, 180);

        mFootCalculateRadius = mClockRadius + (minSize - mClockRadius) / 4f;
        mFootPaint.setStrokeWidth(mClockStrokeWidth * 2 / 3f);

        mHourHandPaint.setStrokeWidth(mClockStrokeWidth / 2f);

        mStopHourHandLength = mClockRadius * 3.2f / 7f;
        mStopMinHandLength = mClockRadius * 4.2f / 7f;
        mStopSecondHandLength = mClockRadius * 4.2f / 7f;

        mMinHandPaint.setStrokeWidth(mClockStrokeWidth / 3f);

        mSecondHandPaint.setStrokeWidth(mClockStrokeWidth / 5f);


        initCoordinates();
    }

    private void initCoordinates() {

        /* Ear */
        float earRightEndX = (float) ((mClockRadius + mClockStrokeWidth) * Math.cos(mEarRightAngel) + mCenterX);
        float earRightEndY = (float) ((mClockRadius + mClockStrokeWidth) * Math.sin(mEarRightAngel) + mCenterY);
        mEarRightEndPointF.set(earRightEndX, earRightEndY);

        float earLeftEndX = (float) ((mClockRadius + mClockStrokeWidth) * Math.cos(mEarLeftAngel) + mCenterX);
        float earLeftEndY = (float) ((mClockRadius + mClockStrokeWidth) * Math.sin(mEarLeftAngel) + mCenterY);
        mEarLeftEndPointF.set(earLeftEndX, earLeftEndY);


        /* Foot */
        float footRightStartX = (float) ((mClockRadius + mClockStrokeWidth / 2) * Math.cos(mFootRightAngel) + mCenterX);
        float footRightStartY = (float) ((mClockRadius + mClockStrokeWidth / 2) * Math.sin(mFootRightAngel) + mCenterY);
        mFootRightStartPointF.set(footRightStartX, footRightStartY);

        float footRightEndX = (float) (mFootCalculateRadius * Math.cos(mFootRightAngel) + mCenterX);
        float footRightEndY = (float) (mFootCalculateRadius * Math.sin(mFootRightAngel) + mCenterY);
        mFootRightEndPointF.set(footRightEndX, footRightEndY);

        float footLeftStartX = (float) ((mClockRadius + mClockStrokeWidth / 2) * Math.cos(mFootLeftAngel) + mCenterX);
        float footLeftStartY = (float) ((mClockRadius + mClockStrokeWidth / 2) * Math.sin(mFootLeftAngel) + mCenterY);
        mFootLeftStartPointF.set(footLeftStartX, footLeftStartY);

        float footLeftEndX = (float) (mFootCalculateRadius * Math.cos(mFootLeftAngel) + mCenterX);
        float footLeftEndY = (float) (mFootCalculateRadius * Math.sin(mFootLeftAngel) + mCenterY);
        mFootLeftEndPointF.set(footLeftEndX, footLeftEndY);

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

        drawHead(canvas);
        drawFoot(canvas);
        drawEars(canvas);
        drawClock(canvas);
    }

    private void drawFoot(Canvas canvas) {
        canvas.drawLine(mFootRightStartPointF.x, mFootRightStartPointF.y, mFootRightEndPointF.x, mFootRightEndPointF.y, mFootPaint);
        canvas.drawLine(mFootLeftStartPointF.x, mFootLeftStartPointF.y, mFootLeftEndPointF.x, mFootLeftEndPointF.y, mFootPaint);
    }

    private void drawHead(Canvas canvas) {
        canvas.drawLine(mCenterX, mCenterY - mClockRadius, mCenterX, mStopHeadLength, mHeadLinePaint);

        canvas.drawCircle(mCenterX, mStopHeadLength, mHeadCircleRadius, mHeadLinePaint);
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

        drawHour(canvas);
        drawMinute(canvas);
        drawSecond(canvas);

        // 圆心
        canvas.drawCircle(mCenterX, mCenterY, mCenterCircleRadius, mCenterCirclePaint);
    }

    private void drawSecond(Canvas canvas) {
        float angle = (float) (mSecond * CLOCK_SECOND_HAND_ARC_LENGTH - CLOCK_START_ARC);

        float secondEndX = (float) (mStopSecondHandLength * Math.cos(angle) + mCenterX);
        float secondEndY = (float) (mStopSecondHandLength * Math.sin(angle) + mCenterY);

        canvas.drawLine(mCenterX, mCenterY,secondEndX, secondEndY, mSecondHandPaint);
    }

    private void drawMinute(Canvas canvas) {
        float angle = (float) (mMinute * CLOCK_MINUTE_HAND_ARC_LENGTH - CLOCK_START_ARC);

        float minEndX = (float) (mStopMinHandLength * Math.cos(angle) + mCenterX);
        float minEndY = (float) (mStopMinHandLength * Math.sin(angle) + mCenterY);

        canvas.drawLine(mCenterX, mCenterY, minEndX, minEndY, mMinHandPaint);
    }

    private void drawHour(Canvas canvas) {
        float angle = (float) ((mHour % 12 + mMinute / 60f) * CLOCK_HOUR_HAND_ARC_LENGTH - CLOCK_START_ARC);
        float hourEndX = (float) (mStopHourHandLength * Math.cos(angle) + mCenterX);
        float hourEndY = (float) (mStopHourHandLength * Math.sin(angle) + mCenterY);
        canvas.drawLine(mCenterX, mCenterY, hourEndX, hourEndY, mHourHandPaint);
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

    private void checkTime(){
        if (mHour < 0){
            mHour = 0;
        }

        if (mHour > 24){
            mHour = 24;
        }

        if (mMinute < 0){
            mMinute = 0;
        }

        if (mMinute > 60){
            mMinute = 60;
        }

        if (mSecond < 0){
            mSecond = 0;
        }

        if (mSecond > 60){
            mSecond = 60;
        }
    }

    public void setTime(int hour,int minute,int second){
        this.mHour = hour;
        this.mMinute = minute;
        this.mSecond = second;
        checkTime();
        postInvalidate();
    }
}
