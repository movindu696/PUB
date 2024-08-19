package com.movindu.pub.Activities;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.movindu.pub.Email.GMail;
import com.movindu.pub.IComResponse;
import com.movindu.pub.Models.User;
import com.movindu.pub.R;

public class EnterOTPRecovery extends Activity {
    EditText otp;
    LinearLayout submit;
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otprecovery);
        initialize();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sent_otp = sharedpreferenceHelper.getInt(Config.OTP);
                String entered_otp = otp.getText().toString();
                if(entered_otp.matches(String.valueOf(sent_otp))){
                    resendPass();
                }else{
                    System.out.println("otp mismatched");
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(EnterOTPRecovery.this, SignUp.class);
        startActivity(i);
        finish();
    }
    public void initialize(){
        otp = (EditText) findViewById(R.id.otp);
        submit = (LinearLayout) findViewById(R.id.btn_submit);
        sharedpreferenceHelper =  new SharedpreferenceHelper(EnterOTPRecovery.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }
    private void resendPass(){
        Query query = databaseReference.child("users").orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
//
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        User user = childSnapshot.getValue(User.class);
                        String pass = user.getPassword();
                        Thread backgroundThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                IComResponse iComResponse = new IComResponse() {
                                    @Override
                                    public void onSuccess() {
                                        IComResponse.super.onSuccess();
                                        Intent i = new Intent(EnterOTPRecovery.this, SignIn.class);
                                        startActivity(i);
                                        finish();
                                    }

                                    @Override
                                    public void onFailed(String msg) {
                                        IComResponse.super.onFailed(msg);
                                        Intent i = new Intent(EnterOTPRecovery.this, RecoverPassActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                };

                                GMail.sendEmail(sharedpreferenceHelper.getString(Config.USER),
                                        Config.OTP_SUBJECT,"This is your password "+String.valueOf(pass),iComResponse);

                            }
                        });
                        backgroundThread.start();
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