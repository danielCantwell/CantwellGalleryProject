package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/14/13.
 */
public class ImageLocalState {
    private final ImageViewHolder   mHolder;
    private final Cursor            mCursor;

    public ImageLocalState(ImageViewHolder holder, Cursor cursor) {
        this.mHolder = holder;
        this.mCursor = cursor;
    }

    public ImageViewHolder getHolder() {
        return mHolder;
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
