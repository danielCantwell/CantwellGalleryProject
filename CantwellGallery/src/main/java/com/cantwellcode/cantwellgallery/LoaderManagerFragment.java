package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 8/22/13.
 */
public class LoaderManagerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG             = "LOADER_MANAGER_FRAGMENT";
    private static final String URI             = "URI";
    private static final String PROJECTION      = "PROJECTION";
    private static final String SELECTION       = "SELECTION";
    private static final String SELECTION_ARGS  = "SELECTION_ARGS";
    private static final String SORT_ORDER      = "SORT_ORDER";

    private Object mOnAttachLock = new Object();
    private boolean mIsAttached = false;
    private Map<Integer,PendingLoader> mPendingLoaders= new HashMap<Integer, PendingLoader>();
    private ListenerCallbacks mListener;

    /**
     * Helper class for storing data to be loaded after the fragment
     * is attached to the host activity
     */
    private static class PendingLoader {
        Bundle bundle;
        public PendingLoader(Bundle bundle) {
            this.bundle = bundle;
        }
    }

    public interface ListenerCallbacks{
        public void onLoadFinished(int id, Cursor cursor);
        public void onLoaderReset(int id);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.d(TAG, "onAttach() thread = " + Thread.currentThread().getName());
        try {
            mListener = (ListenerCallbacks) activity;
        }catch (ClassCastException e){
            Log.d(TAG, "Host activity must implement ListenerCallbacks.");
        }
        // Load any pending loaders
        load(mPendingLoaders);
        mIsAttached = true;
     }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public static LoaderManagerFragment newInstance(){
        return new LoaderManagerFragment();
    }

    /**
     * Called by host activity to load data at the provided uri.
     * @param id : id to associate with this loader
     * @param uri : uri to be queried
     * @param projection : columns to be returned
     * @param selection : selection criteria
     * @param selectionArgs : args to pass to selection strings of the type "value=?"
     * @param sortOrder : sort order to return
     */
    public void load(int id, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Log.d(TAG, "load() thread = " + Thread.currentThread().getName());
        Bundle bundle = new Bundle();
        bundle.putParcelable(URI,uri);
        bundle.putStringArray(PROJECTION,projection);
        bundle.putString(SELECTION,selection);
        bundle.putStringArray(SELECTION_ARGS,selectionArgs);
        bundle.putString(SORT_ORDER,sortOrder);
        // If the fragment is not attached to an activity yet save the load data.
        if (!mIsAttached) mPendingLoaders.put(id, new PendingLoader(bundle));
        // else initialize loader
        else getLoaderManager().initLoader(id, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.d(TAG, "onCreateLoader() thread = " + Thread.currentThread().getName());
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
        mListener.onLoadFinished(cursorLoader.getId(),cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


    /**
     * Private helper function to load any pending loaders
     * @param pendingLoaders
     */
    private void load(Map<Integer, PendingLoader> pendingLoaders) {
        for (Integer key : pendingLoaders.keySet()){
            getLoaderManager().initLoader(key,pendingLoaders.get(key).bundle,this);
        }
        pendingLoaders.clear();
    }

}
