package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/13/13.
 */
public interface DatabaseMaster {
    public interface Callbacks {
        public void changeContentCursor(String label, Cursor cursor);
    }
}
