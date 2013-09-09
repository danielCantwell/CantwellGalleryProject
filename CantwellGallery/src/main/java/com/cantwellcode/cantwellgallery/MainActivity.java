package com.cantwellcode.cantwellgallery;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.DragEvent;
import android.view.View;

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
        //mQuickBarFragment = (QuickBarFragment) fm.findFragmentById(R.id.quickBarFragment);
        mContentFragment = (ContentFragment) fm.findFragmentById(R.id.contentFragment);


        final SlidingPaneLayout slidingPaneLayout = SlidingPaneLayout.class.cast(root.findViewById(R.id.slidingpanelayout));



        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelOpened(View view) {

                switch (view.getId()) {
                    case R.id.directoryFragment:
                        getSupportFragmentManager().findFragmentById(R.id.contentFragment).setHasOptionsMenu(true);
                        getSupportFragmentManager().findFragmentById(R.id.contentFragment).setHasOptionsMenu(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPanelClosed(View view) {

                switch (view.getId()) {
                    case R.id.directoryFragment:
                        getSupportFragmentManager().findFragmentById(R.id.contentFragment).setHasOptionsMenu(false);
                        getSupportFragmentManager().findFragmentById(R.id.directoryFragment).setHasOptionsMenu(true);
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


    @Override
    public boolean onDirectoryPaneItemSelected(long id, String name) {
        mContentFragment.changeDirectory(name);
        return true;
    }
}
