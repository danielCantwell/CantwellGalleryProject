package com.cantwellcode.cantwellgallery;

import android.view.View;

/**
 * Created by Chris on 9/14/13.
 * Holds information required for drag and drop interaction
 */
public class ImageData extends ItemData{

    private final View              mView;

    public ImageData(long mItemID, String mItemPath, View view) {
        super(mItemID, mItemPath);
        this.mView = view;
    }
    public View getView() {
        return mView;
    }

}
