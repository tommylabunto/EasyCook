package com.example.easycook.Home.Ingredient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.easycook.R;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private ArrayList<IngredientItem> ingredientList;
    private final LayoutInflater mInflater;

    /**
     * Creates a new custom view holder to hold the view to display in
     * the RecyclerView.
     */
    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientName;
        public TextView ingredientWeight;
        final IngredientAdapter mAdapter;

        public IngredientViewHolder(View itemView, IngredientAdapter adapter){
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient_name);
            ingredientWeight = itemView.findViewById(R.id.ingredient_weight);
            this.mAdapter = adapter;
        }
    }

    public IngredientAdapter(Context context, ArrayList<IngredientItem> ingredientList) {
        mInflater = LayoutInflater.from(context);
        this.ingredientList = ingredientList;

    }
    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     * represent an item.
     *
     * This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View
     * manually or inflate it from an XML layout file.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be reused to
     * display different items in the data set, it is a good idea to cache
     * references to sub views of the View to avoid unnecessary findViewById()
     * calls.
     */
    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.activity_ingredient_item, parent, false);
        return new IngredientViewHolder(mItemView, this);

    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder.itemView to
     * reflect the item at the given position.
     */
    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {

        IngredientItem currentItem = ingredientList.get(position);

        holder.ingredientName.setText(currentItem.getIngredientName());
        holder.ingredientWeight.setText("" + currentItem.getWeight());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
}
