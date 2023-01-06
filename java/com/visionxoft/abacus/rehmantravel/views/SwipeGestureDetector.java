package com.visionxoft.abacus.rehmantravel.views;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.visionxoft.abacus.rehmantravel.activity.MainActivity;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    MainActivity mainActivity;
    View toolbar_user;

    public SwipeGestureDetector(MainActivity mainActivity, View toolbar_user) {
        this.mainActivity = mainActivity;
        this.toolbar_user = toolbar_user;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {

        switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
            case 1:
                //  Updward Swipe
                animateOnScreen(mainActivity, toolbar_user);
                return true;
            case 2:
                // Left Swipe
                return true;
            case 3:
                // Down Swipe
                return true;
            case 4:
                // Right Swipe
                return true;
        }
        return false;
    }

    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle >= -135)
            // down
            return 3;
        if (angle > -45 && angle <= 45)
            // right
            return 4;
        return 0;
    }

    private static void animateOnScreen(Activity activity, final View view) {
        final int screenHeight = LayoutHack.getScreenHeight(activity);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "y", screenHeight, (screenHeight - 60));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "y", screenHeight, (screenHeight));
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            }
        }, 4000);
    }
}
