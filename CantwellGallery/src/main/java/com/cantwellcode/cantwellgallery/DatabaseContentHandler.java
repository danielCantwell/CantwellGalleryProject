package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/13/13.
 */
public interface DatabaseContentHandler {
    public interface Callbacks{
        public boolean onSwipeLeft(String itemPath);
    }

    public void changeContentLabelAndCursor(String label, Cursor cursor);
}
