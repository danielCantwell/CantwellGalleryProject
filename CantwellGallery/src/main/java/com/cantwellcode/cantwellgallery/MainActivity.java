package com.cantwellcode.cantwellgallery;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements QuickBarFragment.QuickBarCallbacks{

    private static final String TAG                     = "MAIN ACTIVITY";
    private static final String LOADER_MANAGER_FRAGMENT = "LOADER_MANAGER_FRAGMENT";
    private static final String _ID                     = MediaStore.Images.Media._ID;
    private static final String BUCKET_ID               = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_DISPLAY_NAME     = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;

    private static final int    IMAGE_BUCKET_DATA       = 0x001;
    private static final int    IMAGE_THUMBNAIL_DATA    = 0x002;

    private QuickBarFragment mQuickBarFragment;
    private Map<Integer,Cursor> mCursors;

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        FragmentManager fm = getSupportFragmentManager();
        // Initialize QuickBarFragment
        mQuickBarFragment = (QuickBarFragment) fm.findFragmentById(R.id.quickBarFragment);

        /*
        // Initialize LoaderManagerFragment
        mLoaderManagerFragment = new LoaderManagerFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mLoaderManagerFragment,LOADER_MANAGER_FRAGMENT).commit();

        load(IMAGE_THUMBNAIL_DATA);
        load(IMAGE_BUCKET_DATA);
*/
//        imageView = (ImageView) findViewById(R.id.photoPaneImageView);
//        setupDrop(imageView);
//        setupDrag(imageView);

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

    @Override
    public void onQuickBarButtonClick() {

    }

/*
    private void load(int loaderID) {
        Uri uri;
        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder;
        switch (loaderID){
            case IMAGE_BUCKET_DATA:
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{"DISTINCT "+BUCKET_ID,BUCKET_DISPLAY_NAME};
                selection = null;
                selectionArgs = new String[]{};
                sortOrder = null;
                break;
            case IMAGE_THUMBNAIL_DATA:
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
    public void onLoadFinished(int id, Cursor cursor) {
        Log.d(TAG,"Loader " + id + " finished loading.  " + cursor.getCount() + " rows returned.");
        switch (id){
            case IMAGE_BUCKET_DATA:
                Log.d(TAG,"Image bucket data finished loading.  Construct new view list");
                mCursors.put(IMAGE_BUCKET_DATA,cursor);
                updateQuickBar();
                break;
            case IMAGE_THUMBNAIL_DATA:
                Log.d(TAG,"Image thumbnail data finished loading.  Get directory thumbnails");
                mCursors.put(IMAGE_THUMBNAIL_DATA, cursor);
                break;
            default:
                throw new IllegalArgumentException(id + " is not a valid Loader id.");
        }
    }

    private void updateQuickBar() {

    }


    @Override
    public void onLoaderReset(int id) {

    }

    /*********************************
     *        DRAG  AND  DROP        *
     *********************************/

    /**
     * Used to allow items in the listView to be dragged.
     *
     * @param view - used to override OnLongClickListener()
     */
    private void setupDrag(View view) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String title = "photoNameWillGoHere";
                final String textData = title;
                ClipData data = ClipData.newPlainText(title, textData);
                view.startDrag(data, new MyDragShadowBuilder(view), null, 0);
                return true;
            }
        });
    }

    private void setupDrop(View v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                switch (dragEvent.getAction()) {

                    // When a view drag starts, imageView turns blue
                    case DragEvent.ACTION_DRAG_STARTED:
                        view.setBackgroundColor(Color.BLUE);
                        return processDragStarted(dragEvent);

                    // When the view is being held over the imageView, the imageView turns blue
                    case DragEvent.ACTION_DRAG_ENTERED:
                        view.setBackgroundColor(Color.MAGENTA);
                        break;

                    // When the view is exited, but not dropped on the imageView, the imageView turns yellow
                    case DragEvent.ACTION_DRAG_EXITED:
                        view.setBackgroundColor(Color.YELLOW);
                        break;

                    // When the view is dropped on the imageView, process the drop
                    case DragEvent.ACTION_DROP:
                        return processDrop(view, dragEvent);

                }
                return false;
            }
        });
    }

    /**
     * Process the drop event
     *
     * @param event
     * @return
     */
    private boolean processDrop(View view, DragEvent event) {

        view.setBackground(getResources().getDrawable(R.drawable.ic_launcher));

        /*
        ClipData data = event.getClipData();
        if (data != null) {
            if (data.getItemCount() > 0) {
                ClipData.Item item = data.getItemAt(0);
                String textData = (String) item.getText();
                String[] parts = textData.split(":");
                int index = Integer.parseInt(parts[1]);
                String listItem = parts[0];
                //updateViewsAfterDropComplete(listItem, index);
                return true;
            }
        }
        */
        return true;
    }

    /**
     * Check if this is the drag operation you want. There might be other
     * clients that would be generating the drag event. Here, we check the mime
     * type of the data
     *
     * @param event
     * @return
     */
    private boolean processDragStarted(DragEvent event) {
        ClipDescription clipDesc = event.getClipDescription();
        if (clipDesc != null) {
            return clipDesc.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        }
        return false;
    }
}
