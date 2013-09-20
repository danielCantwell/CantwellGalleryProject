package com.cantwellcode.cantwellgallery;

import android.database.Cursor;
import android.view.View;

/**
 * Created by Chris on 9/14/13.
 * Holds information required for drag and drop interaction
 */
public class ImageLocalState {

    private final View              mView;
    private final ImageViewHolder   mHolder;
    private final Cursor            mCursor;
    private final long              mImageID;
    private final String            mImagePath;

    public ImageLocalState(View view, ImageViewHolder holder, Cursor cursor, long imageID, String imagePath) {
        this.mView      = view;
        this.mHolder    = holder;
        this.mCursor    = cursor;
        this.mImageID   = imageID;
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
    public View getView() {
        return mView;
    }

}
