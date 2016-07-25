package com.runningmusic.view;

import android.animation.Animator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runningmusic.runninspire.R;

/**
 * Created by guofuming on 16/5/16.
 */
public class PulseView extends RelativeLayout {

    private ImageView backgroud;
    private TextView label;
    private FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();

    public PulseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.pulse, this);

        label = (TextView) findViewById(R.id.bpm);
        backgroud = (ImageView) findViewById(R.id.background);
    }

    public void setBpm(int bpm) {
        label.setText(String.valueOf(bpm));
    }

    public void pulse() {
        pulse(1.3f, 5);
    }

    private void pulse(float scale, int num) {
        float step = (scale - 1) / num;
        for (int n = 0; n < num; n +=1) {
            pulseOnce(1000+100*n, scale - n * step);
        }
    }

    private void pulseOnce(int duration, float scale) {
        final ImageView view = new ImageView(getContext());
        view.setLayoutParams(backgroud.getLayoutParams());
        view.setImageDrawable(getResources().getDrawable(R.drawable.pulse_background));
        view.animate()
                .setDuration(duration)
                .setInterpolator(interpolator)
                .scaleX(scale)
                .scaleY(scale)
                .alpha(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                removeView(view);
                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

        addView(view);
    }



}
