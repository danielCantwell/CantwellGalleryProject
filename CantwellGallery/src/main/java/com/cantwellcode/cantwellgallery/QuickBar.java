package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Chris on 8/14/13.
 */
public class QuickBar extends ListView{
    View activeDirectory;

    public QuickBar(Context context) {
        super(context);
    }

    public QuickBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuickBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
