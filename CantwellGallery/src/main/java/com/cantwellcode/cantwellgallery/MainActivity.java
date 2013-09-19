package com.cantwellcode.cantwellgallery;

import android.content.ClipDescription;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements TargetBar.Callbacks, DatabaseMaster.Callbacks {

    private static final String TAG                     = "MAIN ACTIVITY";

    private TargetBar               mTargetBar;
    private ContentFragment         mContentFragment;
    private DirectoryFragment       mDirectoryFragment;
    private ImageView               mUtilityBarDeleteView;
    private ImageView               mUtilityBarHelpView;
    private DatabaseContentHandler  mDatabaseContentHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        FragmentManager fm = getSupportFragmentManager();
        // Initialize TargetBarListFragment
        //mTargetBarListFragment   = (TargetBarListFragment) fm.findFragmentById(R.id.quickBarFragment);
        mContentFragment        = (ContentFragment)     fm.findFragmentById(R.id.contentFragment);
        mDirectoryFragment      = (DirectoryFragment)   fm.findFragmentById(R.id.directoryFragment);
        mDatabaseContentHandler = mContentFragment;

        mUtilityBarHelpView   = (ImageView) findViewById(R.id.utilityBarHelpView);
        mUtilityBarDeleteView = (ImageView) findViewById(R.id.utilityBarDeleteView);
        setupDrop(mUtilityBarDeleteView);

        /*
         * This is what happens when the user presses the help (?) button
         */
        mUtilityBarHelpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast helpToast = Toast.makeText(getBaseContext(),
                        "You Pressed the Help Button.", Toast.LENGTH_SHORT);
                helpToast.setMargin(10, 0);
                helpToast.show();
            }
        });


        final SlidingPane slidingPaneLayout = SlidingPane.class.cast(root.findViewById(R.id.slidingpanelayout));

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelOpened(View view) {

                switch (view.getId()) {
                    case R.id.directoryFragment:
                        //getSupportFragmentManager().findFragmentById(R.id.contentFragment).setHasOptionsMenu(true);
                        //getSupportFragmentManager().findFragmentById(R.id.contentFragment).setHasOptionsMenu(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPanelClosed(View view) {

                switch (view.getId()) {
                    case R.id.directoryFragment:
                        //getSupportFragmentManager().findFragmentById(R.id.contentFragment).setHasOptionsMenu(false);
                        //getSupportFragmentManager().findFragmentById(R.id.directoryFragment).setHasOptionsMenu(true);
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
                        Toast dragStartedToast = Toast.makeText(MainActivity.this, "Trash Can recognized: Drag Started", Toast.LENGTH_SHORT);
//                        dragStartedToast.show();
                        return processDragStarted(dragEvent);

                    // When the view is being held over the imageView
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Toast dragEnteredToast = Toast.makeText(MainActivity.this, "Trash Can recognized: Drag Entered", Toast.LENGTH_SHORT);
//                        dragEnteredToast.show();
                        break;

                    // When the view is exited
                    case DragEvent.ACTION_DRAG_EXITED:
                        Toast dragExitedToast = Toast.makeText(MainActivity.this, "Trash Can recognized: Drag Exited", Toast.LENGTH_SHORT);
//                        dragExitedToast.show();
                        break;

                    // When the view is dropped on the quickbar
                    case DragEvent.ACTION_DROP:
                        Toast dropToast = Toast.makeText(MainActivity.this, "Trash Can recognized: Drop", Toast.LENGTH_SHORT);
//                        dropToast.show();
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
        }
        return false;
    }

    @Override
    public boolean onMoveItemToTarget(Cursor itemCursor, Cursor targetCursor) {
        return mDirectoryFragment.moveImageToBucket(itemCursor, targetCursor);
    }

    @Override
    public boolean onCreateNewTargetFromItem(Cursor itemCursor) {
        return false;
    }

    @Override
    public void changeContentLabelAndCursor(String label, Cursor cursor) {
        mDatabaseContentHandler.changeContentLabelAndCursor(label, cursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /********************************
     *      Menu Item Clicks        *
     ********************************/

    public void menuClickSettings(MenuItem item) {
        Toast settings = Toast.makeText(this, "Settings", Toast.LENGTH_SHORT);
        settings.show();
    }

    public void menuClickHelp(MenuItem item) {
        Toast help = Toast.makeText(this, "What would you like help with?", Toast.LENGTH_SHORT);
        help.show();
    }

    public void menuClickInfo(MenuItem item) {
        Toast info = Toast.makeText(this, "Created by Cantwell Code", Toast.LENGTH_SHORT);
        info.show();
    }
}
