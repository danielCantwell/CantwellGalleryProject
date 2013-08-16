package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 8/14/13.
 * Adapter for displaying data from a cursor in a ListView.
 * Accepts a position corresponding to an active selection.
 */
public class AlbumListAdapter extends CursorAdapter {

    /**
     * Inner class to hold the icon and text for each view in the list
     */
    static class ViewHolder{
        ImageView icon;
        TextView  text;
    }

    /*
     * Keys for setting and getting the tags
     * of the Views in the List
     */
    private static final int ACTIVE_KEY   = 0;
    private static final int INACTIVE_KEY = 1;
    private static final int DEFAULT_ACTIVE_POSITION = 0;

    private int mActivePosition;
    private LayoutInflater mInflater;


    /**
     * Default constructor.  Creates a blank list and uses the default active position.
     * @param context
     * @param cursor
     */
    public AlbumListAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
        init(DEFAULT_ACTIVE_POSITION);
    }
    /**
     * Constructor that does not have a List of IDs as a parameter
     * so it creates a blank list.
     * @param context
     * @param cursor
     * @param activePosition
     */
    public AlbumListAdapter(Context context, Cursor cursor, int activePosition){
        super(context,cursor,0);
        init(activePosition);
    }

/*    public AlbumListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AlbumListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
*/


    /**
     * Called each time a view needs to be loaded
     * to be displayed in the list
     * It controls what and how each item is displayed
     * @param //position
     * @param //convertView
     * @param //parent
     * @return
     */
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // if the list view is first being populated
        if (convertView == null){
            holder = new ViewHolder();

            // for the active position
            if (position== mActivePosition){
                // inflate the view with the layout for "active position"
                convertView=mInflater.inflate(R.layout.active_album, null, false);

                // set the text and icon for the view
                holder.text = (TextView)  convertView.findViewById(R.id.activeAlbumName);
                holder.icon = (ImageView) convertView.findViewById(R.id.activeAlbumIcon);

                // set tag for this active view, containing the text and icon information
                convertView.setTag(ACTIVE_KEY, holder);
            }
            // for the inactive position
            else {
                // inflate the view with the layout for "inactive position"
                convertView = mInflater.inflate(R.layout.active_album,null,false);

                // set the text and icon for the view
                holder.text = (TextView)  convertView.findViewById(R.id.inactiveAlbumName);
                holder.icon = (ImageView) convertView.findViewById(R.id.inactiveAlbumIcon);

                // set tag for this inactive view, containing the text and icon information
                convertView.setTag(INACTIVE_KEY,holder);
            }
        }

        // if the view is in the active position
        else if (position == mActivePosition){
            // load the text and icon information for the active position
            holder = (ViewHolder) convertView.getTag(ACTIVE_KEY);
        }
        // if the view is in any of the inactive positions
        else{
            // load the text and icon information for the inactive position
            holder = (ViewHolder) convertView.getTag(INACTIVE_KEY);
        }

        // set the text and icon information for the view, whether it is active or inactive position
        holder.text.setText("Text");
        holder.icon.setImageResource(R.drawable.ic_launcher);

        return convertView;
    }
*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(mContext, mCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, mCursor);
        return v;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v;
        int position = cursor.getPosition();
        if (position == mActivePosition){
            v = mInflater.inflate(R.layout.active_album,null,false);
        }
        else{
            v = mInflater.inflate(R.layout.inactive_album,null,false);
        }
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();
        if(position==mActivePosition){
        }
    }

    /**
     * Initialize member variables
     * @param activePosition
     */
    private void init(int activePosition){
        mActivePosition = activePosition;
        mInflater       = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
