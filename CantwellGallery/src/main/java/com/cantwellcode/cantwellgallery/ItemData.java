package com.cantwellcode.cantwellgallery;

import java.io.File;

/**
 * Created by Chris on 9/20/13.
 * Contains data pertaining to individual items stored in a database
 * Used for passing item information during touch events
 */
public class ItemData {
    private final long      mItemID;        // Database id for the item
    private final String    mItemPath;      // File path for the item

    public long getItemID() {
        return mItemID;
    }
    public String getItemPath() {
        return mItemPath;
    }
    public ItemData(long mItemID, String mItemPath) {
        this.mItemID = mItemID;
        this.mItemPath = mItemPath;
    }
}
