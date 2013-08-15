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
    static class ViewHolder{
        ImageView icon;
        TextView text;
    }
    private static final int ACTIVE_KEY = 0;
    private static final int INACTIVE_KEY = 1;
    private Context mContext;
    private int mActivePosition;
    private LayoutInflater mInflater;
    private List<File> mAlbums;

    public AlbumListAdapter(Context context, int activePosition){
        init(context, new ArrayList<File>(), activePosition);
    }

    public AlbumListAdapter(Context context, List<File> albums, int activePosition){
        init(context,albums,activePosition);
    }

    @Override
    public int getCount() {
        return mAlbums.size();
    }

    @Override
    public Object getItem(int i) {
        return mAlbums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        File album = mAlbums.get(position);
        if (convertView == null){
            holder = new ViewHolder();
            if (position==mActivePosition){
                convertView=mInflater.inflate(R.layout.active_album, null, false);
                holder.text = (TextView) convertView.findViewById(R.id.activeAlbumName);
                holder.icon = (ImageView) convertView.findViewById(R.id.activeAlbumIcon);
                convertView.setTag(ACTIVE_KEY,holder);
            }
            else{
                convertView = mInflater.inflate(R.layout.active_album,null,false);
                holder.text = (TextView) convertView.findViewById(R.id.inactiveAlbumName);
                holder.icon = (ImageView) convertView.findViewById(R.id.inactiveAlbumIcon);
                convertView.setTag(INACTIVE_KEY,holder);
            }
        }
        else if (position == mActivePosition){
            holder = (ViewHolder) convertView.getTag(ACTIVE_KEY);
        }
        else{
            holder = (ViewHolder) convertView.getTag(INACTIVE_KEY);
        }
        holder.text.setText(album.getName());
        holder.icon.setImageResource(R.drawable.ic_launcher);
        return convertView;
    }

    private void init(Context context, List<File> albums, int activePosition){
        mContext = context;
        mAlbums = albums;
        mActivePosition = activePosition;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
