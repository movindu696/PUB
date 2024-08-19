package com.movindu.pub.Activities;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.Email.GMail;
import com.movindu.pub.IComResponse;
import com.movindu.pub.Models.Category;
import com.movindu.pub.Models.Datee;
import com.movindu.pub.Models.Expense;
import com.movindu.pub.R;

import java.util.Calendar;

public class AddExpenses extends Activity {
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    Spinner spinner;
    Datee date =  new Datee();
    private DatePicker datePicker;
    private int cate_id = 0;
    LinearLayout add_btn;
    EditText amount,note;
    double total_expenses,total_income;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        Intent intent = getIntent();
        total_expenses = intent.getDoubleExtra("totalEXP",0.0);
        total_income = intent.getDoubleExtra("totalINC",0.0);

        spinner = findViewById(R.id.spinner);
        add_btn = (LinearLayout) findViewById(R.id.btn_add_expenses);
        amount = (EditText) findViewById(R.id.amount);
        note = (EditText) findViewById(R.id.note);
        amount.setFocusableInTouchMode(false);
        note.setFocusableInTouchMode(false);
        datePicker = findViewById(R.id.datePicker);
        sharedpreferenceHelper =  new SharedpreferenceHelper(AddExpenses.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dateFromDatePicker();
        getDate();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.category_dropdown, Config.CATEGORIES);
        adapter.setDropDownViewResource(R.layout.category_dropdown);
        spinner.setAdapter(adapter);

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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cate_id = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!amount.getText().toString().matches("") && !note.getText().toString().matches("")){
                    addExpenses(Double.valueOf(amount.getText().toString()),note.getText().toString());
                } else {
                    //add error message here such as "Fill all blanks"
                    Config.errorDialog(AddExpenses.this,"Please Fill all fields !");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void addExpenses(double amount, String note){

        if(amount + total_expenses < total_income) {
            Config.showProgress(AddExpenses.this);
            Category category = new Category();
            category.setId(cate_id);
            category.setName(Config.CATEGORIES[cate_id]);
            Expense expense = new Expense();
            expense.setCategory(category);
            expense.setAmount(amount);
            expense.setDate(date);
            expense.setNote(note);
            expense.setFb_user_id(sharedpreferenceHelper.getString(Config.FIREBASE_USER_ID));
            expense.setEmail(sharedpreferenceHelper.getString(Config.USER));

            DatabaseReference expensesRef = databaseReference.child("expenses");
            String expensesId = databaseReference.child("expenses").push().getKey();
            expensesRef.child(expensesId).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Config.hideMsg();
                        Config.showSuccessMsg(AddExpenses.this);
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(total_income-(amount + total_expenses)< Config.ACCOUNT_LIMIT){
                                    IComResponse iComResponse = new IComResponse() {
                                        @Override
                                        public void onSuccess() {
                                            IComResponse.super.onSuccess();
                                        }

                                        @Override
                                        public void onFailed(String msg) {
                                            IComResponse.super.onFailed(msg);
                                        }
                                    };
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                GMail.sendEmail(sharedpreferenceHelper.getString(Config.USER),
                                                        "Expense limit exceed! ","Expense limit exceed!",iComResponse);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    thread.start();

                                }
                                Config.hideMsg();
                                onBackPressed();
                            }
                        }, 3000);
                    } else {
                        Config.showFailedMsg(AddExpenses.this);
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
        }else{
            //show error msg here
            Config.errorDialog(AddExpenses.this,"Expense amount is greater than your balance");

        }


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
        Intent i = new Intent(AddExpenses.this, ExpensesDetail.class);
        startActivity(i);
        finish();
    }
}