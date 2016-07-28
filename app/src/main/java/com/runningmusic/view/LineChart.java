package com.runningmusic.view;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.location.Location;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.androidquery.util.AQUtility;
import com.runningmusic.data.Date;
import com.runningmusic.runninspire.Messages;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Util;

public class LineChart extends View implements AnimatorUpdateListener {
    private static String TAG = LineChart.class.getName();

    private class LineCharMinuteSH {
        private int minuteNum = 0;

        public int getMinuteNum() {
            return minuteNum;
        }

        public void setMinuteNum( int minuteNum ) {
            this.minuteNum = minuteNum;
        }

        public LineCharMinuteSH( int minuteNum ) {
            this.minuteNum = minuteNum;
        }
    }

    public static int getInterval( int intervalNum ) {


        int width = mWidth - 2 * mPadding;

        return width / intervalNum;
    }

    // private static int MAX_MINUTE = 1440;

    private LineCharMinuteSH mLineCharMinuteSH;
    public static double[] mSpeedArray;
    private ArrayList<Location> locations;
    private Messages.Sport sport;
    // private PathEffect mEffect;
    private AnimatorSet mAnimation = null;

    private Paint mPaint;
    private Paint colorPaint;
    private PathEffect mCornerPathEffect;
    private PathEffect mDashPathEffect;
    private Path mPathLine;
    private Path mPathPhash;
    private Path mPathHorLine;

    public static int mPadding;
    private int mMaxHeight;
    private int mHeight;
    private static int mWidth;
    private static int mIntervalNum;
    public static int INTERVAL = 1;
    public static double mMax;

    private final int mPhase1;
    private final int mPhase2;
    private final int mPhase3;

    private final int mPhaseColor = Color.rgb(178, 178, 178);
    private final int[] colors;

    private final Bitmap startBitmap;
    private final Bitmap endBitmap;
    private final String startTimeString;
    private final String endTimeString;

    public int getLineChartLeft() {
        return mPadding;
    }

    public int getLineChartRight() {
        return mWidth - mPadding;
    }

    public double[] getStepArray() {
        return mSpeedArray;
    }

    public int getInterval() {
        return mIntervalNum;
    }

    public LineChart( Context context, Messages.Sport sportInput,
                      DisplayMetrics metrics, int height ) {


        super(context);
        // TODO Auto-generated constructor stub
        // Log.e("pony----", ""+metrics.widthPixels+":"+metrics.heightPixels);
        mPadding = AQUtility.dip2pixel(context, 10);
        mHeight = height;
        Log.e(TAG, "height is " + mHeight);

        mWidth = metrics.widthPixels;
        // mInterval = 4;
        mMaxHeight = mHeight - AQUtility.dip2pixel(context, 5);
        mIntervalNum = sportInput.getExtra().getStepCount();
        INTERVAL = getInterval(mIntervalNum);
        Log.e(TAG, "mIntervalNum is " + mIntervalNum);

        mPhase1 = mIntervalNum / 4;
        mPhase2 = mIntervalNum / 4 * 2;
        mPhase3 = mIntervalNum / 4 * 3;

        mLineCharMinuteSH = new LineCharMinuteSH(0);
        sport = sportInput;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colors = new int[]{Color.argb(255, 180, 236, 81),
        Color.argb(255, 42, 164, 223),
        Color.argb(255, 111, 200, 152)};
        mCornerPathEffect = new CornerPathEffect(40);
        mDashPathEffect = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);

        mPathLine = new Path();
        mPathPhash = new Path();
        mPathHorLine = new Path();

        startBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.manual_icon_start);
        sizeOf(startBitmap);
        endBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.manual_icon_end);
        sizeOf(endBitmap);

        startTimeString = ""
                + Util.dateFormat(Date.dateWithMilliSeconds(sport.getStartTime()), "HH:mm");
        long endTime = sport.getStartTime() + sport.getDuration();
        endTimeString = ""
                + Util.dateFormat(Date.dateWithMilliSeconds(endTime), "HH:mm");
        updateLineChart(sportInput);
    }

    private void updateSpeedArray( Messages.Sport sport ) {
        int timeSlotsSize = sport.getExtra().getStepCount();
        if (timeSlotsSize == 0) {
            return;
        }
        // int tempInterval = mIntervalNum/timeSlotsSize;
        // if (tempInterval == 0) {
        // tempInterval =1;
        // }
        List<Messages.Step> speedTem = sport.getExtra().getStepList();
        int length = speedTem.size();
        mSpeedArray = new double[length];
        Log.e("LineChart", "speed got by locations is" + length);
        for (int i = 0; i < length; i++) {
            if (speedTem.size() != 0 && i < speedTem.size()
                    && speedTem.get(i) != null) {
                mSpeedArray[i] = speedTem.get(i).getBpm();

                if (mMax < mSpeedArray[i]) {
                    mMax = mSpeedArray[i];
                }
            } else {
                mSpeedArray[i] = 0;
            }

            Log.d("pony____", "" + mSpeedArray[i]);
        }


//        mMax = (mMax * 1.55);
        double diff = mMax / mMaxHeight;
        for (int i = 0; i < mSpeedArray.length; i++) {
            mSpeedArray[i] = (mMaxHeight - mSpeedArray[i] / diff)
                    - startBitmap.getHeight() / 3;
        }

    }

    public double getSpeed( int realIndex ) {
        return (mMaxHeight - startBitmap.getHeight() / 3 - mSpeedArray[realIndex])
                * mMax / mMaxHeight;
    }

    public int getRealIndex( int index ) {
        return index / INTERVAL;
    }

    public void updateLineChart(Messages.Sport sport ) {
        updateSpeedArray(sport);
//        if (!isAnimation) {
//            mLineCharMinuteSH.setMinuteNum(mIntervalNum);
//            invalidate();
//            updateLine(true);
//        } else {
            mLineCharMinuteSH.setMinuteNum(0);
            invalidate();
            startAnimation(0, mIntervalNum);
            updateLine(true);
//        }
    }

    private void createAnimation( int startNum, int endNum ) {
        if (mAnimation == null) {
            ObjectAnimator anim = ObjectAnimator.ofInt(mLineCharMinuteSH,
                    "minuteNum", startNum, endNum).setDuration(3000);

            anim.addUpdateListener(this);

            mAnimation = new AnimatorSet();
            mAnimation.play(anim);
        }
    }

    public void startAnimation( int startNum, int endNum ) {
        createAnimation(startNum, endNum);
        mAnimation.start();
    }

    public void updateLine( boolean isPost ) {
        if (isPost) {
            this.postInvalidate();
        } else {
            this.invalidate();
        }

    }

    @Override
    public void onAnimationUpdate( ValueAnimator animation ) {
        // TODO Auto-generated method stub
        invalidate();
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        int minuteNum = mSpeedArray.length;

        if (minuteNum <= 0) {
            return;
        }
        // invalidate();
        mPathLine.reset();
        mPathHorLine.reset();
        mPathPhash.reset();




        float gap = INTERVAL;

        canvas.drawColor(Color.TRANSPARENT);

        // path
        mPaint.reset();
        mPaint.setPathEffect(null);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.rgb(255, 126, 0));

        mPaint.setStyle(Paint.Style.STROKE);

        float start = 0;
        float end = 0;
        if (minuteNum > 0) {
            start = (float) mSpeedArray[0];
            end = (float) mSpeedArray[minuteNum - 1];
            mPathLine.moveTo(mPadding + 0, start);
        }
        double max = mSpeedArray[0];
        double min = 0;
        for (int i = 0; i < (minuteNum-1); i++) {
            mPathLine.quadTo(mPadding + i * gap, (float) mSpeedArray[i], (mPadding + i * gap + gap), (float) mSpeedArray[i+1]);
            if (mSpeedArray[i] > max) {
                max = mSpeedArray[i];
            }
        }

        colorPaint.reset();
        LinearGradient lgGradient = new LinearGradient(mPadding, (float) min,
                mPadding, (float) max, colors, null, TileMode.CLAMP);

        colorPaint.setShader(lgGradient);
        colorPaint.setStrokeWidth(6);
        colorPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPathLine, colorPaint);

        // 添加时间的显示
        PointF startPoint = new PointF(mPadding, start);
        PointF endPoint = new PointF((mPadding + minuteNum * gap), end);
        mPaint.setPathEffect(null);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mPhaseColor);
        float scaleSize = getResources().getDimensionPixelSize(
                R.dimen.text_font_size);
        mPaint.setTextSize(scaleSize);
        Rect textBounds = new Rect();
        mPaint.getTextBounds(startTimeString, 0, startTimeString.length(),
                textBounds);

//        canvas.drawText(startTimeString, startPoint.x - textBounds.width() / 2,
//                (float) (textBounds.height() * 1.2), mPaint);
//        canvas.drawText(endTimeString, endPoint.x - textBounds.width() / 2,
//                (float) (textBounds.height() * 1.2), mPaint);

        // 添加 起始终止位置的icon
        canvas.drawBitmap(startBitmap, startPoint.x - startBitmap.getWidth()
                / 2, startPoint.y - startBitmap.getHeight() / 2, mPaint);
        canvas.drawBitmap(endBitmap, endPoint.x - endBitmap.getWidth() / 2,
                endPoint.y - endBitmap.getHeight() / 2, mPaint);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf( Bitmap data ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }
}
