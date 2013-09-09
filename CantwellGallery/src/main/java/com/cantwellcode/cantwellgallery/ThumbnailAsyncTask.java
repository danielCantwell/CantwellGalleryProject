package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

/**
 * Created by Chris on 8/31/13.
 */
public class ThumbnailAsyncTask extends AsyncTask<Long,Void,Bitmap> {
    private BitmapCache  mCache;
    private Context      mContext;
    private ImageView    mTarget;

    public ThumbnailAsyncTask(Context context, ImageView target, BitmapCache cache){
        mContext = context;
        mTarget  = target;
        mCache   = cache;
    }

    @Override
    protected Bitmap doInBackground(Long... params) {
        Bitmap result = null;
        long imageID = params[0];
        if(mCache != null) result = mCache.get(imageID);
        if(result != null) return result;
        result = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),imageID,
                MediaStore.Images.Thumbnails.MINI_KIND,null);
        mCache.put(imageID,result);
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if(mTarget != null) mTarget.setImageBitmap(result);
    }
}
