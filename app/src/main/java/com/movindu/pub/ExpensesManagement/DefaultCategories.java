package com.movindu.pub.ExpensesManagement;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.IComResponse;
import com.movindu.pub.Models.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCategories {
    private DatabaseReference databaseReference;
    String [] defaultCategory = {"Food & Beverages", "Transport", "Education", "Entertainment"};

    public DefaultCategories() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveDefaultCategory(String email){
        Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        //User user = childSnapshot.getValue(User.class);
                         String userId = childSnapshot.getKey();
                        databaseReference.child("category").child(userId).push().setValue(defaultCategory[0]);
                        databaseReference.child("category").child(userId).push().setValue(defaultCategory[1]);
                        databaseReference.child("category").child(userId).push().setValue(defaultCategory[2]);
                        databaseReference.child("category").child(userId).push().setValue(defaultCategory[3]);


                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }

}
