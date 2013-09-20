package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Daniel on 8/13/13.
 */
public class DirectoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DatabaseMaster {
    private static final String TAG = "DirectoryFragment";

    // Bundle tags
    private static final String URI             = "URI";
    private static final String PROJECTION      = "PROJECTION";
    private static final String SELECTION       = "SELECTION";
    private static final String SELECTION_ARGS  = "SELECTION_ARGS";
    private static final String SORT_ORDER      = "SORT_ORDER";

    // Exception Strings
    private static final String INVALID_LOADER_ID       = "Invalid Loader ID.";

    // Database Columns
    private static final Uri    BUCKET_URI              = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static final String IMAGE_ID                = MediaStore.Images.Media._ID;
    private static final String IMAGE_DISPLAY_NAME      = MediaStore.Images.Media.DISPLAY_NAME;
    private static final String IMAGE_DATA              = MediaStore.Images.Media.DATA;

    private static final String MIN_ID                  = "MIN(" + IMAGE_ID + ")";
    private static final String BUCKET_ID               = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_DISPLAY_NAME     = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private static final String BUCKET_DATA             = MediaStore.Images.ImageColumns.DATA;
    private static final String DEFAULT_SORT_ORDER      = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

    // Loader Projections
    private static final String[] ALL_BUCKETS_PROJECTION    = {MIN_ID, BUCKET_ID,BUCKET_DISPLAY_NAME,BUCKET_DATA};
    private static final String[] BUCKET_CONTENT_PROJECTION = {BUCKET_ID,BUCKET_DISPLAY_NAME,IMAGE_ID,IMAGE_DISPLAY_NAME,IMAGE_DATA};

    // Loader IDs
    private static final int        ALL_BUCKETS_LOADER      = 0x001;
    private static final int        BUCKET_CONTENT_LOADER   = 0x002;

    private static final String     INITIAL_BUCKET_NAME     = "Camera";

    // Private member variables
    private GridView                mGridView;
    private BucketCursorAdapter     mAdapter;
    private Callbacks               mListener;
    private ContentResolver         mContentResolver;


    /**
     * Called when the fragment is attached to the host activity.
     * Ensures the host activity implements the fragment callbacks and sets the mListener member.
     * @param activity: The host activity
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mListener = (Callbacks) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement DatabaseMaster.Callbacks");
        }
        mContentResolver = activity.getContentResolver();
        loadAllBuckets();
        loadBucketContents(INITIAL_BUCKET_NAME);
    }
    /**
     * Place all initialization that should be retained across config changes in here.
     * This will not be called again because we use setRetainInstance(true).
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAdapter = new BucketCursorAdapter(getActivity(),null,BUCKET_ID,BUCKET_DISPLAY_NAME,MIN_ID);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view for this fragment from the associated xml file.
        final View root = inflater.inflate(R.layout.directory_pane, container, false);
        mGridView = (GridView) root.findViewById(R.id.directoryPaneGridView);
        mGridView.setAdapter(mAdapter);

        setupDrag(mGridView);
        setupListItemSelect(mGridView);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    /**************************************
     *        DATABASE LOADING            *
     **************************************/


    /**
     * Construct load parameters for all buckets and initialize loader
     */
    private void loadAllBuckets() {
        // The following will produce a cursor that contains only the first row of images for each
        //  unique bucket id.  This only works because the underlying database for MediaStore is SQLite
        // This produces the SQLite query string:
        //      SELECT %BUCKET_PROJECTION FROM (%URI) WHERE (1) GROUP BY (%BUCKET_ID) ORDER BY (%DEFAULT_SORT_ORDER)
        // WHERE (%s)
        final Uri       uri             = BUCKET_URI;
        final String[]  projection      = ALL_BUCKETS_PROJECTION;
        final String    selection       = "1) GROUP BY (" + BUCKET_ID;
        final String[]  selectionArgs   = null;
        final String    sortOrder       = DEFAULT_SORT_ORDER;

        Bundle loaderArgs = getLoaderArgs(uri, projection, selection, selectionArgs, sortOrder);
        load(ALL_BUCKETS_LOADER,loaderArgs);
    }

