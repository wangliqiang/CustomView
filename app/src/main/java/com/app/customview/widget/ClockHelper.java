package com.app.customview.widget;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.app.customview.view.ClockView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ClockHelper extends TimerTask {

    private ClockView clockView;
    private Timer timer;

    public ClockHelper(ClockView clockView) {
        this.clockView = clockView;
    }

    public void start() {
        stop();
        timer = new Timer();
        timer.schedule(this, 0, 1000);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void getOff() {
        if (clockView == null) {
            return;
        }
        RotateAnimation rotateAnimation = new RotateAnimation(-15, 15, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(100);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(15);
//        rotateAnimation.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimation.setFillAfter(false);
        clockView.startAnimation(rotateAnimation);
    }

    @Override
    public void run() {
        if (clockView != null) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            clockView.setTime(hour, minute, second);
        }
    }
}
