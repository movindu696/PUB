package com.movindu.pub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.movindu.pub.Activities.ExpensesDetail;
import com.movindu.pub.Activities.IncomeSummary;
import com.movindu.pub.Activities.SignIn.SignIn;
import com.movindu.pub.Activities.CryptoExchangeRates;
import com.movindu.pub.Activities.SummaryReport;
import com.movindu.pub.Activities.TwoFA;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.Models.Datee;
import com.movindu.pub.Models.Expense;
import com.movindu.pub.Models.Income;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    TextView income,expense,balance;
    LinearLayout addExpenses,addIncome,ExchangeRates;
    private DatabaseReference databaseReference;
    SharedpreferenceHelper sharedpreferenceHelper;
    double total_income = 0.0;
    double monthly_income = 0.0;
    double monthly_expense = 0.0;
    double total_expense = 0.0;
    private PieChart pieChart;
    private int selectedHour;
    private int selectedMinute;
    Datee date =  new Datee();
    private String[] category_name = new String[13];
    private double[] category_values = new double[13];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!((AlarmManager) getSystemService(Context.ALARM_SERVICE)).canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }


        initialize();
        getDate();

        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i =  new Intent(MainActivity.this, AddIncome.class);
//                startActivity(i);
//                finish();

                Intent i =  new Intent(MainActivity.this, IncomeSummary.class);
                startActivity(i);
                finish();
            }
        });

        addExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i =  new Intent(MainActivity.this, AddExpenses.class);
//                i.putExtra("totalEXP",total_expense);
//                i.putExtra("totalINC",total_income);
//                startActivity(i);
//                finish();

                Intent i =  new Intent(MainActivity.this, ExpensesDetail.class);
                i.putExtra("totalEXP",total_expense);
                i.putExtra("totalINC",total_income);
                startActivity(i);
                finish();
            }
        });


        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i =  new Intent(MainActivity.this, AddExpenses.class);
//                startActivity(i);
//                finish();

//                Intent i =  new Intent(MainActivity.this, ExpensesSummary.class);
//                startActivity(i);

//                Intent i =  new Intent(MainActivity.this, StockDetails.class);
//                startActivity(i);
                //finish();
            }
        });
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i =  new Intent(MainActivity.this, AddIncome.class);
//                startActivity(i);
////                finish();
//                Intent i =  new Intent(MainActivity.this, IncomeSummary.class);
//                startActivity(i);
                //finish();
            }
        });
        ExchangeRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(MainActivity.this, CryptoExchangeRates.class);
                startActivity(i);
            }
        });
        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //load initial data here
            }
        });

        backgroundThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clear();
        getIncome();
        getExpenses();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.profile) {
            sharedpreferenceHelper.clear();
            Intent i = new Intent(MainActivity.this, SignIn.class);
            startActivity(i);
            finish();
        }
        else if(id == R.id.alm){
            final CountDownLatch latch = new CountDownLatch(1);
            showTimePickerDialog(latch);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await(); // Wait for the latch to count down to 0
                        // Run getAllExpenses on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlarmUtils.setDailyAlarm(MainActivity.this,selectedHour,selectedMinute);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } else if (id == R.id.summary) {
            Intent i =  new Intent(MainActivity.this, SummaryReport.class);
               startActivity(i);
////                finish();

        }

        else if (id == R.id.TwoFA) {
        Intent i =  new Intent(MainActivity.this, TwoFA.class);
        startActivity(i);
////                finish();

        }

        return super.onOptionsItemSelected(item);
    }

    public void loadChart(){
        List<PieEntry> pieEntries = generateRandomPieEntries();

        // Set up the pie dataset
        PieDataSet dataSet = new PieDataSet(pieEntries, "Pie Chart");
        dataSet.setColors(generateRandomColors(pieEntries.size()));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        // Set up the pie data
        PieData pieData = new PieData(dataSet);

        // Customize pie chart
        pieChart.getDescription().setEnabled(true); // Hide description
        Description description =  new Description();
        description.setText("Your monthly expenses as a percentage for month "+Config.months[date.getMonth()-1]);
        description.setPosition(pieChart.getWidth()-50, pieChart.getHeight()-50);
        description.setTextSize(15);
        pieChart.setDescription(description);
        pieChart.setHoleRadius(40f); // Set hole radius (donut chart)
        pieChart.setTransparentCircleRadius(45f); // Set transparent circle radius
        pieChart.setData(pieData);
        pieChart.invalidate(); // Refresh chart


    }
    public void initialize(){
        income = (TextView) findViewById(R.id.income);
        expense = (TextView) findViewById(R.id.expense);
        balance = (TextView) findViewById(R.id.balance);
        addIncome = (LinearLayout) findViewById(R.id.btn_add_income);
        addExpenses = (LinearLayout) findViewById(R.id.btn_add_expenses);
        ExchangeRates = (LinearLayout) findViewById(R.id.btn_exchange);
        pieChart = findViewById(R.id.pieChart);

        income.setText(String.valueOf(total_income));
        expense.setText(String.valueOf(total_expense));
        sharedpreferenceHelper =  new SharedpreferenceHelper(MainActivity.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public void getExpenses(){
        Query query = databaseReference.child("expenses").orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Expense expense = childSnapshot.getValue(Expense.class);
                        total_expense += expense.getAmount();
                        if(expense.getDate().getMonth() == date.getMonth()){
                            monthly_expense += expense.getAmount();
                            int cateId = expense.getCategory().getId();
                            double temp = category_values[cateId];
                            category_values[cateId] = temp +expense.getAmount();
                        }


                    }
                    expense.setText("Rs: "+String.valueOf(monthly_expense));
                    getBalance();
                    loadChart();

                }
                else {
                    System.out.println("no expenses yet");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getIncome(){
        Query query = databaseReference.child("income").orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Income income = childSnapshot.getValue(Income.class);
                        total_income += income.getAmount();
                        if(income.getDate().getMonth() == date.getMonth()) {
                            monthly_income += income.getAmount();
                        }
                    }
                    income.setText("Rs: "+String.valueOf(monthly_income));
                    getBalance();
                }else {
                    System.out.println("no expenses yet");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getBalance(){
        double bal = total_income - total_expense;
        balance.setText("Rs: "+String.valueOf(bal));
    }
    public void clear(){
        total_expense = 0.0;
        total_income = 0.0;
        monthly_expense = 0.0;
        monthly_income = 0.0;
        for(int i=0;i<13;i++){
            category_values[i] = 0;
        }

    }

    private List<PieEntry> generateRandomPieEntries() {
        List<PieEntry> entries = new ArrayList<>();
        Random random = new Random();

        // Generate random pie chart segments with random values
        for (int i = 0; i < 13; i++) {
            //float value = random.nextFloat() * 100; // Random value between 0 and 100
            float val =(float) category_values[i]*100/(float) total_expense ;
            entries.add(new PieEntry(val, (val >=10) ? Config.CATEGORIES[i] : ""));
        }

        return entries;
    }

    private List<Integer> generateRandomColors(int count) {
        List<Integer> colors = new ArrayList<>();
        Random random = new Random();

        // Generate random colors for the pie chart segments
        for (int i = 0; i < count; i++) {
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            colors.add(color);
        }

        return colors;
    }
    public void getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Datee date1 = new Datee(dayOfMonth,month,year);
        date = date1;
    }
    private void showTimePickerDialog(CountDownLatch latch) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            selectedHour = hourOfDay;
            selectedMinute = minuteOfHour;
            latch.countDown();
        }, hour, minute, true);

        timePickerDialog.show();


    }
}