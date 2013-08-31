package com.cantwellcode.cantwellgallery;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Chris on 8/29/13.
 */
public class ThumbnailCache extends LruCache<Long,Bitmap>{
    public ThumbnailCache(int maxSize) {
        super(maxSize);
    }
    @Override
    protected int sizeOf(Long key, Bitmap value) {
        return value.getByteCount();
    }
}
