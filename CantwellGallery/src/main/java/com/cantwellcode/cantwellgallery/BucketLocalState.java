package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/14/13.
 */
public class BucketLocalState {

    private final BucketViewHolder  mHolder;
    private final Cursor            mCursor;


    public BucketLocalState(BucketViewHolder holder, Cursor cursor) {
        this.mHolder = holder;
        this.mCursor = cursor;
    }

    public BucketViewHolder getHolder() {
        return mHolder;
    }
    public Cursor getCursor() {
        return mCursor;
    }


}
