package com.cantwellcode.cantwellgallery;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements QuickBarFragment.QuickBarCallbacks, LoaderManagerFragment.ListenerCallbacks{

    // Log tags
    private static final String TAG                     = "MAIN ACTIVITY";
    private static final String LOADER_MANAGER_FRAGMENT = "LOADER_MANAGER_FRAGMENT";

    // Exception strings
    private static final String INVALID_ID              = " is an invalid loader id. " +
            "If this is a BucketCursor, add the BUCKET_ID to mBucketCursors keyset before " +
            "calling load.";

    // Database Uri and column Strings
    private static final Uri    CONTENT_URI             = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String _ID                     = MediaStore.Images.Media._ID;
    private static final String BUCKET_ID               = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_DISPLAY_NAME     = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;

    // IDs
    private static final int    BUCKET_IDS              = 0x001;

    // Private Members
    private LoaderManagerFragment   mLoaderManagerFragment;
    private QuickBarFragment        mQuickBarFragment;
    private Cursor                  mBucketIds;
    private Map<Long,Cursor>        mBucketCursors;
    private Map<Long,Long>          mBucketThumbnailIDMap;
    private ThumbnailCache          mThumbnailCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        mBucketIds = null;
        mBucketCursors = null;
        mThumbnailCache = getThumbnailCache();

        FragmentManager fm = getSupportFragmentManager();

        // Initialize LoaderManagerFragment
        mLoaderManagerFragment = new LoaderManagerFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mLoaderManagerFragment,LOADER_MANAGER_FRAGMENT).commit();

        load(BUCKET_IDS);
        // Initialize QuickBarFragment
        mQuickBarFragment = (QuickBarFragment) fm.findFragmentById(R.id.quickBarFragment);




        final SlidingPaneLayout slidingPaneLayout = SlidingPaneLayout.class.cast(root.findViewById(R.id.slidingpanelayout));

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelOpened(View view) {

                switch (view.getId()) {
                    case R.id.album_pane:
                        getSupportFragmentManager().findFragmentById(R.id.photo_pane).setHasOptionsMenu(true);
                        getSupportFragmentManager().findFragmentById(R.id.photo_pane).setHasOptionsMenu(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPanelClosed(View view) {

                switch (view.getId()) {
                    case R.id.album_pane:
                        getSupportFragmentManager().findFragmentById(R.id.photo_pane).setHasOptionsMenu(false);
                        getSupportFragmentManager().findFragmentById(R.id.album_pane).setHasOptionsMenu(true);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private ThumbnailCache getThumbnailCache() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClassBytes = manager.getMemoryClass()*1024*1024;
        return new ThumbnailCache(memoryClassBytes/8);
    }

    /**
     * Constructs Loader arguments and requests load() from the LoaderManagerFragment
     * @param id : Reference ID of the Cursor to be loaded.
     */
    private void load(int id) {
        Uri uri;
        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder;
        switch (id){
            case BUCKET_IDS:
                uri = CONTENT_URI;
                projection = new String[]{"DISTINCT "+BUCKET_ID};
                selection = null;
                selectionArgs = new String[]{};
                sortOrder = null;
                break;
            default:
                // If this is a BucketCursor Loader that has been added to mBucketCursors, load
                if(isBucketCursor(id)){
                    // Intended State:
                    //      mBucketCursors.contains(id) == true
                    // Description:
                    // Action: Construct the appropriate Loader initialization arguments to
                    //      get a cursor with data associated with this BUCKET_ID
                    uri = CONTENT_URI;
                    projection = new String[]{_ID,BUCKET_ID,BUCKET_DISPLAY_NAME};
                    selection = BUCKET_ID + "=?";
                    selectionArgs = new String[]{String.valueOf(id)};
                    sortOrder = null;
                }
                else throw new IllegalArgumentException(id + INVALID_ID);
        }
        mLoaderManagerFragment.load(id,uri,projection,selection,selectionArgs,sortOrder);
    }


    @Override
    public void onQuickBarButtonClick() {

    }

    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Log.d(TAG,"Loader " + id + " finished loading.  " + cursor.getCount() + " rows returned.");
        switch (id){
            case BUCKET_IDS:
                Log.d(TAG,"BUCKET_IDs finished loading.");
                updateBucketIDs(cursor);
                break;
            default:
                // If this is a bucket cursor id, add it to the map.
                if(isBucketCursor(id)) updateBucketCursor(id,cursor);
                else throw new IllegalArgumentException(id + INVALID_ID);
        }
    }

    private void updateBucketCursor(int id, Cursor cursor) {
        mBucketCursors.put(Long.valueOf(id), cursor);
    }

    private void updateBucketIDs(Cursor cursor) {
        mBucketIds = cursor;
        loadBucketCursors(cursor);
    }

    private boolean isBucketCursor(int id) {
        if(null == mBucketCursors) return false;
        else return mBucketCursors.containsKey(id);
    }

    /**
     * Given a cursor providing a list of unique BUCKET_IDs, load data cursors for
     *  data associated with that BUCKET_ID
     * @param cursor
     */
    private void loadBucketCursors(Cursor cursor) {
        //mBucketCursors = new HashMap<Integer,Cursor>();
        cursor.moveToFirst();
        int bucketIDColumn = cursor.getColumnIndex(BUCKET_ID);
        // For each id in the Bucket_ID cursor, create a new map entry in mBucketCursors
        // with a null Cursor, and initialize the load of the cursor data.
        // The BUCKET_ID will serve as the loader reference id for the associated cursor.
        while(!cursor.isAfterLast()){
            Integer key = cursor.getInt(bucketIDColumn);
            //mBucketCursors.put(key,null);
            load(key);
            cursor.moveToNext();
        }
    }

    private void updateQuickBar() {
        mQuickBarFragment.changeCursor(mBucketIds,BUCKET_ID,BUCKET_DISPLAY_NAME);
    }


    @Override
    public void onLoaderReset(int id) {

    }
}
