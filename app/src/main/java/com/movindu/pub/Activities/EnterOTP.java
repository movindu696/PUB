package com.movindu.pub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Activities.SignIn.SignIn;
import com.movindu.pub.Activities.SignUp.SignUp;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.ExpensesManagement.DefaultCategories;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.User;
import com.movindu.pub.R;

public class EnterOTP extends Activity {
    EditText otp;
    LinearLayout submit;
    boolean is2FA;
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        Intent intent = getIntent();
        is2FA = intent.getBooleanExtra("2fa", false);
        initialize();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sent_otp = sharedpreferenceHelper.getInt(Config.OTP);
                String entered_otp = otp.getText().toString();
                if(entered_otp.matches(String.valueOf(sent_otp))){
                    if(is2FA){
                        signIn();
                    }else{
                        userRegister(sharedpreferenceHelper.getString(Config.USER_NAME),
                                sharedpreferenceHelper.getString(Config.USER),
                                sharedpreferenceHelper.getString(Config.PASSWORD));
                    }

                }else{
                    System.out.println("otp mismatched");
                    Config.errorDialog(EnterOTP.this,"otp mismatched");
                }
            }
        });
    }

    private void signIn() {
        Log.d("FirebaseHelper", "Login success");
        sharedpreferenceHelper.saveString(Config.APP_STATE,Config.AppState.LOGGED.toString());
        Intent i = new Intent(EnterOTP.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(EnterOTP.this, SignUp.class);
        startActivity(i);
        finish();
    }
    public void initialize(){
        otp = (EditText) findViewById(R.id.otp);
        submit = (LinearLayout) findViewById(R.id.btn_submit);
        sharedpreferenceHelper =  new SharedpreferenceHelper(EnterOTP.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }
    private void userRegister(String name,String email,String pass){
        User user =  new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(pass);
        Query query = databaseReference.child("users").orderByChild("email").equalTo(user.getEmail());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("FirebaseHelper", "User with the same email already exists");
                    Intent i = new Intent(EnterOTP.this, SignUp.class);
                    startActivity(i);
                    finish();
                } else {
                    String userId = databaseReference.child("users").push().getKey();
                    databaseReference.child("users").child(userId).setValue(user);
//                    save default defaultCategories  start
                    DefaultCategories defaultCategories = new DefaultCategories();
                    defaultCategories.saveDefaultCategory(sharedpreferenceHelper.getString(Config.USER));
//                    save default defaultCategories  end
                    sharedpreferenceHelper.saveString(Config.APP_STATE,Config.AppState.REGISTERED.toString());
                    Log.d("FirebaseHelper", "User saved successfully");
                    Intent i = new Intent(EnterOTP.this, SignIn.class);
                    startActivity(i);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }
}