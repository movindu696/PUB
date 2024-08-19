package com.movindu.pub.Activities.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Activities.EnterOTP;
import com.movindu.pub.Activities.RecoverPassActivity;
import com.movindu.pub.Activities.SignUp.SignUp;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.Email.GMail;
import com.movindu.pub.IComResponse;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.User;
import com.movindu.pub.R;

public class SignIn extends Activity {
    EditText email,password;
    LinearLayout signIn;
    TextView signUp,recover;
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initialize();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(),password.getText().toString());

            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
                finish();

            }
        });
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, RecoverPassActivity.class);
                startActivity(i);
                finish();

            }
        });

    }
    public void initialize(){
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signUp = (TextView) findViewById(R.id.signUp);
        recover = (TextView) findViewById(R.id.recover);
        signIn = (LinearLayout) findViewById(R.id.btn_sign_in);
        sharedpreferenceHelper =  new SharedpreferenceHelper(SignIn.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }
    public void signIn(String email,String pass){
        if(!email.matches("") && !pass.matches("")){
            Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);

                            if(user !=null){
                                if(user.getPassword().matches(pass)){
                                    //call 2FA here
                                    if(Config.is2FAEnabled(SignIn.this)){
                                        /*******************/
                                        int otp = Config.generateRandomNumber(1000,9999);
                                        Thread backgroundThread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                IComResponse iComResponse = new IComResponse() {
                                                    @Override
                                                    public void onSuccess() {
                                                        IComResponse.super.onSuccess();
                                                        sharedpreferenceHelper.saveInt(Config.OTP,otp);
                                                        sharedpreferenceHelper.saveString(Config.APP_STATE,Config.AppState.TWO_FA_OTP_SENT.toString());
                                                        sharedpreferenceHelper.saveString(Config.USER,email);
                                                        sharedpreferenceHelper.saveString(Config.PASSWORD,pass);
                                                        String name = dataSnapshot.child("name").getValue(String.class);
                                                        sharedpreferenceHelper.saveString(Config.USER_NAME,name);
                                                        String userID = childSnapshot.getKey();
                                                        sharedpreferenceHelper.saveString(Config.FIREBASE_USER_ID,userID);
                                                        Intent i = new Intent(SignIn.this, EnterOTP.class);
                                                        i.putExtra("2fa", true);
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
                                        /*******************/

                                    }else{
                                        /*********************/
                                        Log.d("FirebaseHelper", "Login success");
                                        String name = dataSnapshot.child("name").getValue(String.class);
                                        sharedpreferenceHelper.saveString(Config.APP_STATE,Config.AppState.LOGGED.toString());
                                        sharedpreferenceHelper.saveString(Config.USER,email);
                                        sharedpreferenceHelper.saveString(Config.PASSWORD,pass);
                                        sharedpreferenceHelper.saveString(Config.USER_NAME,name);
                                        String userID = childSnapshot.getKey();
                                        sharedpreferenceHelper.saveString(Config.FIREBASE_USER_ID,userID);
                                        Intent i = new Intent(SignIn.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                        /*********************/
                                    }
                                }else{
                                    System.out.println("Wrong pass");
                                    errorDialog("Wrong password !");
                                }
                            }
                        }


                    } else {
                        //no user. please register
                        System.out.println("No user");
                        errorDialog("No registered user for this email !");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println(error.toString());
                    //sytm
                    System.out.println(error.toString());
                }
            });
        }else{
            //plese don't leave empty fields here
            errorDialog("please don't leave empty fields here");
        }
    }
    public void errorDialog(String error){


        dialog = new Dialog(SignIn.this);
        dialog.setContentView(R.layout.error_dialog);
        dialog.setTitle("error");
        dialog.setCancelable(false);
        LinearLayout OK = (LinearLayout) dialog.findViewById(R.id.ok);
        TextView errmsg = (TextView) dialog.findViewById(R.id.error_msg);
        errmsg.setText(error);

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}