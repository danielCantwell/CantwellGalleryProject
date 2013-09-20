package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ClipData;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Daniel on 8/13/13.
 */
public class ContentFragment extends Fragment implements DatabaseContentHandler {

    private static final String TAG = "ContentFragment";


    // Default values
    private static final String     DEFAULT_LABEL       = "No Album Selected";

    private Callbacks               mListener;
    private ListView                mListView;
    private ImageCursorAdapter      mListAdapter;
    private TextView                mTextView;
    private String                  mName;

    private SwipeListViewDetect swipeDetect;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Callbacks) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement ContentFragment.Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        String[]    from    = {"DATA"};
        int[]       to      = {R.id.contentPaneItemImage};
        mListAdapter        = new ImageCursorAdapter(getActivity(),null,R.layout.content_pane_item,R.id.contentPaneItemImage);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.content_pane, container, false);

        mTextView = (TextView) root.findViewById(R.id.contentPaneHeader);
        if (mName == null) mTextView.setText(DEFAULT_LABEL);
        else mTextView.setText(mName);

        mListView = (ListView) root.findViewById(R.id.contentPaneListView);
        mListView.setAdapter(mListAdapter);

        swipeDetect = new SwipeListViewDetect(mListView, new SwipeListViewDetect.SlideCallbacks() {
            @Override
            public void onSlide(ListView listView, int[] reverseSortedPositions, SwipeListViewDetect.Direction direction) {
                for (int position : reverseSortedPositions) {
                    slideItem(position, direction);
                }
            }
        });

        setupDrag(mListView);
        setupSwipe(mListView);

        return root;
    }


    @Override
    public void changeContentLabelAndCursor(String label, Cursor cursor) {
        if(label != null) mTextView.setText(label);
        else mTextView.setText(DEFAULT_LABEL);
        mListAdapter.changeCursor(cursor);
    }


    /*********************************
     *        DRAG  AND  DROP        *
     *********************************/

    private void setupDrag(final ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Start an IMAGE drag event with ClipData set to the image id
                ClipData data = ClipData.newPlainText(ClipDataLabels.IMAGE.toString(), String.valueOf(id));
                ImageLocalState localState = new ImageLocalState((ImageViewHolder)view.getTag(), (Cursor)mListAdapter.getItem(position));
                view.startDrag(data, new MyDragShadowBuilder(view), localState, 0);
                return true;
            }
        });
    }

    private void setupSwipe(final ListView listView) {
        listView.setOnTouchListener(swipeDetect);
        listView.setOnScrollListener(swipeDetect.ScrollListener());
    }

    private void slideItem(int position, SwipeListViewDetect.Direction direction) {
        // move file etc
        // mListAdapter.remove(mAdapter.getItem(position));
        // notify data set changed
        String swipeDirection = null;

        if (direction == SwipeListViewDetect.Direction.Left) {
            Cursor cursor = (Cursor) mListAdapter.getItem(position);
            mListener.onSwipeLeft(cursor);
            swipeDirection = "Left";
        } else if (direction == SwipeListViewDetect.Direction.Right) {
            swipeDirection = "Right";
        }

        Toast directionToast = Toast.makeText(getActivity(), swipeDirection + "Swipe", Toast.LENGTH_SHORT);
        directionToast.show();
    }

}
