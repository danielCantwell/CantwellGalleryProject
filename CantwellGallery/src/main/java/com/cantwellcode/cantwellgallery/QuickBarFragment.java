package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.database.Cursor;
import android.net.Uri;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.DragEvent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 8/16/13.
 * Displays a subset of directories from a database provided by the host activity.
 * Host activity must provide a Uri for the database, the column name for the
 * directory id and the Active and Inactive views to be used for display, along with their
 * associated bindings.
 * This fragment will then keep a modifiable list of directories to display
 */
public class QuickBarFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "QuickBarFragment";

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
    private QuickBarCallbacks       mListener;
    private ListView                mListView;
    private QuickBarCursorAdapter   mQuickBarAdapter;

    public interface QuickBarCallbacks{
        boolean processDropOnQuickBar(Long itemID, DragEvent event);
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
            mListener = (QuickBarCallbacks) activity;
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
        mQuickBarAdapter = new QuickBarCursorAdapter(getActivity(),null,BUCKET_ID,BUCKET_DISPLAY_NAME,MIN_ID);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view for this fragment from the associated xml file.
        final View root = inflater.inflate(R.layout.quick_bar, container, false);
        mListView = (ListView) root.findViewById(R.id.quickBarListView);
        mListView.setAdapter(mQuickBarAdapter);

        setupDrag(mListView);
        setupDrop(mListView);

        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        mListener = null;
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
        outState.putInt(LIST_POSITION,mListView.getFirstVisiblePosition());
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
        String[] projection = bundle.getStringArray(PROJECTION);
        String selection = bundle.getString(SELECTION);
        String[] selectionArgs = bundle.getStringArray(SELECTION_ARGS);
        String sortOrder = bundle.getString(SORT_ORDER);
        CursorLoader loader = new CursorLoader(this.getActivity(),uri,projection,selection,selectionArgs,sortOrder);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, BUCKET_ID + " cursor finished loading. " + cursor.getCount() + " rows found.");
        mQuickBarAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mQuickBarAdapter.changeCursor(null);
    }


    /*********************************
     *        DRAG  AND  DROP        *
     *********************************/

    /**
     * Used to allow items in the listView to be dragged.
     *
     * @param listView - used to override OnItemLongClickListener()
     */
    private void setupDrag(ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String title = "albumNameWillGoHere";
                final String textData = title + ":" + position;
                ClipData data = ClipData.newPlainText(title, textData);
                view.startDrag(data, new MyDragShadowBuilder(view), null, 0);
                return true;
            }
        });
    }

    private void setupDrop(final ListView v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                switch (dragEvent.getAction()) {

                    // When a view drag starts, imageView turns blue
                    case DragEvent.ACTION_DRAG_STARTED:
                        Toast dragStartedToast = Toast.makeText(getActivity(), "Drag Started", Toast.LENGTH_SHORT);
                        dragStartedToast.show();
                        return processDragStarted(dragEvent);

                    // When the view is being held over the imageView, the imageView turns blue
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Toast dragEnteredToast = Toast.makeText(getActivity(), "Drag Entered", Toast.LENGTH_SHORT);
                        dragEnteredToast.show();
                        break;

                    // When the view is exited, but not dropped on the imageView, the imageView turns yellow
                    case DragEvent.ACTION_DRAG_EXITED:
                        Toast dragExitedToast = Toast.makeText(getActivity(), "Drag Exited", Toast.LENGTH_SHORT);
                        dragExitedToast.show();
                        break;

                    // When the view is dropped on the imageView, process the drop
                    case DragEvent.ACTION_DROP:
                        Toast dropToast = Toast.makeText(getActivity(), "Drop", Toast.LENGTH_SHORT);
                        dropToast.show();

                        int itemPosition = v.pointToPosition((int) dragEvent.getX(), (int) dragEvent.getY());
                        long itemID = v.getAdapter().getItemId(itemPosition);

                        return mListener.processDropOnQuickBar(itemID, dragEvent);
                }
                return false;
            }
        });
    }

    /**
     * Check if this is the drag operation you want. There might be other
     * clients that would be generating the drag event. Here, we check the mime
     * type of the data
     *
     * @param event
     * @return : true if the item has mimetype = plain text
     */
    private boolean processDragStarted(DragEvent event) {
        ClipDescription clipDesc = event.getClipDescription();
        if (clipDesc != null) {
            return clipDesc.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        }
        return false;
    }

}
