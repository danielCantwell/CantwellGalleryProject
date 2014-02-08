package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Daniel on 2/7/14.
 */
public class ColorPickerDialog extends DialogFragment {

    public interface ColorDialogListener {
        public void onColorDialogOkClick(ColorPickerDialog dialog);
        public void onColorDialogCancelClick(ColorPickerDialog dialog);
    }

    ColorDialogListener mListener;

    SeekBar rSeek;
    SeekBar gSeek;
    SeekBar bSeek;

    TextView rValue;
    TextView gValue;
    TextView bValue;

    ImageView colorBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.color_picker, null);
        builder.setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onColorDialogOkClick(ColorPickerDialog.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onColorDialogCancelClick(ColorPickerDialog.this);
                    }
                });


        rSeek = (SeekBar) v.findViewById(R.id.R_seekBar);
        gSeek = (SeekBar) v.findViewById(R.id.G_seekBar);
        bSeek = (SeekBar) v.findViewById(R.id.B_seekBar);

        rSeek.setProgress(0);
        gSeek.setProgress(221);
        bSeek.setProgress(255);

        rValue = (TextView) v.findViewById(R.id.R_value);
        gValue = (TextView) v.findViewById(R.id.G_value);
        bValue = (TextView) v.findViewById(R.id.B_value);

        colorBar = (ImageView) v.findViewById(R.id.colorBar);

        /* SeekBar Listeners */

        rSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rValue.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                colorBar.setBackgroundColor(Color.rgb(rSeek.getProgress(), gSeek.getProgress(), bSeek.getProgress()));
            }
        });

        gSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gValue.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                colorBar.setBackgroundColor(Color.rgb(rSeek.getProgress(), gSeek.getProgress(), bSeek.getProgress()));
            }
        });

        bSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bValue.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                colorBar.setBackgroundColor(Color.rgb(rSeek.getProgress(), gSeek.getProgress(), bSeek.getProgress()));
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ColorDialogListener) activity;
        } catch (ClassCastException e) {
            // If the activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
