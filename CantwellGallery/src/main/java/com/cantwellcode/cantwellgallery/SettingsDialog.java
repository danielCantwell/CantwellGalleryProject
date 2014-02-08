package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Daniel on 9/27/13.
 */
public class SettingsDialog extends DialogFragment {

    public interface SettingsDialogListener {
        public void onSettingsDialogColorClick(DialogFragment dialog);
    }

    SettingsDialogListener mListener;

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
                        mListener.onSettingsDialogColorClick(SettingsDialog.this);
                        break;
                    case R.id.settingsButtonChangeDefaultAlbum:

                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SettingsDialogListener) activity;
        } catch (ClassCastException e) {
            // If the activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
