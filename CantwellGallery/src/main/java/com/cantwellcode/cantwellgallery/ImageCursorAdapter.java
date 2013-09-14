package com.cantwellcode.cantwellgallery;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Chris on 9/2/13.
 * Takes a cursor to image data and constructs views
 * Assumes the cursor has a co
 */
public class ImageCursorAdapter extends BaseAdapter{
    private static final String TAG = "ImageCursorAdapter";

    private static final String NULL_CONTEXT = "Must supply a valid context.";
    private static final String NULL_COLUMN_NAMES = "Must supply non-null column names";

    private String              mIDColumnName           = "_ID";
    private String              mImageIDColumnName      = "_ID";
    private int                 mImageResourceID;
    private int                 mItemLayoutID;
    private int                 mIDIndex;
    private int                 mImageIDIndex;

    private Context             mContext;
    private Cursor              mCursor;
    private LayoutInflater      mInflater;
    private BitmapCache         mCache;

    public ImageCursorAdapter(Context context, Cursor cursor, int itemLayoutID, int imageResourceID){
        if(context != null)mContext = context;
        else throw new IllegalArgumentException(NULL_CONTEXT);

        mCursor = cursor;

        mItemLayoutID    = itemLayoutID;
        mImageResourceID = imageResourceID;

        mInflater = LayoutInflater.from(context);

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClassBytes = am.getMemoryClass()*1024*1024;
        mCache = new BitmapCache(memoryClassBytes/8);
    }

    public void changeCursor(Cursor cursor) {
        mCursor = cursor;
        if(cursor != null){
            findColumns();
            notifyDataSetChanged();
        } else notifyDataSetInvalidated();
    }

    private void findColumns() {
        if (mCursor == null){
            mIDIndex = -1;
            mImageIDIndex = -1;
        }else{
            mIDIndex      = (mIDColumnName!= null)      ? mCursor.getColumnIndexOrThrow(mIDColumnName)      : -1;
            mImageIDIndex = (mImageIDColumnName!= null) ? mCursor.getColumnIndexOrThrow(mImageIDColumnName) : -1;
        }
    }

    @Override
    public int getCount() {
        return (mCursor!=null) ? mCursor.getCount() : 0;
    }

    @Override
    public Object getItem(int i) {
        if (mCursor != null){
            mCursor.moveToPosition(i);
            return mCursor;
        }else return null;
    }

    @Override
    public long getItemId(int i) {
        if(mCursor != null && mIDIndex != -1){
            mCursor.moveToPosition(i);
            return mCursor.getLong(mIDIndex);
        }
        else return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageViewHolder holder;
        if (convertView==null){
            convertView = mInflater.inflate(mItemLayoutID,parent,false);
            holder = new ImageViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(mImageResourceID);

            convertView.setTag(holder);
        }else{
            holder = (ImageViewHolder) convertView.getTag();
        }
        mCursor.moveToPosition(position);
        long imageID = mCursor.getLong(mImageIDIndex);
        new ThumbnailAsyncTask(mContext,holder.imageView,mCache).execute(imageID);
        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }
}
