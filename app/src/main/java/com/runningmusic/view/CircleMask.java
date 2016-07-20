package com.runningmusic.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.androidquery.util.AQUtility;

/**
 * Created by guofuming on 20/1/16.
 */
public class CircleMask extends View implements ValueAnimator.AnimatorUpdateListener {
    public static String TAG = CircleMask.class.getName();

    class CircleMaskShapeHolder {

        private int currentRadian = 0;

        public void setCurrentRadian(int radian) {
            currentRadian = radian;
        }

        public int getCurrentRadian() {
            return currentRadian;
        }

        public CircleMaskShapeHolder(int radian) {
            currentRadian = radian;
        }
    }

    public static float CIRCLE_MASK_WIDTH = 206;
    public static float SMALL_CIRCLE_MASK_WIDTH = 23;

    public CircleMaskShapeHolder currentRadianSH = new CircleMaskShapeHolder(0);

    private Point centerPoint_;
    private Paint paint_;
    private RectF rectFOutside_;
    private RectF rectFInside_;
    private RectF rectFSmallUp_;
    private RectF rectFSmallDown_;
    private Path path_;

    int cricleWdithPx_ = 0;
    int smallCircleWidthPx_ = 0;
    float radius = 0;
    float smallRadius = 0;

    private int currentRadian = 0;
    private ObjectAnimator objectAnimator_;

    public CircleMask(Context context) {
        super(context);

        cricleWdithPx_ = AQUtility.dip2pixel(context, CIRCLE_MASK_WIDTH);
        smallCircleWidthPx_ = AQUtility.dip2pixel(context, SMALL_CIRCLE_MASK_WIDTH);

        centerPoint_ = new Point(cricleWdithPx_ / 2, cricleWdithPx_ / 2);
        path_ = new Path();
        paint_ = new Paint();
        paint_.setAntiAlias(true);
        paint_.setColor(Color.WHITE);

        // instruct recf
        radius = cricleWdithPx_ / 2;
        rectFOutside_ = new RectF(centerPoint_.x - radius, centerPoint_.y - radius, centerPoint_.x + radius, centerPoint_.y + radius);
        rectFInside_ = new RectF(centerPoint_.x - radius + smallCircleWidthPx_, centerPoint_.y - radius + smallCircleWidthPx_, centerPoint_.x + radius
                - smallCircleWidthPx_, centerPoint_.y + radius - smallCircleWidthPx_);

        smallRadius = smallCircleWidthPx_ / 2;
        rectFSmallUp_ = new RectF(centerPoint_.x - smallRadius, centerPoint_.y - radius, centerPoint_.x + smallRadius, centerPoint_.y - radius
                + smallCircleWidthPx_);
        rectFSmallDown_ = new RectF();
        // radian
        // currentRadianSH = new CircleMaskShapeHolder((int) radian);
    }

    private void createAnimator(int startRadian, int endRadian) {
        if (objectAnimator_ == null) {
            objectAnimator_ = ObjectAnimator.ofInt(currentRadianSH, "currentRadian", startRadian, endRadian).setDuration(700);
            objectAnimator_.addUpdateListener(this);
            objectAnimator_.setInterpolator(new AccelerateDecelerateInterpolator());
        } else {
            objectAnimator_.setIntValues(startRadian, endRadian);
        }
    }

    public void updateCircleMask(int startRadian, int endRadian, boolean isPost) {
        if (isPost) {
            Log.i(TAG, "start: " + startRadian + " end: " + endRadian);
            createAnimator(startRadian, endRadian);
            objectAnimator_.start();
        } else {
            currentRadianSH.setCurrentRadian(endRadian);
            invalidate();
        }

        // if (isPost) {
        // this.postInvalidate();
        // } else {
        // this.invalidate();
        // }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        canvas.save();

        currentRadian = currentRadianSH.getCurrentRadian();
        // currentRadian = 50;
        if (currentRadian >= 360 - 10) {
            // 不用再画白色 圆环
            return;
        }
        path_.reset();

        if (currentRadian == 0) {
            paint_.setStyle(Paint.Style.STROKE);
            paint_.setStrokeWidth(smallCircleWidthPx_);
            canvas.drawCircle(centerPoint_.x, centerPoint_.y, radius - smallRadius, paint_);
            return;
        }
        // float startAngle = 90;
        // float sweepAngle = 90;

        paint_.setStyle(Paint.Style.FILL);

        path_.arcTo(rectFOutside_, currentRadian - 90, 360 - currentRadian);
        path_.arcTo(rectFSmallUp_, -90, -180);
        path_.arcTo(rectFInside_, -90, -1 * (360 - currentRadian));

        float x = (float) (centerPoint_.x + Math.sin(Math.toRadians(currentRadian)) * (radius - smallRadius));
        float y = (float) (centerPoint_.y - Math.cos(Math.toRadians(currentRadian)) * (radius - smallRadius));
        rectFSmallDown_.set(x - smallRadius, y - smallRadius, x + smallRadius, y + smallRadius);
        path_.arcTo(rectFSmallDown_, currentRadian + 90, -180);

        canvas.drawPath(path_, paint_);

        canvas.restore();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

}
