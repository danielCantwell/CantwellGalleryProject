package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/14/13.
 */
public class ImageLocalState {
    private final ImageViewHolder   mHolder;
    private final Cursor            mCursor;
    private final long              mImageID;
    private final String            mImagePath;

    public ImageLocalState(ImageViewHolder holder, Cursor cursor, long imageID, String imagePath) {
        this.mHolder = holder;
        this.mCursor = cursor;
        this.mImageID = imageID;
        this.mImagePath = imagePath;
    }

    public ImageViewHolder getHolder() {
        return mHolder;
    }
    public Cursor getCursor() {
        return mCursor;
    }
    public long getImageID(){
        return mImageID;
    }

    public String getImagePath() {
        return mImagePath;
    }
}
