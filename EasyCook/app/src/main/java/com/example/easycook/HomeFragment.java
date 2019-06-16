package com.example.easycook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<IngredientItem> ingredientList = new ArrayList<>();
        ingredientList.add(new IngredientItem("Meat","Duck"
                , "500", "1/1/2019"));

        // Create recycler view.
        mRecyclerView = view.findViewById(R.id.meat_recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        // Create an adapter and supply the data to be displayed.
        mAdapter = new IngredientAdapter(getContext(),ingredientList);


        // Give the recycler view a default layout manager.
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Connect the adapter with the recycler view.
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);



        return view;
    }


}
