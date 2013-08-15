package com.cantwellcode.cantwellgallery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    ListView quickBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        quickBar = (ListView) this.findViewById(R.id.quickBar);
        AlbumListAdapter adapter = new AlbumListAdapter(this,new ArrayList<File>(),0);
        quickBar.setAdapter(adapter);

        final SlidingPaneLayout slidingPaneLayout = SlidingPaneLayout.class.cast(root.findViewById(R.id.slidingpanelayout));

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelOpened(View view) {

                switch (view.getId()) {
                    case R.id.album_pane:
                        getSupportFragmentManager().findFragmentById(R.id.photo_pane).setHasOptionsMenu(true);
                        getSupportFragmentManager().findFragmentById(R.id.photo_pane).setHasOptionsMenu(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPanelClosed(View view) {

                switch (view.getId()) {
                    case R.id.album_pane:
                        getSupportFragmentManager().findFragmentById(R.id.photo_pane).setHasOptionsMenu(false);
                        getSupportFragmentManager().findFragmentById(R.id.album_pane).setHasOptionsMenu(true);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void onAddDirectoryButtonClick(){

    }

    
}
