package com.example.bakingtime.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.bakingtime.R;
import com.example.bakingtime.adapters.IngredientAdapter;
import com.example.bakingtime.data.Ingredient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredientFragment#} factory method to
 * create an instance of this fragment.
 */
public class IngredientFragment extends Fragment {

    private static final String TAG = IngredientFragment.class.getSimpleName();
    private static final String ARG_INGREDIENT_LIST = "argIngredient";
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private List<Ingredient> ingredients;


    public IngredientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ingredients Parameter 1.
     * @return A new instance of fragment IngredientFragment.
     */
    public static IngredientFragment newInstance(List<Ingredient> ingredients) {
        IngredientFragment fragment = new IngredientFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_INGREDIENT_LIST, (ArrayList<? extends Parcelable>) ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ingredients = getArguments().getParcelableArrayList(ARG_INGREDIENT_LIST);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.list, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        IngredientAdapter ingredientAdapter = new IngredientAdapter(getContext(), ingredients);

        loadingIndicator.setVisibility(View.INVISIBLE);

        recyclerView.setAdapter(ingredientAdapter);

        return view;
    }

}
