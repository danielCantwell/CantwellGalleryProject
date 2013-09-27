package com.cantwellcode.cantwellgallery;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Daniel on 9/27/13.
 */
public class SettingsDialog extends DialogFragment {

    private Button mColorButton;
    private Button mAlbumButton;

    public SettingsDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_dialog, container);
        mColorButton = (Button) view.findViewById(R.id.settingsButtonChangeColor);
        mAlbumButton = (Button) view.findViewById(R.id.settingsButtonChangeDefaultAlbum);
        
        setupButtonClick(mColorButton);
        setupButtonClick(mAlbumButton);

        getDialog().setTitle("Settings");

        return view;
    }
    
    private void setupButtonClick(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.settingsButtonChangeColor:

                        break;
                    case R.id.settingsButtonChangeDefaultAlbum:

                        break;
                    default:
                        break;
                }
            }
        });
    }
}
