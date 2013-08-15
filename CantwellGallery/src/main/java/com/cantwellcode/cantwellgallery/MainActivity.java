package com.cantwellcode.cantwellgallery;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "CANTWELL_GALLERY";
    private static final Uri URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String[] PROJECTION = {""};
    private static final String SELECTION = null;
    private static final String[] SELECTION_ARGS = {};
    private static final String SORT_ORDER = null;
    private static final int MEDIA_FILE_LOADER = 0;

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID){
            case MEDIA_FILE_LOADER:
                return new CursorLoader(this,URI,PROJECTION,SELECTION,SELECTION_ARGS,SORT_ORDER);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private ListView mQuickBar;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        getLoaderManager().initLoader(MEDIA_FILE_LOADER,null,this);

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
