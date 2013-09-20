package com.cantwellcode.cantwellgallery;

import android.view.View;

/**
 * Created by Chris on 9/14/13.
 */
public class BucketData extends DirectoryData{

    private final View    mView;

    public View getView() {
        return mView;
    }
    public BucketData(long directoryID, String directoryPath, String displayName, View view) {
        super(directoryID, directoryPath, displayName);
        mView = view;
    }
}
