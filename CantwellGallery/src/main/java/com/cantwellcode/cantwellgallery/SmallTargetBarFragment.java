package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 9/6/13.
 */
public class SmallTargetBarFragment extends Fragment implements TargetBar{
    private static final String TAG = "SMALL_TARGET_BAR_FRAGMENT";

    private Callbacks   mListener;

    private ImageView   mCurrentTargetImage;
    private ImageView   mNewTargetImage;
    private TextView    mCurrentTargetText;
    private long        mCurrentTargetID;
    private String      mCurrentTargetPath;
    private BucketData  mCurrentTargetData;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTargetPath = null;
        mCurrentTargetData = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root           = inflater.inflate(R.layout.small_target_bar,container,false);

        mCurrentTargetImage = (ImageView)   root.findViewById(R.id.targetBarCurrentTargetImage);
        mCurrentTargetText  = (TextView)    root.findViewById(R.id.targetBarCurrentTargetText);
        mNewTargetImage     = (ImageView)   root.findViewById(R.id.targetBarNewTargetImage);

        setupDrop(mCurrentTargetImage);
        setupDrop(mNewTargetImage);

        //setupDrag(mCurrentTargetImage);

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
        if (clipDesc == null) return false;

        // Get the label to determine if the drag event can be handled.
        ClipDataLabels label;
        try {
            label = ClipDataLabels.valueOf(clipDesc.getLabel().toString());
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
        switch (label){
            case BUCKET:
                return true;
            case IMAGE:
                return true;
            default:
                return false;
        }
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
        // Get the label to handle processing
        ClipDataLabels label;
        try {
            label = ClipDataLabels.valueOf(clipDescription.getLabel().toString());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
        // Determine where the item was dropped and handle it appropriately
        switch (v.getId()) {
            // If the item is dropped on the view "targetBarCurrentItemImage"
            case R.id.targetBarCurrentTargetImage:
                Toast t = Toast.makeText(getActivity(), "Dropped on : targetBarCurrentTarget", Toast.LENGTH_SHORT);
//                t.show();
                switch (label){
                    case BUCKET:
                        return processBucketDropOnCurrentTarget(dragEvent, v);
                    case IMAGE:
                        return processImageDropOnCurrentTarget(dragEvent, v);
                    default:
                        return false;
                }
            // If the item is dropped on the view "targetBarNewItemImage"
            case R.id.targetBarNewTargetImage:
                Toast t1 = Toast.makeText(getActivity(), "Dropped on : targetBarNewTarget", Toast.LENGTH_SHORT);
//                t1.show();
                switch (label){
                    case BUCKET:
                        return false;
                    case IMAGE:
                        return false;
                    default:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * Process drop event for dropped items labeled IMAGE
     * @param dragEvent
     * @param v
     */
    private boolean processImageDropOnCurrentTarget(DragEvent dragEvent, View v) {
        if (mCurrentTargetID==-1) return false;
        // Get the local state info for the Image being dropped
        ImageData item = (ImageData) dragEvent.getLocalState();

        // Get the cursor for the image and pass it to moveItemToTarget
        moveItemToTarget(item,mCurrentTargetData);
        return true;
    }

    /**
     * Processes a drop event for dropped items labeled BUCKET
     * @param dragEvent
     * @param v
     */
    private boolean processBucketDropOnCurrentTarget(DragEvent dragEvent, View v) {
        // Get the local state info for the Bucket view being dropped.
        BucketData bucketData = (BucketData) dragEvent.getLocalState();
        mCurrentTargetData = bucketData;

        // Change displayed data using the BucketViewHolder stored in the local state.
        BucketViewHolder holder = (BucketViewHolder) bucketData.getView().getTag();
        Bitmap image = ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();
        String title = (String) holder.textView.getText();
        mCurrentTargetImage.setImageBitmap(image);
        mCurrentTargetText.setText(title);
        return true;
    }

    /**
     * Given the cursors corresponding to an item and a target, move the item to th target
     *
     * @param itemData
     * @param targetData
     * @return
     */
    @Override
    public boolean moveItemToTarget(ImageData itemData, BucketData targetData) {
        return mListener.onMoveItemToTarget(itemData, targetData);
    }

    /**
     * Create a new target from data contained in the supplied item
     * @param itemCursor
     * @return
     */
    @Override
    public boolean createNewTargetFromItem(Cursor itemCursor) {
        return mListener.onCreateNewTargetFromItem(itemCursor);
    }

    @Override
    public long getCurrentTarget() {
        return mCurrentTargetID;
    }

    @Override
    public String getCurrentTargetPath() {
        return mCurrentTargetPath;
    }

    @Override
    public BucketData getCurrentTargetData() {
        return mCurrentTargetData;
    }
}
