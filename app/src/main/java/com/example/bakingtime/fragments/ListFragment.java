package com.example.bakingtime.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.bakingtime.R;
import com.example.bakingtime.adapters.ListAdapter;
import com.example.bakingtime.data.RecipeEvent;
import com.example.bakingtime.data.Step;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ListFragment extends Fragment {

    private static final String TAG = ListFragment.class.getSimpleName();
    // To restore the recyclerView position
    private static int recyclerViewPosition = -1;
    private static int top = -1;
    ProgressBar loadingIndicator;
    private List<Step> steps;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private OnFragmentInteractionListener mListener;
    View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            // Position of the adapter
            int position = viewHolder.getAdapterPosition();
            if (mListener != null) {
                mListener.onFragmentInteraction(position);
            }
            // From here you can also get ItemId, ItemViewType and itemView
            // viewHolder.getItemId();
            // viewHolder.getItemViewType();
            // viewHolder.itemView;
        }
    };


    public ListFragment() {
        // Required empty public constructor
    }

    @Subscribe
    public void onRecipeEvent(RecipeEvent event) {
        Log.d(TAG, "Inside onRecipeEvent");
        steps = event.eventGetSteps();

        listAdapter = new ListAdapter(getContext(), steps);

        loadingIndicator.setVisibility(View.INVISIBLE);

        recyclerView.setAdapter(listAdapter);

        // Create and set OnItemClickListener to the adapter.
        listAdapter.setOnItemClickListener(onItemClickListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingIndicator = rootView.findViewById(R.id.loading_indicator);

        Log.d(TAG, "Inside onCreateView");

        return rootView;
    }


    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerViewPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        View view = recyclerView.getChildAt(0);
        if (view == null) {
            top = 0;
        } else {
            top = view.getTop() - recyclerView.getPaddingTop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerViewPosition != -1) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(recyclerViewPosition, top);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int position);
    }
}





