package com.cantwellcode.cantwellgallery;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Daniel on 8/29/13.
 *
 * This is used to create the floating view
 */
public class MyDragShadowBuilder extends View.DragShadowBuilder {
    private Drawable mShadow;

    public MyDragShadowBuilder(View v) {
        super(v);
        //mShadow = v.getResources().getDrawable(R.drawable.ic_launcher);
        //mShadow.setCallback(v);
        //mShadow.setBounds(0, 0, v.getWidth(), v.getHeight());
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
        //mShadow.draw(canvas);
        getView().draw(canvas);
    }
}
