package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Daniel on 9/12/13.
 */
public class SlidingPane extends SlidingPaneLayout {

    private float xLeftSlideArea;
    private float xRightSlideArea;

    private float yTopSlideArea;
    private float yBotSlideArea;

    private float wSlideArea;
    private float hSlideArea;

    private ImageView slideArea;

    public SlidingPane(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingPane(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SlidingPane(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        init();

        // Coordinates of the touch event
        float xTouch = ev.getX();
        float yTouch = ev.getY();

        // If the touch event is within the designated drag area, allow panel sliding
        if ( (xTouch >= xLeftSlideArea)  && (yTouch >= yTopSlideArea) &&
             (xTouch <= xRightSlideArea) && (yTouch <= yBotSlideArea)) {

            return true;
        }

        return false;
    }

    private void init() {

        slideArea = (ImageView) findViewById(R.id.contentPaneDragArea);

        xLeftSlideArea = slideArea.getX();      // Top Left X Coordinate of the drag area
        yTopSlideArea  = slideArea.getY();      // Top Left Y Coordinate of the drag area

        wSlideArea = slideArea.getWidth();  // Width  of the drag area
        hSlideArea = slideArea.getHeight(); // Height of the drag area

        xRightSlideArea = xLeftSlideArea + wSlideArea;
        yBotSlideArea   = yTopSlideArea  + hSlideArea;
    }
}
