package com.example.easycook.Settings;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteDocThread extends Thread {

    private String LOG_TAG = "DeleteDocThread";

    private String collectionPath;
    private String uid;

    public DeleteDocThread() {
        // empty constructor needed
    }

    public DeleteDocThread(String collectionPath, String uid) {
        this.collectionPath = collectionPath;
        this.uid = uid;
    }

    @Override
    public void run() {

        FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection(collectionPath)
                .whereEqualTo("author", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                document.getReference().delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(LOG_TAG, collectionPath + " DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(LOG_TAG, "Error deleting document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(LOG_TAG, "access document: task failed");
                        }
                    }
                });
    }
}
