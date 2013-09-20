package com.cantwellcode.cantwellgallery;

import android.database.Cursor;

/**
 * Created by Chris on 9/13/13.
 */
public interface TargetBar {

    public interface Callbacks{
        public boolean onMoveItemToTarget(String itemPath, String targetPath);
        public boolean onCreateNewTargetFromItem(Cursor itemCursor);
    }

    public boolean moveItemToTarget(String itemPath, String targetPath);
    public boolean createNewTargetFromItem(Cursor itemCusor);
    public long getCurrentTarget();
    public String getCurrentTargetPath();

}
