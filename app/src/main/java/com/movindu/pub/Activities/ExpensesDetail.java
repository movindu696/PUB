package com.movindu.pub.Activities;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Adapters.ExpenseAdapter;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.Datee;
import com.movindu.pub.Models.Expense;
import com.movindu.pub.Models.Income;
import com.movindu.pub.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ExpensesDetail extends Activity {
    private ListView listView;
    private ExpenseAdapter eAdapter;
    private List<Expense> expenseList;
    private double totalIncome = 0.0;
    private double totalExpense = 0.0;
    private double monthlyExpense = 0.0;
    SharedpreferenceHelper sharedpreferenceHelper;
    TextView Balance,Heading,Expenses;
    LinearLayout AddIncome;
    private DatabaseReference databaseReference,databaseReferenceIncome;
    double total_expenses,total_income;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_detail);
        Intent intent = getIntent();
        total_expenses = intent.getDoubleExtra("totalEXP",0.0);
        total_income = intent.getDoubleExtra("totalINC",0.0);
        listView = findViewById(R.id.listView);
        AddIncome = (LinearLayout) findViewById(R.id.btn_add_expenses);
        Balance = (TextView) findViewById(R.id.balance_txt);
        Expenses = (TextView) findViewById(R.id.expenses_txt);
        Heading = (TextView) findViewById(R.id.heading);
        sharedpreferenceHelper =  new SharedpreferenceHelper(ExpensesDetail.this);
        expenseList = new ArrayList<>();
        eAdapter = new ExpenseAdapter(this, expenseList);
        listView.setAdapter(eAdapter);
        Collections.reverse(expenseList);
        eAdapter.notifyDataSetChanged();
        //set current month
        Calendar calendar = Calendar.getInstance();
        int current_month = calendar.get(Calendar.MONTH);
        Heading.setText("Expenses for month "+String.valueOf(Config.months[current_month]));
        AddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(ExpensesDetail.this, AddExpenses.class);
                i.putExtra("totalEXP",total_expenses);
                i.putExtra("totalINC",total_income);
                startActivity(i);
                finish();

            }
        });

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




        }

        public void getAllIncome(final CountDownLatch latch){
            databaseReferenceIncome = (DatabaseReference) FirebaseDatabase.getInstance().getReference("income");
            Query query = databaseReferenceIncome.orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Handle the data from the query
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Income userIn = snapshot.getValue(Income.class);
                        totalIncome  = totalIncome+ userIn.getAmount();
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                    latch.countDown();
                }
            });

            databaseReferenceIncome.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Income userIn = snapshot.getValue(Income.class);
//                        totalIncome  = totalIncome+ userIn.getAmount();
//                    }
//                    latch.countDown();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    latch.countDown();
                }
            });
        }
    public void getAllExpenses(){
        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference("expenses");
        Query query = databaseReference.orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle the data from the query
                Calendar calendar = Calendar.getInstance();
                int current_month = calendar.get(Calendar.MONTH)+1;
                expenseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense userEx = snapshot.getValue(Expense.class);
                    totalExpense = totalExpense + userEx.getAmount();
                    Datee date = userEx.getDate();
                    int month = date.getMonth();
                    if(month == current_month){
                        expenseList.add(userEx);
                        monthlyExpense = monthlyExpense +userEx.getAmount();
                    }

                }
                eAdapter.notifyDataSetChanged();
                //Balance.setText(String.valueOf(calcTotalBalance()));
                Expenses.setText("Total Expenses "+String.valueOf(monthlyExpense));
                Balance.setText("Wallet Balance "+String.valueOf(total_income-total_expenses));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Calendar calendar = Calendar.getInstance();
//                // Get the current month (January is 0, December is 11)
//                int current_month = calendar.get(Calendar.MONTH)+1;
//                expenseList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Expense userEx = snapshot.getValue(Expense.class);
//                    totalExpense = totalExpense+userEx.getAmount();
//                    Datee date = userEx.getDate();
//                    int month = date.getMonth();
//                    if(month == current_month){
//                        expenseList.add(userEx);
//                    }
//
//                }
//                eAdapter.notifyDataSetChanged();
//                Balance.setText(String.valueOf(calcTotalBalance()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public double calcTotalBalance(){
        return totalIncome - totalExpense;
    }

    @Override
    public void onBackPressed() {
        Intent i =  new Intent(ExpensesDetail.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}