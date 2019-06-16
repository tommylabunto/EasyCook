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
import java.util.List;

// TODO create form to 1. add ingredient  2. add recipe

/**
 * Home page
 * Entire page is a scroll view and individual categories are recycler views
 */
public class HomeFragment extends Fragment {

    private RecyclerView meatRecyclerView;
    private RecyclerView grainsRecyclerView;
    private RecyclerView vegRecyclerView;
    private RecyclerView dairyRecyclerView;
    private RecyclerView saucesRecyclerView;
    private RecyclerView condRecyclerView;

    private RecyclerView.LayoutManager meatLayoutManager;
    private RecyclerView.LayoutManager grainsLayoutManager;
    private RecyclerView.LayoutManager vegLayoutManager;
    private RecyclerView.LayoutManager dairyLayoutManager;
    private RecyclerView.LayoutManager saucesLayoutManager;
    private RecyclerView.LayoutManager condLayoutManager;

    private RecyclerView.Adapter meatAdapter;
    private RecyclerView.Adapter grainsAdapter;
    private RecyclerView.Adapter vegAdapter;
    private RecyclerView.Adapter dairyAdapter;
    private RecyclerView.Adapter saucesAdapter;
    private RecyclerView.Adapter condAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<IngredientItem> ingredientList = new ArrayList<>();
        ArrayList<IngredientItem> meatList = new ArrayList<>();
        ArrayList<IngredientItem> grainsList = new ArrayList<>();
        ArrayList<IngredientItem> vegList = new ArrayList<>();
        ArrayList<IngredientItem> dairyList = new ArrayList<>();
        ArrayList<IngredientItem> saucesList = new ArrayList<>();
        ArrayList<IngredientItem> condList = new ArrayList<>();

        ingredientList.add(new IngredientItem("Meat","Duck"
                , "500", "1/1/2019"));
        ingredientList.add(new IngredientItem("Grains", "Rice"
                , "500", "1/1/2019"));
        ingredientList.add(new IngredientItem("Vegetable", "Xiao Bai Cai"
                , "500", "1/1/2019"));
        ingredientList.add(new IngredientItem("Dairy", "Milk"
                , "500", "1/1/2019"));
        ingredientList.add(new IngredientItem("Sauces", "Soya sauce"
                , "500", "1/1/2019"));
        ingredientList.add(new IngredientItem("Condiment", "Pepper"
                , "500", "1/1/2019"));

        // Create recycler view
        meatRecyclerView = view.findViewById(R.id.meat_recyclerView);
        grainsRecyclerView = view.findViewById(R.id.grains_recyclerView);
        vegRecyclerView = view.findViewById(R.id.veg_recyclerView);
        dairyRecyclerView = view.findViewById(R.id.dairy_recyclerView);
        saucesRecyclerView = view.findViewById(R.id.sauces_recyclerView);
        condRecyclerView = view.findViewById(R.id.cond_recyclerView);

        // Create layout manager
        meatLayoutManager = new LinearLayoutManager(getContext());
        grainsLayoutManager = new LinearLayoutManager(getContext());
        vegLayoutManager = new LinearLayoutManager(getContext());
        dairyLayoutManager = new LinearLayoutManager(getContext());
        saucesLayoutManager = new LinearLayoutManager(getContext());
        condLayoutManager = new LinearLayoutManager(getContext());

        // sort ingredients
        for (int i = 0; i < ingredientList.size(); i++) {
            switch (ingredientList.get(i).getIngredientType()) {
                case "Meat":
                    meatList.add(ingredientList.get(i));
                    continue;
                case "Grains":
                    grainsList.add(ingredientList.get(i));
                    continue;
                case "Vegetable":
                    vegList.add(ingredientList.get(i));
                    continue;
                case "Dairy":
                    dairyList.add(ingredientList.get(i));
                    continue;
                case "Sauces":
                    saucesList.add(ingredientList.get(i));
                    continue;
                case "Condiment":
                    condList.add(ingredientList.get(i));
                    continue;
            }
        }

        // Create an adapter and supply the data to be displayed.
        meatAdapter = new IngredientAdapter(getContext(), meatList);
        grainsAdapter = new IngredientAdapter(getContext(), grainsList);
        vegAdapter = new IngredientAdapter(getContext(), vegList);
        dairyAdapter = new IngredientAdapter(getContext(), dairyList);
        saucesAdapter = new IngredientAdapter(getContext(), saucesList);
        condAdapter = new IngredientAdapter(getContext(), condList);

        // Give the recycler view a default layout manager.
        meatRecyclerView.setLayoutManager(meatLayoutManager);
        grainsRecyclerView.setLayoutManager(grainsLayoutManager);
        vegRecyclerView.setLayoutManager(vegLayoutManager);
        dairyRecyclerView.setLayoutManager(dairyLayoutManager);
        saucesRecyclerView.setLayoutManager(saucesLayoutManager);
        condRecyclerView.setLayoutManager(condLayoutManager);

        // Connect the adapter with the recycler view.
        meatRecyclerView.setAdapter(meatAdapter);
        grainsRecyclerView.setAdapter(grainsAdapter);
        vegRecyclerView.setAdapter(vegAdapter);
        dairyRecyclerView.setAdapter(dairyAdapter);
        saucesRecyclerView.setAdapter(saucesAdapter);
        condRecyclerView.setAdapter(condAdapter);

        meatRecyclerView.setHasFixedSize(true);
        grainsRecyclerView.setHasFixedSize(true);
        vegRecyclerView.setHasFixedSize(true);
        dairyRecyclerView.setHasFixedSize(true);
        saucesRecyclerView.setHasFixedSize(true);
        condRecyclerView.setHasFixedSize(true);

        return view;
    }
}
