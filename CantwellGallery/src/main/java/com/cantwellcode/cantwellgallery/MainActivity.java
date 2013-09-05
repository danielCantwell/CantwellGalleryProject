package com.cantwellcode.cantwellgallery;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.net.URI;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements QuickBarFragment.QuickBarCallbacks, DirectoryFragment.Callbacks{

    private static final String TAG                     = "MAIN ACTIVITY";

    private QuickBarFragment    mQuickBarFragment;
    private ContentFragment     mContentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        FragmentManager fm = getSupportFragmentManager();
        // Initialize QuickBarFragment
        mQuickBarFragment = (QuickBarFragment) fm.findFragmentById(R.id.quickBarFragment);
        mContentFragment  = (ContentFragment)  fm.findFragmentById(R.id.contentFragment);


        final SlidingPaneLayout slidingPaneLayout = SlidingPaneLayout.class.cast(root.findViewById(R.id.slidingpanelayout));

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

    @Override
    public boolean processDropOnQuickBar(Cursor directoryItem, DragEvent event) {

        ClipData data = event.getClipData();

        Cursor imageItem    = (Cursor) event.getLocalState();
        Cursor quickbarItem = directoryItem;

        int    quickbarIndex     = quickbarItem.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        String quickbarDirectory = quickbarItem.getString(quickbarIndex);
/*
        int    imageDataIndex   = imageItem.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int    imageNameIndex   = imageItem.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int    imageDescIndex   = imageItem.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION);
        String imageDirectory   = imageItem.getString(imageDataIndex);
        String imageName        = imageItem.getString(imageNameIndex);
        String imageDescription = imageItem.getString(imageDescIndex);
*/
/*
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), imageDirectory, imageName, imageDescription);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
*/
/*
        Toast t1 = Toast.makeText(this, "Quickbar Display Name: " + quickbarDirectory, Toast.LENGTH_SHORT);
        t1.show();
        Toast t2 = Toast.makeText(this, "Image Directory: " + imageDirectory, Toast.LENGTH_SHORT);
        t2.show();
        Toast t3 = Toast.makeText(this, "Image Name: " + imageName, Toast.LENGTH_SHORT);
        t3.show();
        Toast t4 = Toast.makeText(this, "Image Description: " + imageDescription, Toast.LENGTH_SHORT);
        t4.show();
*/
        //String contentIDString = data.getDescription().getLabel().toString();
        //Long contentID = Long.parseLong(contentIDString);

        return true;
    }


    @Override
    public boolean onDirectoryPaneItemSelected(long id, String name) {
        mContentFragment.changeDirectory(name);
        return true;
    }
}
