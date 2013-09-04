package com.cantwellcode.cantwellgallery;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Main Activity that is first loaded when the application starts
 */
public class MainActivity extends FragmentActivity
        implements QuickBarFragment.QuickBarCallbacks{

    private static final String TAG                     = "MAIN ACTIVITY";

    private QuickBarFragment    mQuickBarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate the main view with the main activity layout
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        FragmentManager fm = getSupportFragmentManager();
        // Initialize QuickBarFragment
        mQuickBarFragment = (QuickBarFragment) fm.findFragmentById(R.id.quickBarFragment);


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

    @Override
    public boolean processDropOnQuickBar(Long itemID, DragEvent event) {
        return true;
    }

*/
}
