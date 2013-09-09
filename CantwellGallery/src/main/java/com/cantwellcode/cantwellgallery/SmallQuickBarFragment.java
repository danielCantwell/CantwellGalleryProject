package com.cantwellcode.cantwellgallery;

import android.content.ClipDescription;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 9/6/13.
 */
public class SmallQuickBarFragment extends Fragment {


    private ImageView mCurrentItemImage;
    private ImageView mNewItemImage;
    private TextView  mCurrentItemText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.small_quick_bar,container,false);

        mCurrentItemImage = (ImageView) root.findViewById(R.id.quickBarCurrentItemImage);
        mNewItemImage     = (ImageView) root.findViewById(R.id.quickBarNewItemImage);

        setupDrop(mCurrentItemImage);
        setupDrop(mNewItemImage);

        return root;
    }

    private void setupDrop(final View v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                switch (dragEvent.getAction()) {

                    // When a view drag starts
                    case DragEvent.ACTION_DRAG_STARTED:
                        Toast dragStartedToast = Toast.makeText(getActivity(), "Small Quick Bar recognized : Drag Started", Toast.LENGTH_SHORT);
                        dragStartedToast.show();
                        return processDragStarted(dragEvent);

                    // When the view is being held over the imageView
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Toast dragEnteredToast = Toast.makeText(getActivity(), "Small Quick Bar recognized : Drag Entered", Toast.LENGTH_SHORT);
                        dragEnteredToast.show();
                        break;

                    // When the view is exited
                    case DragEvent.ACTION_DRAG_EXITED:
                        Toast dragExitedToast = Toast.makeText(getActivity(), "Small Quick Bar recognized : Drag Exited", Toast.LENGTH_SHORT);
                        dragExitedToast.show();
                        break;

                    // When the view is dropped on the quickbar
                    case DragEvent.ACTION_DROP:
                        Toast dropToast = Toast.makeText(getActivity(), "Small Quick Bar recognized : Drop", Toast.LENGTH_SHORT);
                        dropToast.show();
                        return processDrop(dragEvent, v);
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

    /**
     * Handle drop events on small quickbar
     *
     * @param dragEvent - the drag event from the view that is being dragged
     * @param v - the view that is receiving the drop event
     * @return true if the drop is processed and handled, otherwise return false
     */
    private boolean processDrop(DragEvent dragEvent, View v) {
        switch (v.getId()) {
            // If the item is dropped on the view "quickBarCurrentItemImage"
            case R.id.quickBarCurrentItemImage:
                Toast t = Toast.makeText(getActivity(), "Dropped on : quickBarCurrentItemImage", Toast.LENGTH_SHORT);
                t.show();
                //long draggedViewID = Long.parseLong(dragEvent.getClipData().getDescription().getLabel().toString());
                return true;
            // If the item is dropped on the view "quickBarNewItemImage"
            case R.id.quickBarNewItemImage:
                Toast t1 = Toast.makeText(getActivity(), "Dropped on : quickBarNewItemImage", Toast.LENGTH_SHORT);
                t1.show();
                return true;
        }
        return false;
    }

}
