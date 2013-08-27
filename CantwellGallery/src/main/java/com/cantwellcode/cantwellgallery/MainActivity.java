package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements QuickBarFragment.QuickBarCallbacks, LoaderManagerFragment.ListenerCallbacks{

    private static final String TAG                     = "MAIN ACTIVITY";
    private static final String LOADER_MANAGER_FRAGMENT = "LOADER_MANAGER_FRAGMENT";
    private static final int    IMAGE_BUCKET_LOADER     = 0x001;
    private static final int    IMAGE_THUMBNAIL_LOADER  = 0x002;
    private static final int    IMAGE_DATA_LOADER       = 0x003;

    private LoaderManagerFragment mLoaderManagerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        // Initialize LoaderManagerFragment
        mLoaderManagerFragment = new LoaderManagerFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mLoaderManagerFragment,LOADER_MANAGER_FRAGMENT).commit();

        load(IMAGE_THUMBNAIL_LOADER);
        load(IMAGE_BUCKET_LOADER);


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

    private void load(int loaderID) {
        Uri uri;
        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder;
        switch (loaderID){
            case IMAGE_BUCKET_LOADER:
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{"DISTINCT " + MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
                selection = null;
                selectionArgs = new String[]{};
                sortOrder = null;
                break;
            case IMAGE_THUMBNAIL_LOADER:
                uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
                projection = new String[]{MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.Thumbnails.IMAGE_ID};
                selection = null;
                selectionArgs = new String[]{};
                sortOrder = null;
                break;
            default:
                throw new IllegalArgumentException("Invalid loader id: " + loaderID);
        }
        mLoaderManagerFragment.load(loaderID,uri,projection,selection,selectionArgs,sortOrder);
    }


    @Override
    public void onQuickBarButtonClick() {

    }

    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Log.d(TAG,"Loader " + id + " finished loading.  " + cursor.getCount() + " rows returned.");
        switch (id){
            case IMAGE_BUCKET_LOADER:
                Log.d(TAG,"Image bucket data finished loading.  Construct new view list");
                break;
            case IMAGE_THUMBNAIL_LOADER:
                Log.d(TAG,"Image thumbnail data finished loading.  Get directory thumbnails");
                break;
            default:
                throw new IllegalArgumentException(id + " is not a valid Loader id.");
        }
    }

    @Override
    public void onLoaderReset(int id) {

    }
}
