package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Chris on 8/22/13.
 */
public class LoaderManagerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ManagedList.Manager{

    private static final String TAG = "LOADER_MANAGER_FRAGMENT";

    public static class DatabaseSubset extends DataSetObserver {
        private static final String TAG = "DATABASE_SUBSET";

        private Cursor mCursor;
        private Map<String,List<String>> mData;
        private List<Observer> mObservers;

        public static interface Observer{
            public void onChanged();
            public void onInvalidated();
        }

        public DatabaseSubset(Cursor database) {
            mCursor = database;
            mCursor.registerDataSetObserver(this);
        }


        private void update() {
        }

        public List<String> get(String columnName){
            return mData.get(columnName);
        }

        @Override
        public void onChanged(){
            Log.d(TAG, this.toString() + ": The data backing this subset has changed.");
            // Update the data
            update();
            // Notify observers
            for (Observer observer : mObservers) observer.onChanged();
        }

        @Override
        public void onInvalidated(){
            Log.d(TAG, this.toString() + ": The data backing this subset has been invalidated.");
            // Remove cursor reference
            mCursor = null;
            // Notify observers
            for (Observer observer : mObservers) observer.onInvalidated();
        }

    }


    public DatabaseSubset getNewDatabaseSubset(int databaseID){
        Cursor database = mCursors.get(databaseID);
        DatabaseSubset subset = new DatabaseSubset(database);
        return null;
    }

    private static class PendingLoader {
        Bundle bundle;
        public PendingLoader(Bundle bundle) {
            this.bundle = bundle;
        }
    }


    public static final String URI              = "URI";
    public static final String PROJECTION       = "PROJECTION";
    public static final String SELECTION        = "SELECTION";
    public static final String SELECTION_ARGS   = "SELECTION_ARGS";
    public static final String SORT_ORDER       = "SORT_ORDER";

    private final Object mOnAttachLock = new Object();
    private boolean mIsAttached = false;
    private Map<String,PendingLoader> mPendingLoaders= new HashMap<String, PendingLoader>();

    private int mLoaderCount;
    private Map<String,Cursor> mCursors;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.d(TAG, "onAttach() thread = " + Thread.currentThread().getName());
        load(mPendingLoaders);
        synchronized (mOnAttachLock){
            mIsAttached = true;
            mOnAttachLock.notifyAll();
        }
     }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLoaderCount = 0;
        mCursors = new HashMap<String, Cursor>();
    }

    public static LoaderManagerFragment newInstance(){
        return new LoaderManagerFragment();
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
        Log.d(TAG, "load() thread = " + Thread.currentThread().getName());
        int id = generateNewLoaderID();
        Bundle bundle = new Bundle();
        bundle.putParcelable(URI,uri);
        bundle.putStringArray(PROJECTION,projection);
        bundle.putString(SELECTION,selection);
        bundle.putStringArray(SELECTION_ARGS,selectionArgs);
        bundle.putString(SORT_ORDER,sortOrder);
        // If the fragment is not attached to an activity yet save the load data.
        if (!mIsAttached) mPendingLoaders.put(String.valueOf(id), new PendingLoader(bundle));
        else getLoaderManager().initLoader(id, bundle, this);
        return id;
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
        // For each ObservableList associated with this loader, update data.

        // Remove instance of cursor.
        swapCursor(String.valueOf(cursorLoader.getId()), cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // For each observable list associated with this loader, invalidate data.

        // Remove instance of cursor
        swapCursor(String.valueOf(cursorLoader.getId()), null);
    }


    /**
     * Private helper function to load any pending loaders
     * @param pendingLoaders
     */
    private void load(Map<String, PendingLoader> pendingLoaders) {
        for (String key : pendingLoaders.keySet()){
            getLoaderManager().initLoader(getLoaderID(key),pendingLoaders.get(key).bundle,this);
            pendingLoaders.remove(key);
        }
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

    /**
     * Helper function for converting a String value corresponding to a map key into an int loader id
     * @param loaderKey
     * @return
     */
    private static int getLoaderID(String loaderKey){
        return Integer.valueOf(loaderKey);
    }

    /**
     * Helper function for converting an int loader id into a String value to be used as a map key
     * @param loaderID
     * @return
     */
    private static String getLoaderMapKey(int loaderID){
        return String.valueOf(loaderID);
    }
}
