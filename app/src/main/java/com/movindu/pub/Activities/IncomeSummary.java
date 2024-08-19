package com.movindu.pub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Adapters.ExpenseAdapter;
import com.movindu.pub.Adapters.IncomeAdapter;
import com.movindu.pub.Config;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.MainActivity;
import com.movindu.pub.Models.Datee;
import com.movindu.pub.Models.Expense;
import com.movindu.pub.Models.Income;
import com.movindu.pub.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncomeSummary extends Activity {
    private ListView listView;
    private IncomeAdapter eAdapter;
    private List<Expense> expenseList;
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    LinearLayout AddIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_summary);

        listView = findViewById(R.id.listView);
        AddIncome = (LinearLayout) findViewById(R.id.btn_add_income);
        sharedpreferenceHelper =  new SharedpreferenceHelper(IncomeSummary.this);
        expenseList = new ArrayList<>();
        eAdapter = new IncomeAdapter(this, expenseList);
        listView.setAdapter(eAdapter);

        AddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(IncomeSummary.this, AddIncome.class);
                startActivity(i);
                finish();

            }
        });

        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference("income");
        Query query = databaseReference.orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle the data from the query
                Calendar calendar = Calendar.getInstance();
                // Get the current month (January is 0, December is 11)
                int current_month = calendar.get(Calendar.MONTH)+1;
                expenseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense userEx = snapshot.getValue(Expense.class);
                    Datee date = userEx.getDate();
                    int month = date.getMonth();
                    if(month == current_month){
                        expenseList.add(userEx);
                    }

                }
                eAdapter.notifyDataSetChanged();
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
//                    Datee date = userEx.getDate();
//                    int month = date.getMonth();
//                    if(month == current_month){
//                        expenseList.add(userEx);
//                    }
//
//                }
//                eAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i =  new Intent(IncomeSummary.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}