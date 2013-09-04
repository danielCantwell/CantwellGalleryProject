package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Daniel on 8/13/13.
 */
public class ContentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "ContentFragment";

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
    private static final String DEFAULT_BUCKET_NAME     = "Camera";

    // Bucket Images loader args
    private static final Uri        IMAGES_URI              = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String[]   IMAGES_PROJECTION       = {_ID,BUCKET_DISPLAY_NAME,IMAGE_DATA};
    private static final String     IMAGES_SELECTION        = BUCKET_DISPLAY_NAME + "=?";
    private static final String[]   IMAGES_SELECTION_ARGS   = {DEFAULT_BUCKET_NAME};
    private static final String     IMAGES_SORT_ORDER       = DEFAULT_SORT_ORDER;

    private ListView mListView;
    private ImageCursorAdapter mListAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        load();
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

        mListView = (ListView) root.findViewById(R.id.contentPaneListView);
        mListView.setAdapter(mListAdapter);

        return root;
    }


    private void load() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Bundle bundle = new Bundle();
        bundle.putParcelable(URI,IMAGES_URI);
        bundle.putStringArray(PROJECTION, IMAGES_PROJECTION);
        bundle.putString(SELECTION, IMAGES_SELECTION);
        bundle.putStringArray(SELECTION_ARGS, IMAGES_SELECTION_ARGS);
        bundle.putString(SORT_ORDER,IMAGES_SORT_ORDER);
        getLoaderManager().initLoader(0,bundle,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = bundle.getParcelable(URI);
        String[] projection = bundle.getStringArray(PROJECTION);
        String selection = bundle.getString(SELECTION);
        String[] selectionArgs = bundle.getStringArray(SELECTION_ARGS);
        String sortOrder = bundle.getString(SORT_ORDER);
        CursorLoader loader = new CursorLoader(this.getActivity(),uri,projection,selection,selectionArgs,sortOrder);
        return loader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.changeCursor(null);
    }
}
