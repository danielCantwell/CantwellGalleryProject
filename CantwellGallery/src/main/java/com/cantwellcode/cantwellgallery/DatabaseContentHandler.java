package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/13/13.
 */
public interface DatabaseContentHandler {
    public void changeContentCursor(String label,Cursor cursor);
}
