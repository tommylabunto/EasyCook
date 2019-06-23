package com.example.easycook.Home.Recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easycook.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecipeAdapter extends FirestoreRecyclerAdapter<RecipeItem, RecipeAdapter.RecipeViewHolder> {

    public RecipeAdapter(@NonNull FirestoreRecyclerOptions<RecipeItem> options) {
        super(options);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recipe_item,
                parent, false);
        return new RecipeViewHolder(mItemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i, @NonNull RecipeItem recipeItem) {

        recipeViewHolder.recipeName.setText(recipeItem.getName());
    }

    /**
     * Creates a new custom view holder to hold the view to display in
     * the RecyclerView.
     */
    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeName;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
        }
    }
}