    /**
     * Construct load parameters for the specified bucket and initialize loader
     * @param bucketID
     */
    private void loadBucketContents(long bucketID){
        final Uri       uri             = BUCKET_URI;
        final String[]  projection      = BUCKET_CONTENT_PROJECTION;
        final String    selection       = BUCKET_ID + "=?";
        final String[]  selectionArgs   = {String.valueOf(bucketID)};
        final String    sortOrder       = DEFAULT_SORT_ORDER;

        Bundle loaderArgs = getLoaderArgs(uri, projection, selection, selectionArgs, sortOrder);
        load(BUCKET_CONTENT_LOADER, loaderArgs);
    }

    /**
     * Construct load parameters for the specified bucket and initialize loader
     * @param bucketDisplayName
     */
    private void loadBucketContents(String bucketDisplayName){
        final Uri       uri             = BUCKET_URI;
        final String[]  projection      = BUCKET_CONTENT_PROJECTION;
        final String    selection       = BUCKET_DISPLAY_NAME + "=?";
        final String[]  selectionArgs   = {String.valueOf(bucketDisplayName)};
        final String    sortOrder       = DEFAULT_SORT_ORDER;

        Bundle loaderArgs = getLoaderArgs(uri, projection, selection, selectionArgs, sortOrder);
        load(BUCKET_CONTENT_LOADER,loaderArgs);
    }

    /**
     * Load or restart the specified loader with the supplied args
     * @param loaderID
     * @param loaderArgs
     */
    private void load(int loaderID, Bundle loaderArgs){
        LoaderManager lm = getLoaderManager();
        if (lm.getLoader(loaderID)==null) lm.initLoader(loaderID,loaderArgs,this);
        else lm.restartLoader(loaderID,loaderArgs,this);
    }

    /**
     * Construct loaderArgs Bundle
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    private static Bundle getLoaderArgs(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(URI,uri);
        bundle.putStringArray(PROJECTION, projection);
        bundle.putString(SELECTION, selection);
        bundle.putStringArray(SELECTION_ARGS, selectionArgs);
        bundle.putString(SORT_ORDER,sortOrder);
        return bundle;
    }

    /**
     * Called by the LoaderManager to create a loader with the given id and loaderArgs
     * @param id
     * @param loaderArgs
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle loaderArgs) {
        Uri uri = loaderArgs.getParcelable(URI);
        String[] projection    = loaderArgs.getStringArray(PROJECTION);
        String   selection     = loaderArgs.getString(SELECTION);
        String[] selectionArgs = loaderArgs.getStringArray(SELECTION_ARGS);
        String   sortOrder     = loaderArgs.getString(SORT_ORDER);
        CursorLoader loader = new CursorLoader(this.getActivity(),uri,projection,selection,selectionArgs,sortOrder);
        return loader;
    }

    /**
     * Called by the LoaderManager when a Loader has finished loading its data.
     * @param cursorLoader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        final int id = cursorLoader.getId();
        switch (id){
            case ALL_BUCKETS_LOADER:
                processAllBucketsLoadFinished(cursorLoader,cursor);
                break;
            case BUCKET_CONTENT_LOADER:
                processBucketContentLoadFinished(cursorLoader,cursor);
                break;
            default:
                break;
        }
    }

    private void processAllBucketsLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    private void processBucketContentLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        final int index = cursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME);
        final String label = cursor.getString(index);
        mListener.changeContentLabelAndCursor(label, cursor);
    }

    /**
     * Called by the LoaderManager when a Loader is reset
     * @param cursorLoader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        final int id = cursorLoader.getId();
        switch (id){
            case ALL_BUCKETS_LOADER:
                processAllBucketsLoaderReset(cursorLoader);
                break;
            case BUCKET_CONTENT_LOADER:
                processBucketContentLoaderReset(cursorLoader);
                break;
            default:
                break;
        }
    }

    private void processAllBucketsLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    private void processBucketContentLoaderReset(Loader<Cursor> cursorLoader) {
        mListener.changeContentLabelAndCursor(null, null);
    }

    /**************************************
     *        DATABASE MODIFICATION       *
     **************************************/

