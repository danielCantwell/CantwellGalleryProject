package com.cantwellcode.cantwellgallery;

import android.content.ContentProvider;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Chris on 8/16/13.
 */
public class QuickBarFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String     DISPLAY_NAME = MediaStore.Images.ImageColumns.DISPLAY_NAME;
    private static final String     _ID = MediaStore.Images.ImageColumns._ID;
    private Uri             mUri;
    private String[]        mProjection;
    private String          mSelection;
    private String[]        mSelectionArgs;
    private ListView        mListView;
    private Button          mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.quick_bar, container, false);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.setBackgroundColor(Color.BLACK);

        mListView = (ListView) root.findViewById(R.id.quickBarListView);
        String[] displayFields = {}

        mUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        mProjection = new String[]{MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC};
        mSelection = null;
        mSelectionArgs = new String[]{};

        return root;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
