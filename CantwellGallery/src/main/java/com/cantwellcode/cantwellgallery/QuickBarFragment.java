package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 8/16/13.
 */
public class QuickBarFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "QuickBarFragment";
    private static final int    CURSOR_LOADER = 0;

    static class QuickBarAdapter extends CursorAdapter {

        private List<String> mItemIDs;

        public QuickBarAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public QuickBarAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public int getCount() {
            return mItemIDs.size();
        }

        @Override
        public Object getItem(int i) {
            return mItemIDs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            return null;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

    static class CursorLoaderArgs{
        Uri uri;
        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder;

        public CursorLoaderArgs(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            this.uri = uri;
            this.projection = projection;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
            this.sortOrder = sortOrder;
        }
    }


    public interface QuickBarCallbacks{
        /**
         * Callback for the QuickBar to request an instance of CursorLoaderArgs that it will use to
         * display content.
         * @return
         */
        public CursorLoaderArgs onQuickBarCursorLoaderArgsRequest();
        public void onQuickBarButtonClick();
    }

    private QuickBarCallbacks mListener;

    private Cursor mCursor;

    private SimpleCursorAdapter mQuickBarAdapter;
    private ListView mListView;



    /**
     * Called when the fragment is attached to the host activity.
     * Ensures the host activity implements the fragment callbacks and sets the mListener member.
     * @param activity: The host activity
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mListener = (QuickBarCallbacks) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement QuickBarCallbacks");
        }
    }


    /**
     * Called when the fragment view is created.
     * Initializes the Adpter and ListView associated with the fragment.
     * @param inflater: Used to inflate the xml file describing the fragment view.
     * @param container: ViewGroup in the activity that the fragment belongs to.
     * @param savedInstanceState: : null on creation.  If the fragment is pushed onto the backstack
     *                          this can be used to save certain parameters for reinstantiation
     * @return: The view associated with this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view for this fragment from the associated xml file.
        final View root = inflater.inflate(R.layout.quick_bar, container, false);

        // Create empty adapter
        String[] from = {MediaStore.Images.ImageColumns._ID};
        int[] to = {R.id.activeAlbumName};
        mQuickBarAdapter = new SimpleCursorAdapter(getActivity(),R.layout.active_album,null,
                from,to,0);

        // Set adapter to ListView
        mListView = (ListView) root.findViewById(R.id.quickBarListView);
        mListView.setAdapter(mQuickBarAdapter);

        // Initialize cursor loader.  It will swap data into adapter when finished loading
        getLoaderManager().initLoader(CURSOR_LOADER, null, this);

        return root;
    }

    private static String cursorLogData(Cursor cursor) {
        if (null==cursor) return "Null Cursor";
        else return cursor.toString();
    }

    /**
     * LoaderManager callback for creating or obtaining an existing instance of all loaders associated
     * with this fragment.
     * @param id: ID for the loader being requested.  Inconsequential if there is only one loader.
     * @param bundle: Bundle args that can be used in loader creation.
     * @return: A new loader or an instance of an existing loader associated with id.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Loader creation requires a Uri to a datasource. This is held in the fragment's mCursorLoaderArgs
        // member.  If this is null, the host acitvity should provide it.
        CursorLoaderArgs args = mListener.onQuickBarCursorLoaderArgsRequest();
        CursorLoader loader = new CursorLoader(this.getActivity(),args.uri,
                args.projection,args.selection,args.selectionArgs,
                args.sortOrder);
        return loader;
    }

    /**
     * Called when the LoaderManager has finished loading data from a database, either on initial
     * request or automatically when the data has changed.
     * @param cursorLoader: The loader that the Loadermanager is calling this function for.
     * @param cursor: The Cursor for the database being loaded.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Data in the database is either finished loading or has changed and has been reloaded.
        // Change the data being displayed by the ListView.
        mQuickBarAdapter.swapCursor(cursor);
    }

    /**
     * Called when a cursor associated with the LoaderManager for this fragment is about to be reset.
     * The LoaderManager will automatically clear the cursor.  The implementing fragment should
     * remove any reference to the existing cursor.
     * @param cursorLoader: The loader associated with the cursor being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Swap a null cursor into the adapter so it doesn't try to reference an invalid cursor.
        mQuickBarAdapter.swapCursor(null);
    }


}
