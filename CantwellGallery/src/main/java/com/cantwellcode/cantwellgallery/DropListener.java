package com.cantwellcode.cantwellgallery;

/**
 * Created by Daniel on 8/24/13.
 *
 * Implement to handle an item being dropped
 * An adapter handling the underlying data
 * will most likely handle this interface
 */
public interface DropListener {

    /**
     * Called when an item is to be dropped
     * @param from - index item started at
     * @param to - index to place item at
     */
    void onDrop(int from, int to);
}
