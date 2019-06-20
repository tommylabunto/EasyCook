package com.example.easycook.Home.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.easycook.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ArrayList<RecipeItem> recipeList;
    private final LayoutInflater mInflater;

    /**
     * Creates a new custom view holder to hold the view to display in
     * the RecyclerView.
     */
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeName;
        final RecipeAdapter mAdapter;

        public RecipeViewHolder(View itemView, RecipeAdapter adapter) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
            this.mAdapter = adapter;
        }
    }

    public RecipeAdapter(Context context, ArrayList<RecipeItem> recipeList) {
        mInflater = LayoutInflater.from(context);
        this.recipeList = recipeList;

    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     * represent an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View
     * manually or inflate it from an XML layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be reused to
     * display different items in the data set, it is a good idea to cache
     * references to sub views of the View to avoid unnecessary findViewById()
     * calls.
     */
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.activity_recipe_item, parent, false);
        return new RecipeAdapter.RecipeViewHolder(mItemView, this);

    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder.itemView to
     * reflect the item at the given position.
     */
    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeViewHolder holder, int position) {

        RecipeItem currentItem = recipeList.get(position);

        holder.recipeName.setText(currentItem.getIngredient());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}

