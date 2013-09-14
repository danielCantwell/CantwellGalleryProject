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
 * Created by Chris on 8/30/13.
 */
public class BucketCursorAdapter extends BaseAdapter {
    private static final String TAG = "QUICK_BAR_CURSOR_ADAPTER";

    private static final String NULL_CONTEXT      = "Must supply a valid context.";
    private static final String NULL_COLUMN_NAMES = "Must supply non-null column names";

    private String              mIDColumnName;
    private String              mTextColumnName;
    private String              mImageIDColumnName;
    private int                 mIDIndex;
    private int                 mTextIndex;
    private int                 mImageIDIndex;

    private BitmapCache         mCache;
    private Context             mContext;
    private Cursor              mCursor;
    private LayoutInflater      mInflater;

    private static class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

    public BucketCursorAdapter(Context context, Cursor cursor, String idColumn, String textColumn, String imageIDColumn){
        // Initialize thumbnail cache
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClassBytes = am.getMemoryClass()*1024*1024;
        mCache = new BitmapCache(memoryClassBytes/16);

        // Initialize context
        if(context != null)mContext = context;
        else throw new IllegalArgumentException(NULL_CONTEXT);

        // Initialize cursor
        mCursor = cursor;

        // Initialize column indices
        if(idColumn!=null && textColumn!=null && imageIDColumn!= null){
            mIDColumnName      = idColumn;
            mTextColumnName    = textColumn;
            mImageIDColumnName = imageIDColumn;
            findColumns();
        }
        else throw new IllegalArgumentException(NULL_COLUMN_NAMES);

        // Initialize layout inflater
        mInflater = LayoutInflater.from(context);
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
            mIDIndex      = -1;
            mTextIndex    = -1;
            mImageIDIndex = -1;
        }else{
            mIDIndex      = (mIDColumnName!= null)      ? mCursor.getColumnIndexOrThrow(mIDColumnName)      : -1;
            mTextIndex    = (mTextColumnName!= null)    ? mCursor.getColumnIndexOrThrow(mTextColumnName)    : -1;
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
        BucketViewHolder holder;
        
        if (convertView==null){
            convertView = mInflater.inflate(R.layout.target_bar_item,parent,false);
            holder = new BucketViewHolder();
            holder.textView  = (TextView)  convertView.findViewById(R.id.targetBarItemTextView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.targetBarItemImageView);

            convertView.setTag(holder);
        }else{
            holder = (BucketViewHolder) convertView.getTag();
        }

        mCursor.moveToPosition(position);
        holder.textView.setText(mCursor.getString(mTextIndex));
        long imageID = mCursor.getLong(mImageIDIndex);
        new ThumbnailAsyncTask(mContext,holder.imageView,mCache).execute(imageID);

        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }
}
