package com.movindu.pub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.movindu.pub.Email.GMail;
import com.movindu.pub.IComResponse;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.User;
import com.movindu.pub.R;

public class RecoverPassActivity extends Activity {
EditText email;
LinearLayout send_btn;
    SharedpreferenceHelper sharedpreferenceHelper;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pass);

        email = (EditText) findViewById(R.id.email);
        send_btn = (LinearLayout) findViewById(R.id.btn_send);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedpreferenceHelper =  new SharedpreferenceHelper(RecoverPassActivity.this);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().matches("")){
                    checkMailExists(email.getText().toString());
                }

            }
        });


    }
    public void checkMailExists(String email){
        Config.showProgress(RecoverPassActivity.this);
        Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
                                    Intent i = new Intent(RecoverPassActivity.this, EnterOTPRecovery.class);
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


                } else {
                   Config.hideMsg();
                   Config.showFailedMsg(RecoverPassActivity.this);
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Config.hideMsg();
                            onBackPressed();
                        }
                    }, 3000);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(RecoverPassActivity.this,SignIn.class);
        startActivity(i);
        finish();
    }
}