package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 8/22/13.
 */
public class LoaderManagerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ManagedList.Manager{
    private static final String TAG = "LOADER_MANAGER_FRAGMENT";

    public static final String URI              = "URI";
    public static final String PROJECTION       = "PROJECTION";
    public static final String SELECTION        = "SELECTION";
    public static final String SELECTION_ARGS   = "SELECTION_ARGS";
    public static final String SORT_ORDER       = "SORT_ORDER";

    public interface Callbacks{
    }
    private static class PendingLoad{
        int id;
        Bundle bundle;

        public PendingLoad(int id, Bundle bundle) {
            this.id = id;
            this.bundle = bundle;
        }
    }

    private int mLoaderCount;
    private Map<String,Cursor> mCursors;
    private boolean mIsAttached = false;
    private List<PendingLoad> mPendingLoads = new ArrayList<PendingLoad>();

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mIsAttached = true;
        load(mPendingLoads);
//        mPendingLoads.clear();
     }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLoaderCount = 0;
        mCursors = new HashMap<String, Cursor>();
    }

    /**
     * Called by host activity to load data at the provided uri.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public int load(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        int id = generateNewLoaderID();
        Bundle bundle = new Bundle();
        bundle.putParcelable(URI,uri);
        bundle.putStringArray(PROJECTION,projection);
        bundle.putString(SELECTION,selection);
        bundle.putStringArray(SELECTION_ARGS,selectionArgs);
        bundle.putString(SORT_ORDER,sortOrder);
        // If the fragment is not attached to an activity yet save the load data.
        if (!mIsAttached)mPendingLoads.add(new PendingLoad(id,bundle));
        else getLoaderManager().initLoader(id, bundle, this);
        return id;
    }

    /**
     * Private helper function to load more than one data set
     * @param pendingLoads
     */
    private void load(List<PendingLoad> pendingLoads) {
        for (PendingLoad p : pendingLoads) getLoaderManager().initLoader(p.id,p.bundle,this);
    }

    /**
     * Generates a new id for a loader
     * @return
     */
    private int generateNewLoaderID() {
        int id = mLoaderCount;
        ++mLoaderCount;
        return id;
    }

    /**
     * Retrieves the existing Cursor with Key Value: key and swaps it for the new Cursor,
     * returning the old.
     * @param key
     * @param cursor
     * @return
     */
    private Cursor swapCursor(String key, Cursor cursor) {
        Cursor old = mCursors.get(key);
        if(null == cursor) mCursors.remove(key);
        else mCursors.put(key,cursor);
        return old;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
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
        Log.d(TAG, "Load of " + cursor.toString() + " complete.  " + cursor.getCount() + " rows found.");
        swapCursor(String.valueOf(cursorLoader.getId()), cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        swapCursor(String.valueOf(cursorLoader.getId()), null);
    }
}
