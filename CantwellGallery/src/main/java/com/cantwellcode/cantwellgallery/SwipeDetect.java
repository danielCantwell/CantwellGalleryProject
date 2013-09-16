package com.cantwellcode.cantwellgallery;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Daniel on 9/15/13.
 */
public class SwipeDetect implements View.OnTouchListener {

    public static enum Swipe {
        Right,  // Left   to Right
        Left,   // Right  to Left
        Down,   // Top    to Bottom
        Up,     // Bottom to Top
        None    // No Swipe
    }

    private final int MIN_HORIZONTAL = 300;
    private final int MIN_VERTICAL = 50;

    private static float downX;    // X Coord of touch
    private static float downY;    // Y Coord of touch

    private static float upX;      // X Coord of release
    private static float upY;      // Y Coord of release

    private float dX;       // delta X
    private float dY;       // delta Y

    private Swipe mSwipe = Swipe.None;

    public boolean swipeDetected() {
        return mSwipe != Swipe.None;
    }

    public Swipe getSwipe() {
        return mSwipe;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = motionEvent.getX();
                downY = motionEvent.getY();
                mSwipe = Swipe.None;
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                upX = motionEvent.getX();
                upY = motionEvent.getY();

                dX = downX - upX;
                dY = downY - upY;

                // Horizontal Swipe
                if (Math.abs(dX) > MIN_HORIZONTAL) {
                    // Swipe Right
                    if (dX < 0) {
                        mSwipe = Swipe.Right;
                        return true;
                    }
                    // Swipe Left
                    if (dX > 0) {
                        mSwipe = Swipe.Left;
                        return true;
                    }
                }

                // Vertical Swipe
                if (Math.abs(dY) > MIN_VERTICAL) {
                    // Swipe Down
                    if (dY < 0) {
                        mSwipe = Swipe.Down;
                        return false;
                    }
                    // Swipe Up
                    if (dY > 0) {
                        mSwipe = Swipe.Up;
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }
}
