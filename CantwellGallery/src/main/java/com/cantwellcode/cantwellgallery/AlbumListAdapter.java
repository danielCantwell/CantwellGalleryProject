package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 8/14/13.
 */
public class AlbumListAdapter extends BaseAdapter {

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

    private int             mActivePosition;
    private Context         mContext;
    private LayoutInflater  mInflater;
    private List<File>      mAlbums;

    /**
     * Constructor that does not have a List of Files as a parameter
     * so it creates a blank list
     * @param context
     * @param activePosition
     */
    public AlbumListAdapter(Context context, int activePosition){
        init(context, new ArrayList<File>(), activePosition);
    }

    /**
     * Constructor that includes the List of Files, which are the albums
     * @param context
     * @param albums
     * @param activePosition
     */
    public AlbumListAdapter(Context context, List<File> albums, int activePosition){
        init(context,albums,activePosition);
    }

    /**
     * Returns the size of the List of album Files
     * @return
     */
    @Override
    public int getCount() {
        return mAlbums.size();
    }

    /**
     * Returns the File at index i
     * @param i
     * @return
     */
    @Override
    public Object getItem(int i) {
        return mAlbums.get(i);
    }

    /**
     * Returns the @id/ of the item at index i
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Called each time a view needs to be loaded
     * to be displayed in the list
     * It controls what and how each item is displayed
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        File album = mAlbums.get(position);

        // if the list view is first being populated
        if (convertView == null){
            holder = new ViewHolder();

            // for the active position
            if (position==mActivePosition){
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
        holder.text.setText(album.getName());
        holder.icon.setImageResource(R.drawable.ic_launcher);

        return convertView;
    }

    /**
     * Initialize member variables
     * @param context
     * @param albums
     * @param activePosition
     */
    private void init(Context context, List<File> albums, int activePosition){
        mContext        = context;
        mAlbums         = albums;
        mActivePosition = activePosition;
        mInflater       = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
