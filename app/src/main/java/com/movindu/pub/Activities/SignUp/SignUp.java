package com.movindu.pub.Activities.SignUp;

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
import com.movindu.pub.Activities.EnterOTP;
import com.movindu.pub.Activities.SignIn.SignIn;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.Email.GMail;
import com.movindu.pub.IComResponse;
import com.movindu.pub.MainActivity;
import com.movindu.pub.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends Activity {
    EditText name,email,password;
    LinearLayout signUp;
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SignUp.this, SignIn.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPassLength(password.getText().toString())&& !name.getText().toString().matches("")){
                    if(Config.isValidEmail(email.getText().toString())){
                        checkUserIsExistOrNot(email.getText().toString(),name.getText().toString(),password.getText().toString());
                    }else{
                        System.out.println("Enter valid email toast");
                    }
                }else{
                    System.out.println("enter 8 lenght pass toast");
                    Config.errorDialog(SignUp.this, "Enter at least 8 characters");
                }
            }
        });


    }
    public void initialize(){
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signUp = (LinearLayout) findViewById(R.id.btn_sign_up);
        sharedpreferenceHelper =  new SharedpreferenceHelper(SignUp.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }
    private boolean checkPassLength(String pass){
        if(pass.length() == 8){
            return true;
        }
        else{
            return false;
        }
    }
    public void checkUserIsExistOrNot(String email,String name,String pass){
        Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("FirebaseHelper", "User with the same email already exists");
                    //show error msg here
                } else {
                    int otp = Config.generateRandomNumber(1000,9999);
                    Thread backgroundThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            IComResponse iComResponse = new IComResponse() {
                                @Override
                                public void onSuccess() {
                                    IComResponse.super.onSuccess();
                                    sharedpreferenceHelper.saveInt(Config.OTP,otp);
                                    sharedpreferenceHelper.saveInt(Config.APP_STATE,1);
                                    sharedpreferenceHelper.saveString(Config.USER,email);
                                    sharedpreferenceHelper.saveString(Config.PASSWORD,pass);
                                    sharedpreferenceHelper.saveString(Config.USER_NAME,name);
                                    Intent i = new Intent(SignUp.this, EnterOTP.class);
                                    i.putExtra("2fa", false);
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void onFailed(String msg) {
                                    IComResponse.super.onFailed(msg);
                                    System.out.println(msg);
                                }
                            };

                            GMail.sendEmail(email,
                                    Config.OTP_SUBJECT,Config.OTP_BODY+String.valueOf(otp),iComResponse);

                        }
                    });
                    backgroundThread.start();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }

}