package com.movindu.pub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.Category;
import com.movindu.pub.Models.Datee;
import com.movindu.pub.Models.Expense;
import com.movindu.pub.Models.Income;
import com.movindu.pub.R;

import java.util.Calendar;

public class AddIncome extends Activity {
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    Datee date =  new Datee();
    private DatePicker datePicker;
    LinearLayout add_btn;
    EditText amount,note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        add_btn = (LinearLayout) findViewById(R.id.btn_add_income);
        amount = (EditText) findViewById(R.id.amount);
        note = (EditText) findViewById(R.id.note);
        amount.setFocusableInTouchMode(false);
        note.setFocusableInTouchMode(false);
        datePicker = findViewById(R.id.datePicker);
        sharedpreferenceHelper =  new SharedpreferenceHelper(AddIncome.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dateFromDatePicker();
        getDate();
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!amount.getText().toString().matches("")&& !note.getText().toString().matches("")){
                    addIncome(Double.valueOf(amount.getText().toString()),note.getText().toString());
                }

            }
        });
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setFocusableInTouchMode(true);
            }
        });
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setFocusableInTouchMode(true);
            }
        });

    }
    public void addIncome(double amount, String note){
        Config.showProgress(AddIncome.this);
        Income income =  new Income();
        income.setAmount(amount);
        income.setDate(date);
        income.setNote(note);
        income.setFb_user_id(sharedpreferenceHelper.getString(Config.FIREBASE_USER_ID));
        income.setEmail(sharedpreferenceHelper.getString(Config.USER));

        DatabaseReference expensesRef = databaseReference.child("income");
        String expensesId = databaseReference.child("income").push().getKey();
        expensesRef.child(expensesId).setValue(income).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Config.hideMsg();
                    Config.showSuccessMsg(AddIncome.this);
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Config.hideMsg();
                            onBackPressed();
                        }
                    }, 3000);
                } else {
                    Config.showFailedMsg(AddIncome.this);
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
        });
    }
    public void getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Datee date1 = new Datee(dayOfMonth,month,year);
        date = date1;
    }
    public void dateFromDatePicker(){

        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.setDay(dayOfMonth);
                        date.setMonth(monthOfYear + 1);
                        date.setYear(year);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(AddIncome.this, IncomeSummary.class);
        startActivity(i);
        finish();
    }
}