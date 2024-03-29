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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Daniel on 8/13/13.
 */
public class ContentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ContentFragment";

    // Default values
    private static final String EMPTY_NAME              = "No Album Selected";
    private static final String DEFAULT_BUCKET_NAME     = "Camera";

    // Bundle tags
    private static final String URI             = "URI";
    private static final String PROJECTION      = "PROJECTION";
    private static final String SELECTION       = "SELECTION";
    private static final String SELECTION_ARGS  = "SELECTION_ARGS";
    private static final String SORT_ORDER      = "SORT_ORDER";

    // Exception Strings
    private static final String INVALID_LOADER_ID       = "Invalid Loader ID.";

    // Database Columns
    private static final String _ID                     = MediaStore.Images.Media._ID;
    private static final String BUCKET_ID               = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_DISPLAY_NAME     = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private static final String IMAGE_DATA              = MediaStore.Images.Media.DATA;
    private static final String DEFAULT_SORT_ORDER      = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

    // Bucket Images loader args
    private static final Uri        IMAGES_URI              = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String[]   IMAGES_PROJECTION       = {_ID,BUCKET_DISPLAY_NAME,IMAGE_DATA};
    private static final String     IMAGES_SELECTION        = BUCKET_DISPLAY_NAME + "=?";
    private static final String[]   IMAGES_SELECTION_ARGS   = {DEFAULT_BUCKET_NAME};
    private static final String     IMAGES_SORT_ORDER       = DEFAULT_SORT_ORDER;

    private ListView                mListView;
    private ImageCursorAdapter      mListAdapter;
    private TextView                mTextView;
    private String                  mName;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        load(DEFAULT_BUCKET_NAME);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mListAdapter = new ImageCursorAdapter(getActivity(),null,R.layout.content_pane_item,
                R.id.contentPaneItemImage,_ID,_ID);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.content_pane, container, false);

        mTextView = (TextView) root.findViewById(R.id.contentPaneHeader);
        if (mName == null) mTextView.setText(EMPTY_NAME);
        else mTextView.setText(mName);

        mListView = (ListView) root.findViewById(R.id.contentPaneListView);
        mListView.setAdapter(mListAdapter);
        setupDrag(mListView);

        return root;
    }


    /**
     * Constructs load parameters to load a cursor from the static IMAGES_URI
     * with data for images in the supplied directory name
     * @param name : name of directory being loaded
     */
    private void load(String name) {
        mName = name;

        String[] selectionArgs = {name};
        Bundle bundle = new Bundle();

        bundle.putParcelable(URI,IMAGES_URI);
        bundle.putStringArray(PROJECTION, IMAGES_PROJECTION);
        bundle.putString(SELECTION, IMAGES_SELECTION);
        bundle.putStringArray(SELECTION_ARGS, selectionArgs);
        bundle.putString(SORT_ORDER,IMAGES_SORT_ORDER);
        LoaderManager manager = getLoaderManager();
        // If this is the first load, call initLoader.
        // Otherwise restartLoader.  Old data will be discarded.
        if (manager.getLoader(0) == null) manager.initLoader(0,bundle,this);
        else manager.restartLoader(0,bundle,this);
    }

    public void changeDirectory(String name){
        load(name);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri          uri           = bundle.getParcelable(URI);
        String[]     projection    = bundle.getStringArray(PROJECTION);
        String       selection     = bundle.getString(SELECTION);
        String[]     selectionArgs = bundle.getStringArray(SELECTION_ARGS);
        String       sortOrder     = bundle.getString(SORT_ORDER);

        CursorLoader loader        = new CursorLoader(this.getActivity(),uri,projection,selection,selectionArgs,sortOrder);

        return loader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (mTextView != null)mTextView.setText(mName);
        mListAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if (mTextView != null)mTextView.setText(EMPTY_NAME);
        mListAdapter.changeCursor(null);
    }


    /*********************************
     *        DRAG  AND  DROP        *
     *********************************/

    private void setupDrag(final ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                long id               = listView.getAdapter().getItemId(position);
                final String label    = Long.toString(id);
                final String textData = label + ":" + position;
                ClipData data         = ClipData.newPlainText(label, textData);

                view.startDrag(data, new MyDragShadowBuilder(view), mListAdapter.getItem(position), 0);

                return true;
            }
        });
    }

    private void setupFling(final ListView listView) {
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });
    }
}
