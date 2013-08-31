package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Chris on 8/30/13.
 */
public class QuickBarCursorAdapter extends BaseAdapter {
    private static final String TAG = "QUICK_BAR_CURSOR_ADAPTER";

    private static final String NULL_CONTEXT = "Must supply a valid context.";
    private static final String NULL_COLUMN_NAMES = "Must supply non-null column names";

    private String              mIDColumnName;
    private String              mTextColumnName;
    private String              mImageIDColumnName;
    private int                 mIDIndex;
    private int                 mTextIndex;
    private int                 mImageIDIndex;

    private Context             mContext;
    private Cursor              mCursor;
    private LayoutInflater      mInflater;

    private static class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

    public QuickBarCursorAdapter(Context context, Cursor cursor, String idColumn, String textColumn, String imageIDColumn){
        if(context != null)mContext = context;
        else throw new IllegalArgumentException(NULL_CONTEXT);

        mCursor = cursor;

        if(idColumn!=null && textColumn!=null && imageIDColumn!= null){
            mIDColumnName = idColumn;
            mTextColumnName = textColumn;
            mImageIDColumnName = imageIDColumn;
            findColumns();
        }
        else throw new IllegalArgumentException(NULL_COLUMN_NAMES);

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
            mIDIndex = -1;
            mTextIndex = -1;
            mImageIDIndex = -1;
        }else{
            mIDIndex = (mIDColumnName!= null) ? mCursor.getColumnIndexOrThrow(mIDColumnName) : -1;
            mTextIndex = (mTextColumnName!= null) ? mCursor.getColumnIndexOrThrow(mTextColumnName) : -1;
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
        ViewHolder holder;
        if (convertView==null){
            convertView = mInflater.inflate(R.layout.quick_bar_item,parent,false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.quickBarItemTextView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.quickBarItemImageView);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        mCursor.moveToPosition(position);
        holder.textView.setText(mCursor.getString(mTextIndex));
        Bitmap thumb = MediaStore.Images.Thumbnails.getThumbnail(
                mContext.getContentResolver(),mCursor.getLong(mImageIDIndex),MediaStore.Images.Thumbnails.MINI_KIND,null);
        holder.imageView.setImageBitmap(thumb);
        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }
}
