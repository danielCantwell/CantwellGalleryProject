package com.cantwellcode.cantwellgallery;

/**
 * Created by Chris on 9/13/13.
 */
public interface TargetBar {
    public interface Callbacks{
        public boolean onMoveItemToTarget(long itemID,long targetID);
        public boolean onCreateNewTargetFromItem(long itemID);
    }

    public boolean moveItemToTarget(long itemID,long targetID);
    public boolean createNewTargetFromItem(long itemID);
}
