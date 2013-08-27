package com.cantwellcode.cantwellgallery;

/**
 * Created by Daniel on 8/24/13.
 *
 * Implement to handle removing items.
 * An adapter handling the underlying data
 * will most likely handle this interface
 */
public interface RemoveListener {

    /**
     * Called when an item is to be removed
     * @param which - indicates which item to remove
     */
    void onRemove(int which);
}
