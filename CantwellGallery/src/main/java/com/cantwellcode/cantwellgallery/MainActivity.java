package com.cantwellcode.cantwellgallery;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String   TAG                = "CANTWELL_GALLERY";
    private static final Uri      IMAGE_URI          = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String   IMAGE_DIRECTORY_ID = MediaStore.Images.ImageColumns._ID;
    private static final String   IMAGE_BUCKET_ID    = MediaStore.Images.ImageColumns.BUCKET_ID;
    private static final String   IMAGE_DIRECTORY    = MediaStore.Images.ImageColumns.DATA;
    private static final String   IMAGE_DATA         = MediaStore.Images.Media.DATA;
    private static final String   IMAGE_ID           = MediaStore.Images.Media._ID;
    private static final String   IMAGE_THUMBNAIL    = MediaStore.Images.Media.MINI_THUMB_MAGIC;
    private static final String   IMAGE_SORT_ORDER   = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
    private static final String[] PROJECTION         = {IMAGE_DIRECTORY_ID,IMAGE_DIRECTORY,IMAGE_ID,IMAGE_DATA,IMAGE_THUMBNAIL};
    private static final String   SELECTION          = null;
    private static final String[] SELECTION_ARGS     = {};
    private static final int      IMAGE_FILE_LOADER  = 0;

    private ListView  mQuickBar;
    private Cursor    mCursor;

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID){
            case IMAGE_FILE_LOADER:
                return new CursorLoader(this,IMAGE_URI,PROJECTION,SELECTION,SELECTION_ARGS, IMAGE_SORT_ORDER);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG,"MediaStore Query complete. " + cursor.getCount() + " files found.");
        final int MAX;
        if(cursor.getCount() >= 10) MAX = 10;
        else MAX = cursor.getCount();
        if(!cursor.isFirst()) cursor.moveToFirst();
        for (int i=0; i<MAX; ++i){
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        getSupportLoaderManager().initLoader(IMAGE_FILE_LOADER,null,this);

//        mQuickBar = (ListView) this.findViewById(R.id.quickBar);
//        AlbumListAdapter adapter = new AlbumListAdapter(this,new ArrayList<File>(),0);
//        mQuickBar.setAdapter(adapter);

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

    private void initMedia() {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA}; // Find image directories
        String selectionClause = null;
        String[] selectionArgs = {""};
//        Cursor cursor = getContentResolver().query(fileUri,projection,);
    }

/*    private static SortedSet<Album> getExternalStorageAlbums(Context context){
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection =
                {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.BUCKET_ID,
                        MediaStore.Images.ImageColumns.DATA};
        String selection = null;
        String[] selectionArgs = {};
        String sortOrder = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor;
        try{
            cursor = resolver.query(imageUri,projection,selection,selectionArgs,sortOrder);
        }catch(RuntimeException e){
            Log.e(TAG,e.getMessage());
            return null;
        }
        if (null==cursor){
            Log.e(TAG,"MediaStore Query Failed:"
            + " context=\"" + context.toString() + "\""
            + " imageURI=\"" + imageUri.toString() + "\""
            + " projection=\"" + projection.toString() + "\""
            + " selection=\"" + selection.toString() + "\""
            + " selectionArgs=\"" + selectionArgs.toString() + "\""
            + " sortOdrer=\"" + sortOrder.toString() + "\"");
            return null;
        }else if (cursor.getCount() < 1){
            Log.d(TAG,"No Media Found");
            cursor.close();
            return new TreeSet<Album>();
        }else{
            String[] columnNames = cursor.getColumnNames();
            int numRows = cursor.getCount();
            Log.d(TAG,"Found " + numRows + " rows satisfying the query for columns: " + columnNames.toString());
            SortedSet<Album> albums = new TreeSet<Album>();
            for (int i=0; i<cursor.getCount(); ++i){

            }
            return albums;
        }
    }
*/

    private void onAddDirectoryButtonClick(){

    }

    
}
