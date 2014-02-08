package com.cantwellcode.cantwellgallery;

import android.app.DialogFragment;
import android.content.ClipDescription;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.DragEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements TargetBar.Callbacks, DatabaseMaster.Callbacks, DatabaseContentHandler.Callbacks,
        InfoDialog.InfoDialogListener, ColorPickerDialog.ColorDialogListener, SettingsDialog.SettingsDialogListener {

    private static final String TAG                     = "MAIN ACTIVITY";

    private TargetBar               mTargetBar;
    private ContentFragment         mContentFragment;
    private DirectoryFragment       mDirectoryFragment;
    private ImageView               mUtilityBarDeleteView;
    private ImageView               mUtilityBarInfoView;
    private ImageView               mChangeViewButton;
    private DatabaseContentHandler  mDatabaseContentHandler;
    private DatabaseMaster          mDatabaseMaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        findViewById(R.id.middleArrow).setBackgroundResource(R.drawable.ic_right_arrow);

        FragmentManager fm = getSupportFragmentManager();
        // Initialize TargetBarListFragment
        //mTargetBarListFragment   = (TargetBarListFragment) fm.findFragmentById(R.id.quickBarFragment);
        mContentFragment        = (ContentFragment)     fm.findFragmentById(R.id.contentFragment);
        mDirectoryFragment      = (DirectoryFragment)   fm.findFragmentById(R.id.directoryFragment);
        mTargetBar              = (TargetBar)           fm.findFragmentById(R.id.targetBarFragment);
        mDatabaseContentHandler = mContentFragment;
        mDatabaseMaster         = mDirectoryFragment;

        mUtilityBarDeleteView = (ImageView) findViewById(R.id.utilityBarDeleteView);
        mUtilityBarInfoView   = (ImageView) findViewById(R.id.utilityBarInfoView);
        setupDrop(mUtilityBarDeleteView);
        setupDrop(mUtilityBarInfoView);

        final SlidingPane slidingPaneLayout = SlidingPane.class.cast(root.findViewById(R.id.slidingpanelayout));

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelOpened(View view) {

                switch (view.getId()) {
                    case R.id.contentFragment:
                        findViewById(R.id.middleArrow).setBackgroundResource(R.drawable.ic_left_arrow);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPanelClosed(View view) {

                switch (view.getId()) {
                    case R.id.contentFragment:
                        findViewById(R.id.middleArrow).setBackgroundResource(R.drawable.ic_right_arrow);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void setupDrop(final View v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                switch (dragEvent.getAction()) {

                    // When a view drag starts
                    case DragEvent.ACTION_DRAG_STARTED:
                        return processDragStarted(dragEvent);

                    // When the view is being held over the imageView
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;

                    // When the view is exited
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;

                    // When the view is dropped on the quickbar
                    case DragEvent.ACTION_DROP:
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
            case R.id.utilityBarDeleteView:
                Toast t = Toast.makeText(this, "Dropped on : utilityBarDeleteView", Toast.LENGTH_SHORT);
                t.show();
                return true;
            case R.id.utilityBarInfoView:
                DialogFragment dialogFragment = new InfoDialog();
                dialogFragment.show(getFragmentManager(), "media_info");
        }
        return false;
    }

    @Override
    public boolean onMoveItemToTarget(ImageData itemData, BucketData targetData) {
        return mDirectoryFragment.moveImageToBucket(itemData, targetData);
    }

    @Override
    public boolean onCreateNewTargetFromItem(Cursor itemCursor) {
        return false;
    }

    @Override
    public void changeContentLabelAndCursor(String label, Cursor cursor) {
        mDatabaseContentHandler.changeContentLabelAndCursor(label, cursor);
    }

    /**
     * Swipe left callback for content fragment
     * @param imageData
     * @return
     */
    @Override
    public boolean onSwipeLeft(ImageData imageData) {
        BucketData bucketData = (BucketData) mTargetBar.getCurrentTargetData();
        return mDirectoryFragment.moveImageToBucket(imageData,bucketData);
    }
    
    /*************************
     *      Menu Click       *
     ************************/

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_settings:
                        menuClickSettings();
                        return true;
                    case R.id.action_help:
                        menuClickHelp();
                        return true;
                    case R.id.action_info:
                        menuClickInfo();
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.show();
    }

    public void menuClickSettings() {
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.show(getFragmentManager(), "fragment_settings");
    }

    public void menuClickHelp() {
        Toast help = Toast.makeText(this, "What would you like help with?", Toast.LENGTH_SHORT);
        help.show();
    }

    public void menuClickInfo() {
        Toast info = Toast.makeText(this, "Created by Cantwell Code", Toast.LENGTH_SHORT);
        info.show();
    }

    /********************************
     *      Media Info Dialog       *
     ********************************/

    @Override
    public void onInfoDialogOkClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    @Override
    public void onInfoDialogEditClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }


    /**********************************
     *       Color Picker Dialog      *
     **********************************/

    @Override
    public void onColorDialogOkClick(DialogFragment dialog) {

    }

    @Override
    public void onColorDialogCancelClick(DialogFragment dialog) {

    }

    /******************************
     *       Settings Dialog      *
     ******************************/

    @Override
    public void onSettingsDialogColorClick(DialogFragment dialog) {
        dialog.dismiss();
        DialogFragment colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.show(getFragmentManager(), "fragment_colorpicker");
    }
}
