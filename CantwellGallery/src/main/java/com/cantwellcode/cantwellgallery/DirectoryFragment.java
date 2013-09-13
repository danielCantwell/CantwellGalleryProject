package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ClipData;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Daniel on 8/13/13.
 */
public class DirectoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "DirectoryFragment";

    // Bundle tags
    private static final String URI             = "URI";
    private static final String PROJECTION      = "PROJECTION";
    private static final String SELECTION       = "SELECTION";
    private static final String SELECTION_ARGS  = "SELECTION_ARGS";
    private static final String SORT_ORDER      = "SORT_ORDER";
    private static final String LIST_POSITION   = "LIST_POSITION";

    // Exception Strings
    private static final String INVALID_LOADER_ID       = "Invalid Loader ID.";

    // Database Columns
    private static final String _ID                     = MediaStore.Images.Media._ID;
    private static final String BUCKET_ID               = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_DISPLAY_NAME     = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private static final String DEFAULT_SORT_ORDER      = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

    // Loader Args
    // The following will produce a cursor that contains only the first row of images for each
    //  unique bucket id.  This only works because the underlying database for MediaStore is SQLite
    // This produces the SQLite query string:
    //      SELECT %BUCKET_PROJECTION FROM (%URI) WHERE (1) GROUP BY (%BUCKET_ID) ORDER BY (%DEFAULT_SORT_ORDER)
    // WHERE (%s)
    private static final Uri        BUCKET_URI              = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String     MIN_ID                  = "MIN(" + _ID + ")";
    private static final String[]   BUCKET_PROJECTION       = {MIN_ID,BUCKET_ID,BUCKET_DISPLAY_NAME};
    private static final String     BUCKET_SELECTION        = "1) GROUP BY (" + BUCKET_ID;
    private static final String[]   BUCKET_SELECTION_ARGS   = null;
    private static final String     BUCKET_SORT_ORDER       = DEFAULT_SORT_ORDER;

    // Loader IDs
    private static final int        DEFAULT_LOADER          = 0x001;

    // Private member variables
    private GridView                mGridView;
    private QuickBarCursorAdapter   mAdapter;
    private Callbacks               mListener;

    public interface Callbacks{
        boolean onDirectoryPaneItemSelected(long id, String name);
    }

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
            throw new ClassCastException(activity.toString() + " must implement QuickBarCallbacks");
        }
        load();
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
        mAdapter = new QuickBarCursorAdapter(getActivity(),null,BUCKET_ID,BUCKET_DISPLAY_NAME,MIN_ID);
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


    /**
     * Construct load parameters and initialize loader
     */
    private void load() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(URI,BUCKET_URI);
        bundle.putStringArray(PROJECTION, BUCKET_PROJECTION);
        bundle.putString(SELECTION, BUCKET_SELECTION);
        bundle.putStringArray(SELECTION_ARGS, BUCKET_SELECTION_ARGS);
        bundle.putString(SORT_ORDER,BUCKET_SORT_ORDER);
        getLoaderManager().initLoader(DEFAULT_LOADER,bundle,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.d(TAG, "onCreateLoader() thread = " + Thread.currentThread().getName());
        Uri uri = bundle.getParcelable(URI);
        String[] projection    = bundle.getStringArray(PROJECTION);
        String   selection     = bundle.getString(SELECTION);
        String[] selectionArgs = bundle.getStringArray(SELECTION_ARGS);
        String   sortOrder     = bundle.getString(SORT_ORDER);
        CursorLoader loader = new CursorLoader(this.getActivity(),uri,projection,selection,selectionArgs,sortOrder);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, BUCKET_ID + " cursor finished loading. " + cursor.getCount() + " rows found.");
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    private void setupListItemSelect(GridView gridView) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                int index     = cursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME);
                String name   = cursor.getString(index);

                Toast directorySelectedToast = Toast.makeText(getActivity(),"Album " + name + " selected", Toast.LENGTH_SHORT);
                directorySelectedToast.show();
                mListener.onDirectoryPaneItemSelected(id,name);
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
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                ClipData data = ClipData.newPlainText(ClipDataLabels.BUCKET.toString(),String.valueOf(l));
                BucketViewHolder holder = (BucketViewHolder) view.getTag();
                // Pass view as the local state object.
                view.startDrag(data, new MyDragShadowBuilder(view), view, 0);
                return true;
            }
        });
    }


}
