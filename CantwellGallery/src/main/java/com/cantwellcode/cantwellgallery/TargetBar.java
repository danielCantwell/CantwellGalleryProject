package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/13/13.
 */
public interface TargetBar {
    public interface Callbacks{
        public boolean onMoveItemToTarget(Cursor itemCursor, Cursor targetCursor);
        public boolean onCreateNewTargetFromItem(Cursor itemCursor);
    }

    public boolean moveItemToTarget(Cursor itemCursor, Cursor targetCursor);
    public boolean createNewTargetFromItem(Cursor itemCursor);
}
