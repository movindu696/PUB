package com.movindu.pub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Activities.SignUp.SignUp;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.Email.GMail;
import com.movindu.pub.ExpensesManagement.DefaultCategories;
import com.movindu.pub.IComResponse;
import com.movindu.pub.R;

import java.util.ArrayList;
import java.util.List;

public class AddCategory extends AppCompatActivity {
    EditText category_name;
    LinearLayout add_btn;
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    List<String> categories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcategory);
        initialize();
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory(category_name.getText().toString());
            }
        });
    }
    public void initialize(){
        category_name = (EditText) findViewById(R.id.category);
        add_btn = (LinearLayout) findViewById(R.id.btn_add_category);
        sharedpreferenceHelper =  new SharedpreferenceHelper(AddCategory.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public void addCategory(String category) {
        String userId2 = sharedpreferenceHelper.getString(Config.FIREBASE_USER_ID);
        System.out.println(userId2);

        Query query = databaseReference.child("category").child(userId2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isCategoryExist = false;
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                        categories.add(childSnapshot.getValue(String.class));
                        if(childSnapshot.getValue(String.class).matches(category)){
                           isCategoryExist = true;
                        }

                    }
                    if(!isCategoryExist){
                        databaseReference.child("category").child(userId2).push().setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    System.out.println("successfully added");
                                } else {
                                    System.out.println("Added failed");
                                }
                            }
                        });
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }


}