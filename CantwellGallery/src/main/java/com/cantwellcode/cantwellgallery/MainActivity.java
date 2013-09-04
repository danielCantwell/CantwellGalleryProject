package com.cantwellcode.cantwellgallery;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements QuickBarFragment.QuickBarCallbacks{

    private static final String TAG                     = "MAIN ACTIVITY";

    private QuickBarFragment mQuickBarFragment;

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        FragmentManager fm = getSupportFragmentManager();
        // Initialize QuickBarFragment
        mQuickBarFragment = (QuickBarFragment) fm.findFragmentById(R.id.quickBarFragment);

//        imageView = (ImageView) findViewById(R.id.photoPaneImageView);
//        setupDrop(imageView);
//        setupDrag(imageView);

        final SlidingPaneLayout slidingPaneLayout = SlidingPaneLayout.class.cast(root.findViewById(R.id.slidingpanelayout));

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelOpened(View view) {

                switch (view.getId()) {
                    case R.id.slidingDirectoryPane:
                        getSupportFragmentManager().findFragmentById(R.id.slidingContentPane).setHasOptionsMenu(true);
                        getSupportFragmentManager().findFragmentById(R.id.slidingContentPane).setHasOptionsMenu(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPanelClosed(View view) {

                switch (view.getId()) {
                    case R.id.slidingDirectoryPane:
                        getSupportFragmentManager().findFragmentById(R.id.slidingContentPane).setHasOptionsMenu(false);
                        getSupportFragmentManager().findFragmentById(R.id.slidingDirectoryPane).setHasOptionsMenu(true);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    /*********************************
     *        DRAG  AND  DROP        *
     *********************************/

    /**
     * Used to allow items in the listView to be dragged.
     *
     * @param view - used to override OnLongClickListener()
     */
    private void setupDrag(View view) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String title = "photoNameWillGoHere";
                final String textData = title;
                ClipData data = ClipData.newPlainText(title, textData);
                view.startDrag(data, new MyDragShadowBuilder(view), null, 0);
                return true;
            }
        });
    }

    private void setupDrop(View v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                switch (dragEvent.getAction()) {

                    // When a view drag starts, imageView turns blue
                    case DragEvent.ACTION_DRAG_STARTED:
                        view.setBackgroundColor(Color.BLUE);
                        return processDragStarted(dragEvent);

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

    /**
     * Process the drop event
     *
     * @param event
     * @return
     */
    private boolean processDrop(View view, DragEvent event) {

        view.setBackground(getResources().getDrawable(R.drawable.ic_launcher));

        /*
        ClipData data = event.getClipData();
        if (data != null) {
            if (data.getItemCount() > 0) {
                ClipData.Item item = data.getItemAt(0);
                String textData = (String) item.getText();
                String[] parts = textData.split(":");
                int index = Integer.parseInt(parts[1]);
                String listItem = parts[0];
                //updateViewsAfterDropComplete(listItem, index);
                return true;
            }
        }
        */
        return true;
    }

    /**
     * Check if this is the drag operation you want. There might be other
     * clients that would be generating the drag event. Here, we check the mime
     * type of the data
     *
     * @param event
     * @return
     */
    private boolean processDragStarted(DragEvent event) {
        ClipDescription clipDesc = event.getClipDescription();
        if (clipDesc != null) {
            return clipDesc.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        }
        return false;
    }
}
