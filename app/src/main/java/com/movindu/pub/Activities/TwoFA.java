package com.movindu.pub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.R;

public class TwoFA extends AppCompatActivity {

    ToggleButton togglebutton;
    TextView textview;
    SharedpreferenceHelper sphelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_fa);
        // Initialize the toggle button and text view
        togglebutton = (ToggleButton) findViewById(R.id.button_toggle);
//        Get 2FA STATUS FROM SHARED PREFERENCE
        sphelper= new SharedpreferenceHelper(TwoFA.this);

        togglebutton.setChecked(sphelper.getBoolean(Config.TWO_FACTOR_STATUS_KEY));
        textview = (TextView) findViewById(R.id.textView1);

    }
    // Method is called when the toggle button is clicked
    public void onToggleClick(View view) {
         sphelper= new SharedpreferenceHelper(TwoFA.this);
        if (togglebutton.isChecked()) {
            sphelper.saveBoolean(Config.TWO_FACTOR_STATUS_KEY,true);
            textview.setText("Status ON 2FA");
        } else {
            sphelper.saveBoolean(Config.TWO_FACTOR_STATUS_KEY,false);
            textview.setText("Status OFF 2FA");
        }
    }
}