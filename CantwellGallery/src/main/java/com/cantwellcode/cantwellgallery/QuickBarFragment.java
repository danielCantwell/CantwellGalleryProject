package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 8/16/13.
 * Displays a subset of directories from a database provided by the host activity.
 * Host activity must provide a Uri for the database, the column name for the
 * directory id and the Active and Inactive views to be used for display, along with their
 * associated bindings.
 * This fragment will then keep a modifiable list of directories to display
 */
public class QuickBarFragment extends Fragment{
    private static final String TAG = "QuickBarFragment";

    public interface QuickBarCallbacks{
        public void onQuickBarButtonClick();
    }

    private QuickBarCallbacks mListener;
    private ListView mListView;
    private ArrayAdapter mListAdapter;
    private Cursor mCursor;
    private List<String> mItemIds;
    private List<String> mItemNames;



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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemIds = new ArrayList<String>();
        mItemNames = new ArrayList<String>();
    }

    /**
     * Called when the fragment view is created.
     * Initializes the Adpter and ListView associated with the fragment.
     * @param inflater: Used to inflate the xml file describing the fragment view.
     * @param container: ViewGroup in the activity that the fragment belongs to.
     * @param savedInstanceState: null on creation.  If the fragment is pushed onto the backstack
     *                          this can be used to save certain parameters for reinstantiation
     * @return: The view associated with this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view for this fragment from the associated xml file.
        final View root = inflater.inflate(R.layout.quick_bar, container, false);
        mListView = (ListView) root.findViewById(R.id.quickBarListView);
        mListAdapter = new ArrayAdapter<String>(getActivity(),R.layout.active_album,R.id.activeAlbumName,mItemNames);
        mListView.setAdapter(mListAdapter);

        setupDrag(mListView);
        //setupDrop(mListView);

        return root;
    }

    public void changeCursor(Cursor cursor, String idColumn, String displayNameColumn){
        List<String> ids = null;
        List<String> names = null;
        if (cursor != null){
            ids = new ArrayList<String>();
            names = new ArrayList<String>();
            int idIndex = cursor.getColumnIndexOrThrow(idColumn);
            int nameIndex = cursor.getColumnIndexOrThrow(displayNameColumn);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                ids.add(cursor.getString(idIndex));
                names.add(cursor.getString(nameIndex));
                cursor.moveToNext();
            }
        }
        mItemIds = ids;
        mItemNames = names;
        if (mListAdapter != null){
            mListAdapter.clear();
            mListAdapter.addAll(names);
            mListAdapter.notifyDataSetChanged();
        }
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
/*
    private void setupDrop(ListView v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                int itemPosition = v.pointToPosition((int) dragEvent.getX(), (int) dragEvent.getY());
                View itemView = v.findViewById(itemPosition);

                switch (dragEvent.getAction()) {

                    // When a view drag starts, imageView turns blue
                    case DragEvent.ACTION_DRAG_STARTED:
                        view.setBackgroundColor(Color.BLUE);
                        //return processDragStarted(dragEvent);
                        break;

                    // When the view is being held over the imageView, the imageView turns blue
                    case DragEvent.ACTION_DRAG_ENTERED:
                        view.setBackgroundColor(Color.MAGENTA);
                        break;

                    // When the view is exited, but not dropped on the imageView, the imageView turns yellow
                    case DragEvent.ACTION_DRAG_EXITED:
                        view.setBackgroundColor(Color.YELLOW);
                        break;

                    // When the view is dropped on the imageView, process the drop
                    case DragEvent.ACTION_DROP:
                        return processDrop(view, dragEvent);

                }
                return false;
            }
        });
    }
*/
    /**
     * Process the drop event
     *
     * @param event
     * @return
     */
    private boolean processDrop(View view, DragEvent event) {

        view.setBackground(getResources().getDrawable(R.drawable.ic_launcher));

        return true;
    }
/*
    private class MyListViewDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DROP:
                    // We drag the item on top of the one which is at itemPosition
                    int itemPosition = _myListView.pointToPosition((int)event.getX(), (int)event.getY());
                    // We can even get the view at itemPosition thanks to get/setid
                    View itemView = _myListView.findViewById(itemPosition );
                /* If you try the same thing in ACTION_DRAG_LOCATION, itemView
                 * is sometimes null; if you need this view, just return if null.
                 * As the same event is then fired later, only process the event
                 * when itemView is not null.
                 * It can be more problematic in ACTION_DRAG_DROP but for now
                 * I never had itemView null in this event. */
                    // Handle the drop as you like
/*                    return true;
            }
            return true;
        }
    }
*/
}
