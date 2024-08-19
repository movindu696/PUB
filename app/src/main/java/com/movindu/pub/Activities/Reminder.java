package com.movindu.pub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.Datee;
import com.movindu.pub.Models.Expense;
import com.movindu.pub.Models.Income;
import com.movindu.pub.R;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

public class Reminder extends AppCompatActivity {
    private double totalIncome = 0.0;
    private double totalExpense = 0.0;
    private int monthlyExCnt = 0;
    private int monthlyInCnt = 0;
    private double monthlyExAmt = 0.0;
    private double monthlyInAmt = 0.0;
    TextView Heading,Balance,ExCnt,InCnt,ExAmt,InAmt;
    LinearLayout Home,ReportView;
    private DatabaseReference databaseReference,databaseReferenceIncome;
    SharedpreferenceHelper sharedpreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        initialize();
        final CountDownLatch latch = new CountDownLatch(1);
        getAllIncome(latch);

        // Wait for getAllIncome to complete
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await(); // Wait for the latch to count down to 0
                    // Run getAllExpenses on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAllExpenses();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Reminder.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void initialize() {
        Heading = (TextView) findViewById(R.id.heading);
        Balance = (TextView) findViewById(R.id.balance);
        ExCnt = (TextView) findViewById(R.id.expense_count);
        InCnt = (TextView) findViewById(R.id.income_count);
        ExAmt = (TextView) findViewById(R.id.expense_amount);
        InAmt = (TextView) findViewById(R.id.income_amount);
        ReportView = (LinearLayout) findViewById(R.id.report);
        Home = (LinearLayout) findViewById(R.id.btn_download_pdf);
        sharedpreferenceHelper = new SharedpreferenceHelper(Reminder.this);
    }

    public void getAllIncome(final CountDownLatch latch) {
        databaseReferenceIncome = FirebaseDatabase.getInstance().getReference("income");
        Query query = databaseReferenceIncome.orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle the data from the query
                Calendar calendar = Calendar.getInstance();
                // Get the current month (January is 0, December is 11)
                int current_month = calendar.get(Calendar.MONTH) + 1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Income userIn = snapshot.getValue(Income.class);
                    totalIncome = totalIncome + userIn.getAmount();
                    Datee date = userIn.getDate();
                    int month = date.getMonth();
                    if (month == current_month) {
                        monthlyInCnt = monthlyInCnt + 1;
                        monthlyInAmt = monthlyInAmt + userIn.getAmount();
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                latch.countDown();
            }
        });

    }

    public void getAllExpenses() {
        databaseReference = FirebaseDatabase.getInstance().getReference("expenses");
        Query query = databaseReference.orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle the data from the query
                Calendar calendar = Calendar.getInstance();
                // Get the current month (January is 0, December is 11)
                int current_month = calendar.get(Calendar.MONTH) + 1;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense userEx = snapshot.getValue(Expense.class);
                    totalExpense = totalExpense + userEx.getAmount();
                    Datee date = userEx.getDate();
                    int month = date.getMonth();
                    if (month == current_month) {
                        monthlyExCnt = monthlyExCnt + 1;
                        monthlyExAmt = monthlyExAmt + userEx.getAmount();
                    }

                }

                Heading.setText("Summary for month " + Config.months[current_month - 1]);
                Balance.setText("Rs: " + String.valueOf(calcTotalBalance()));
                ExAmt.setText("Rs: " + String.valueOf(monthlyExAmt));
                ExCnt.setText(String.valueOf(monthlyExCnt));
                InAmt.setText("Rs: " + String.valueOf(monthlyInAmt));
                InCnt.setText(String.valueOf(monthlyInCnt));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    public double calcTotalBalance() {
        return totalIncome - totalExpense;
    }

}