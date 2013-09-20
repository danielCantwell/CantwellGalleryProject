package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/14/13.
 */
public class BucketLocalState {

    private final BucketViewHolder  mHolder;
    private final Cursor            mCursor;
    private final long              mBucketID;
    private final String            mBucketPath;


    public BucketLocalState(BucketViewHolder holder, Cursor cursor, long bucketID, String bucketPath) {
        this.mHolder = holder;
        this.mCursor = cursor;
        this.mBucketID = bucketID;
        this.mBucketPath = bucketPath;
    }

    public BucketViewHolder getHolder() {
        return mHolder;
    }
    public Cursor getCursor() {
        return mCursor;
    }
    public long getBucketID() {
        return mBucketID;
    }
    public String getBucketPath() {
        return mBucketPath;
    }


}
