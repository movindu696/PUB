package com.movindu.pub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.movindu.pub.Activities.SignIn.SignIn;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.MainActivity;
import com.movindu.pub.R;

public class ProfileActivity extends Activity {
TextView Name,Email;
LinearLayout Logout;
SharedpreferenceHelper sharedpreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Name = (TextView) findViewById(R.id.name);
        Email = (TextView) findViewById(R.id.email);
        Logout = (LinearLayout) findViewById(R.id.logout);
        sharedpreferenceHelper =  new SharedpreferenceHelper(ProfileActivity.this);
        fetchDetails();
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void fetchDetails(){
        String email = sharedpreferenceHelper.getString(Config.USER);
        String name = sharedpreferenceHelper.getString(Config.USER_NAME);
        Name.setText(name);
        Email.setText(email);
    }
    public void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences(Config.mySharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Intent i = new Intent(ProfileActivity.this, SignIn.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}