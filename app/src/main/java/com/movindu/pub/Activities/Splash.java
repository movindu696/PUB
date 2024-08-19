package com.movindu.pub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Activities.SignIn.SignIn;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.ExpensesManagement.DefaultCategories;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.Category;
import com.movindu.pub.Models.User;
import com.movindu.pub.R;

public class Splash extends AppCompatActivity {
    SharedpreferenceHelper sharedpreferenceHelper;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        sharedpreferenceHelper =  new SharedpreferenceHelper(Splash.this);
       // sharedpreferenceHelper.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String app_state = sharedpreferenceHelper.getString(Config.APP_STATE);
        System.out.println(app_state);
        if(app_state.matches(Config.AppState.LOGGED.toString())){
            checkAuth();
        }else if(app_state.matches(Config.AppState.R_OTP_SENT.toString())){
            Runnable runnable =  new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, EnterOTP.class);
                    i.putExtra("2fa", false);
                    startActivity(i);
                    finish();
                }
            };
            Handler handler =  new Handler();
            handler.postDelayed(runnable,3000);
        }
//        else if(app_state.matches(Config.AppState.TWO_FA_OTP_SENT.toString())){
//            Runnable runnable =  new Runnable() {
//                @Override
//                public void run() {
//                    Intent i = new Intent(Splash.this, EnterOTP.class);
//                    i.putExtra("2fa", true);
//                    startActivity(i);
//                    finish();
//                }
//            };
//            Handler handler =  new Handler();
//            handler.postDelayed(runnable,3000);
//        }
        else if(app_state.matches(Config.AppState.REGISTERED.toString())){
            Runnable runnable =  new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, SignIn.class);
                    startActivity(i);
                    finish();
                }
            };
            Handler handler =  new Handler();
            handler.postDelayed(runnable,3000);
        }



        else {

            Runnable runnable =  new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, SignIn.class);
                    startActivity(i);
                    finish();
                }
            };
            Handler handler =  new Handler();
            handler.postDelayed(runnable,3000);
        }
    }

    private void checkAuth() {
        String email = sharedpreferenceHelper.getString(Config.USER);
        String pass = sharedpreferenceHelper.getString(Config.PASSWORD);
        Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        User user = childSnapshot.getValue(User.class);
                        if (user != null) {
                            if(user.getPassword().matches(pass)){
                                Log.d("FirebaseHelper", "Login success");
                                String userID = childSnapshot.getKey();
                                sharedpreferenceHelper.saveString(Config.FIREBASE_USER_ID,userID);
                                Runnable runnable =  new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(Splash.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                };
                                Handler handler =  new Handler();
                                handler.postDelayed(runnable,3000);
                            }else{
                                Runnable runnable =  new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(Splash.this, SignIn.class);
                                        startActivity(i);
                                        finish();
                                    }
                                };
                                Handler handler =  new Handler();
                                handler.postDelayed(runnable,3000);
                            }
                        } else {
                            Runnable runnable =  new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(Splash.this, SignIn.class);
                                    startActivity(i);
                                    finish();
                                }
                            };
                            Handler handler =  new Handler();
                            handler.postDelayed(runnable,3000);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }
    public void addInitialCategory(int id,String name){
        Category category =  new Category();
        category.setId(id);
        category.setName(name);
    }
}