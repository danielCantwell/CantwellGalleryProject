package com.cantwellcode.cantwellgallery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 8/16/13.
 * Displays a subset of directories from a database provided by the host activity.
 * Host activity must provide a Uri for the database, the column name for the
 * directory id and the Active and Inactive views to be used for display, along with their
 * associated bindings.
 * This fragment will then keep a modifiable list of directories to display
 */
public class QuickBarFragment extends Fragment{
    private static final String TAG = "QuickBarFragment";

    public class Item{
        public int id;
        public String displayName;
    }

    public class QuickBarAdapter extends BaseAdapter{

        private List<Item> mItems;

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mItems.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }

        public List<Item> swap(List<Item> newItems){
            List<Item> old = mItems;
            mItems = newItems;
            return old;
        }
    }

    public interface QuickBarCallbacks{
         public void onQuickBarButtonClick();
    }

    private QuickBarCallbacks mListener;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<Item> mItems;



    /**
     * Called when the fragment is attached to the host activity.
     * Ensures the host activity implements the fragment callbacks and sets the mListener member.
     * @param activity: The host activity
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mListener = (QuickBarCallbacks) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement QuickBarCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<Item>();
    }

    /**
     * Called when the fragment view is created.
     * Initializes the Adpter and ListView associated with the fragment.
     * @param inflater: Used to inflate the xml file describing the fragment view.
     * @param container: ViewGroup in the activity that the fragment belongs to.
     * @param savedInstanceState: : null on creation.  If the fragment is pushed onto the backstack
     *                          this can be used to save certain parameters for reinstantiation
     * @return: The view associated with this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view for this fragment from the associated xml file.
        final View root = inflater.inflate(R.layout.quick_bar, container, false);
        mListView = (ListView) root.findViewById(R.id.quickBarListView);
        mListAdapter = new ArrayAdapter<Item>(getActivity(),R.layout.activity_main,mItems);
        return root;
    }

    public void updateList(List<Item> newItems){
        mItems = newItems;
    }


}
