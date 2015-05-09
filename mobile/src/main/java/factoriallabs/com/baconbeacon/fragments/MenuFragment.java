package factoriallabs.com.baconbeacon.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import factoriallabs.com.baconbeacon.R;
import factoriallabs.com.baconbeacon.menu.Menu;
import factoriallabs.com.baconbeacon.menu.MenuDataSetAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ProgressBar empty;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        // Inflate the layout for this fragment
        int mScrollPosition = 0;
        if (savedInstanceState != null) {
            //restore state
            list = savedInstanceState.getParcelableArrayList("menu");
            mScrollPosition = savedInstanceState.getInt("currentIndex");
            hideProgressBar();
        }else{
            list = new ArrayList<>();
        }
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = setRecyclerViewLayoutManager(mRecyclerView);
        mAdapter = new MenuDataSetAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setDataSet(list);
        empty = (ProgressBar) v.findViewById(R.id.empty);
        //scroll to saved position
        int count = mLayoutManager.getChildCount();
        if(mScrollPosition != RecyclerView.NO_POSITION && mScrollPosition < count){
            mLayoutManager.scrollToPosition(mScrollPosition);
        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private MenuDataSetAdapter mAdapter;
    private ArrayList<Menu> list;
    private SearchView mSearch;
    private MenuItem mSearchItem;
    private RecyclerView mRecyclerView;
    private boolean sorting_order = true;


    private void refreshContent() {
        //Download a list of Menus, and set a callback to this class
        //new DataDownloadTask(this,this).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(list.size() == 0)
            refreshContent();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the list of downloaded employees
        savedInstanceState.putParcelableArrayList("Menus", list);
        //save currently visible index on recyclerview
        savedInstanceState.putInt("currentIndex", ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    /**
     * Set RecyclerView's LayoutManager
     */
    public LinearLayoutManager setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
        return linearLayoutManager;
    }

    /**
     * Callback that is called when the Menu dataset has been downloaded
     * @param array list of Menus

    @Override
    public void onDownloaded(ArrayList<Menu> array) {
        list = array;
        mAdapter.setDataSet(array);
        mAdapter.notifyDataSetChanged();
        hideProgressBar();
    }*/

    private void hideProgressBar() {
        empty.setVisibility(View.GONE);
    }

}
