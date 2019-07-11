package com.example.easycook.Home.Recipe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easycook.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class RecipeAdapter extends FirestoreRecyclerAdapter<RecipeItem, RecipeAdapter.RecipeViewHolder> {

    private final String LOG_TAG = "RecipeAdapter";

    private RecipeAdapter.OnItemClickListener listener;

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
    protected void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position, @NonNull RecipeItem recipeItem) {

        recipeViewHolder.recipeName.setText(recipeItem.getName());

        if (!recipeItem.getImageLink().isEmpty()) {
            Picasso.get().load(recipeItem.getImageLink()).into(recipeViewHolder.recipeImage);
        }
    }

    public void deleteItem(int position) {

        RecipeItem recipe = getSnapshots().getSnapshot(position).toObject(RecipeItem.class);

        // delete image on storage
        if (!recipe.getPath().isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child(recipe.getPath());

            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(LOG_TAG, "deleted image successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(LOG_TAG, "failed to delete image");
                }
            });
        }

        // delete document reference on firestore
        getSnapshots().getSnapshot(position)
                .getReference().delete();
    }

    /**
     * Creates a new custom view holder to hold the view to display in
     * the RecyclerView.
     */
    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeName;
        public ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeImage = itemView.findViewById(R.id.recipe_image);

            // when recycler view is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        } else {
                            Log.d(LOG_TAG, "listener is null");
                        }
                    } else {
                        Log.d(LOG_TAG, "no position");
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

