package com.cantwellcode.cantwellgallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 9/6/13.
 */
public class SmallQuickBarFragment extends Fragment {


    private ImageView mCurrentItemImage;
    private TextView mCurrentItemText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.small_quick_bar,container,false);
        mCurrentItemImage = (ImageView) root.findViewById(R.id.quickBarCurrentItemImage);
        setupDrop(mCurrentItemImage);
        return root;
    }

    private void setupDrop(final View v) {

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                switch (dragEvent.getAction()) {

                    // When a view drag starts, imageView turns blue
                    case DragEvent.ACTION_DRAG_STARTED:
                        Toast dragStartedToast = Toast.makeText(getActivity(), "Drag Started", Toast.LENGTH_SHORT);
                        dragStartedToast.show();
                        return true;

                    // When the view is being held over the imageView, the imageView turns blue
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Toast dragEnteredToast = Toast.makeText(getActivity(), "Drag Entered", Toast.LENGTH_SHORT);
                        dragEnteredToast.show();
                        break;

                    // When the view is exited, but not dropped on the imageView, the imageView turns yellow
                    case DragEvent.ACTION_DRAG_EXITED:
                        Toast dragExitedToast = Toast.makeText(getActivity(), "Drag Exited", Toast.LENGTH_SHORT);
                        dragExitedToast.show();
                        break;

                    // When the view is dropped on the imageView, process the drop
                    case DragEvent.ACTION_DROP:
                        Toast dropToast = Toast.makeText(getActivity(), "Drop", Toast.LENGTH_SHORT);
                        dropToast.show();
                        break;
                }
                return false;
            }
        });
    }

}