    /**
     * Update the database so the specified image belongs to the specified bucket.
     * The cursors must already be pointing to their respective data.
     *
     * @param imageCursor
     * @param bucketCursor
     * @return
     */
    public boolean moveImageToBucket(Cursor imageCursor, Cursor bucketCursor){
        final int imagePathIndex;
        final int bucketPathIndex;
        final int bucketIDIndex;
        final int bucketDisplayNameIndex;
        final int imageIDIndex;
        try {
            imagePathIndex          = imageCursor.getColumnIndexOrThrow(IMAGE_DATA);
            bucketPathIndex         = bucketCursor.getColumnIndexOrThrow(BUCKET_DATA);
            bucketIDIndex           = bucketCursor.getColumnIndexOrThrow(BUCKET_ID);
            bucketDisplayNameIndex  = bucketCursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME);
            imageIDIndex            = imageCursor.getColumnIndexOrThrow(IMAGE_ID);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
        String imagePath = imageCursor.getString(imagePathIndex);
        String bucketPath = bucketCursor.getString(bucketPathIndex);

        File bucket             = new File(bucketPath);
        File bucketDirectory    = new File(bucket.getParent());
        File image              = new File(imagePath);
        if(!bucketDirectory.canWrite() || !image.canWrite()) return false;
        if (!image.renameTo(new File(bucketDirectory,image.getName()))) return false;

        // The file rename was successful, update the mediastore
        MediaScannerConnection.scanFile(getActivity(),new String[]{image.toString()},null,new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Log.d(TAG,"Media scan complete.");
            }
        });

        long imageID                = imageCursor.getLong(imageIDIndex);
        long bucketID               = bucketCursor.getLong(bucketIDIndex);
        String bucketDisplayName    = bucketCursor.getString(bucketDisplayNameIndex);

        // Construct values to update mediastore with

/*        ContentValues values        = new ContentValues();
        values.put(BUCKET_ID,bucketID);
        values.put(BUCKET_DISPLAY_NAME,bucketDisplayName);

        // Update mediastore with the new values
        ContentResolver cr      = getActivity().getContentResolver();
        Uri uri                 = ContentUris.withAppendedId(BUCKET_URI,imageID);
        String selection        = IMAGE_ID + "=?";
        String[] selectionArgs  = {String.valueOf(imageID)};
        int r;
        try {
            r = cr.update(uri,values,null,null);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
            return false;
        }

        Log.d(TAG,"Moving image " + imagePath + " to bucket " + bucketPath);
        */
        return true;
    }

    public boolean createNewBucketFromImage(long imageID, String bucketName){
        return true;
    }

    /**
     * Sets up database bucket content when a bucket is selected.
     * @param gridView
     */
    private void setupListItemSelect(GridView gridView) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                loadBucketContents(id);
                Toast directorySelectedToast = Toast.makeText(getActivity(),"Album " + id + " selected", Toast.LENGTH_SHORT);
                directorySelectedToast.show();
            }
        });
    }


    /*********************************
     *        DRAG  AND  DROP        *
     *********************************/

    /**
     * Used to allow items in the listView to be dragged.
     *
     * @param gridView - used to override OnItemLongClickListener()
     */
    private void setupDrag(GridView gridView) {
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Initiate a BUCKET drag event, passing the BUCKET_ID in ClipData
                ClipData data = ClipData.newPlainText(ClipDataLabels.BUCKET.toString(),String.valueOf(id));
                // Pass a BucketLocalState containing the ViewHolder information for the curent view
                //      as well as the cursor pointing to the associated item.
                BucketLocalState localState = new BucketLocalState((BucketViewHolder)view.getTag(),(Cursor)mAdapter.getItem(position));
                view.startDrag(data, new MyDragShadowBuilder(view), localState, 0);
                return true;
            }
        });
    }


}
