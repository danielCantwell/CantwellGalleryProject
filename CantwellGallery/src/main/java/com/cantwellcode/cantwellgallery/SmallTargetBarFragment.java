package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 9/6/13.
 */
public class SmallTargetBarFragment extends Fragment implements TargetBar{
    private static final String TAG = "SMALL_TARGET_BAR_FRAGMENT";

    private Callbacks   mListener;

    private ImageView   mCurrentItemImage;
    private ImageView   mNewItemImage;
    private TextView    mCurrentItemText;

    private long        mCurrentBucketID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Callbacks) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement QuickBarCallbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root           = inflater.inflate(R.layout.small_target_bar,container,false);

        mCurrentItemImage   = (ImageView)   root.findViewById(R.id.targetBarCurrentItemImage);
        mCurrentItemText    = (TextView)    root.findViewById(R.id.targetBarCurrentItemText);
        mNewItemImage       = (ImageView)   root.findViewById(R.id.targetBarNewItemImage);

        setupDrop(mCurrentItemImage);
        setupDrop(mNewItemImage);

        //setupDrag(mCurrentItemImage);

        return root;
    }


    /*********************************
     *        DRAG  AND  DROP        *
     *********************************/

    /**
     * Setup a view to be able to be dragged
     *
     * @param view - the view that will be able to be dragged
     */
    private void setupDrag(final View view) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                long id               = view.getId();
                final String label    = Long.toString(id);
                final String textData = label + ":" + "somethin";
                ClipData data         = ClipData.newPlainText(label, textData);

                view.startDrag(data, new MyDragShadowBuilder(view), id, 0);
                return true;
            }
        });
    }

    /**
     * Setup a view to accept other views to be dropped on it
     *
     * @param v - the view that will accept a drop
     */
    private void setupDrop(final View v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                switch (dragEvent.getAction()) {

                    // When a view drag starts
                    case DragEvent.ACTION_DRAG_STARTED:
                        Toast dragStartedToast = Toast.makeText(getActivity(), "Small Target Bar recognized : Drag Started", Toast.LENGTH_SHORT);
//                        dragStartedToast.show();
                        return processDragStarted(dragEvent);

                    // When the view is being held over the imageView
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Toast dragEnteredToast = Toast.makeText(getActivity(), "Small Target Bar recognized : Drag Entered", Toast.LENGTH_SHORT);
//                        dragEnteredToast.show();
                        break;

                    // When the view is exited
                    case DragEvent.ACTION_DRAG_EXITED:
                        Toast dragExitedToast = Toast.makeText(getActivity(), "Small Target Bar recognized : Drag Exited", Toast.LENGTH_SHORT);
//                        dragExitedToast.show();
                        break;

                    // When the view is dropped on the target bar
                    case DragEvent.ACTION_DROP:
                        Toast dropToast = Toast.makeText(getActivity(), "Small Target Bar recognized : Drop", Toast.LENGTH_SHORT);
                        dropToast.show();
                        return processDrop(dragEvent, v);
                }
                return false;
            }
        });
    }

    /**
     * Check if this is the drag operation you want. There might be other
     * clients that would be generating the drag event. Here we check the label
     *
     * @param event
     * @return : true if the item has mimetype = plain text
     */
    private boolean processDragStarted(DragEvent event) {
        ClipDescription clipDesc = event.getClipDescription();
        if (clipDesc != null) {
            String label = (String) clipDesc.getLabel();
            try{
                switch (ClipDataLabels.valueOf(label)){
                    case BUCKET:
                        return true;
                    case IMAGE:
                        return false;
                    default:
                        return false;
                }
            } catch(IllegalArgumentException e){
                Log.d(TAG,"Invalid ClipData label: " + label);
                return false;
            }
        }
        return false;
    }

    /**
     * Handle drop events on target bar
     *
     * @param dragEvent - the drag event from the view that is being dragged
     * @param v - the view that is receiving the drop event
     * @return true if the drop is processed and handled, otherwise return false
     */
    private boolean processDrop(DragEvent dragEvent, View v) {
        ClipDescription clipDescription = dragEvent.getClipDescription();
        if (clipDescription==null) return false;
        ViewParent parent = v.getParent();
        switch (v.getId()) {
            // If the item is dropped on the view "targetBarCurrentItemImage"
            case R.id.targetBarCurrentItemImage:
                Toast t = Toast.makeText(getActivity(), "Dropped on : targetBarCurrentItem", Toast.LENGTH_SHORT);
                t.show();
                switch (ClipDataLabels.valueOf(clipDescription.getLabel().toString())){
                    case BUCKET:
                        processBucketDrop(dragEvent,v);
                        return true;
                    case IMAGE:
                        return false;
                    default:
                        return false;
                }
            // If the item is dropped on the view "targetBarNewItemImage"
            case R.id.targetBarNewItemImage:
                Toast t1 = Toast.makeText(getActivity(), "Dropped on : targetBarNewItem", Toast.LENGTH_SHORT);
                t1.show();
                return true;
            default:
                return false;
        }
    }

    /**
     * Processes a drop event for dropped items labeled BUCKET
     * @param dragEvent
     * @param v
     */
    private void processBucketDrop(DragEvent dragEvent, View v) {
        // Get the attached ClipData.  For buckets this should be the bucket id.
        String data = (String) dragEvent.getClipData().getItemAt(0).coerceToText(getActivity());
        mCurrentBucketID = Long.valueOf(data);
        // Get a reference to the view being dropped.  This must be set as the localState when
        //      initiating the drag
        View dropped = (View) dragEvent.getLocalState();
        // Change displayed data using the BucketViewHolder which should be set as the tag of the dropped view
        BucketViewHolder holder = (BucketViewHolder) dropped.getTag();
        Bitmap image = ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();
        String title = (String) holder.textView.getText();
        mCurrentItemImage.setImageBitmap(image);
        mCurrentItemText.setText(title);
    }

    /**
     *
     * @param itemID
     * @param targetID
     * @return
     */
    @Override
    public boolean moveItemToTarget(long itemID, long targetID) {
        return mListener.onMoveItemToTarget(itemID,targetID);
    }

    /**
     *
     * @param itemID
     * @return
     */
    @Override
    public boolean createNewTargetFromItem(long itemID) {
        return mListener.onCreateNewTargetFromItem(itemID);
    }
}
