package com.movindu.pub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

public class SummaryReport extends AppCompatActivity {
    private double totalIncome = 0.0;
    private double totalExpense = 0.0;
    private int monthlyExCnt = 0;
    private int monthlyInCnt = 0;
    private double monthlyExAmt = 0.0;
    private double monthlyInAmt = 0.0;
    TextView Heading,Balance,ExCnt,InCnt,ExAmt,InAmt;
    LinearLayout DownloadPdf,ReportView;
    private DatabaseReference databaseReference,databaseReferenceIncome;
    SharedpreferenceHelper sharedpreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_report);
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
        DownloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    int current_month = calendar.get(Calendar.MONTH);
                    generatePdfFromView(ReportView, Config.months[current_month]+"_report"+".pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SummaryReport.this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void initialize(){
        Heading = (TextView) findViewById(R.id.heading);
        Balance = (TextView) findViewById(R.id.balance);
        ExCnt = (TextView) findViewById(R.id.expense_count);
        InCnt = (TextView) findViewById(R.id.income_count);
        ExAmt = (TextView) findViewById(R.id.expense_amount);
        InAmt = (TextView) findViewById(R.id.income_amount);
        ReportView = (LinearLayout) findViewById(R.id.report);
        DownloadPdf = (LinearLayout) findViewById(R.id.btn_download_pdf);
        sharedpreferenceHelper =  new SharedpreferenceHelper(SummaryReport.this);
    }
    public void getAllIncome(final CountDownLatch latch){
        databaseReferenceIncome =  FirebaseDatabase.getInstance().getReference("income");
        Query query = databaseReferenceIncome.orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle the data from the query
                Calendar calendar = Calendar.getInstance();
                // Get the current month (January is 0, December is 11)
                int current_month = calendar.get(Calendar.MONTH)+1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Income userIn = snapshot.getValue(Income.class);
                    totalIncome  = totalIncome+ userIn.getAmount();
                    Datee date = userIn.getDate();
                    int month = date.getMonth();
                    if(month == current_month){
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
    public void getAllExpenses(){
        databaseReference =  FirebaseDatabase.getInstance().getReference("expenses");
        Query query = databaseReference.orderByChild("email").equalTo(sharedpreferenceHelper.getString(Config.USER));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle the data from the query
                Calendar calendar = Calendar.getInstance();
                // Get the current month (January is 0, December is 11)
                int current_month = calendar.get(Calendar.MONTH)+1;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense userEx = snapshot.getValue(Expense.class);
                    totalExpense = totalExpense+userEx.getAmount();
                    Datee date = userEx.getDate();
                    int month = date.getMonth();
                    if(month == current_month){
                        monthlyExCnt = monthlyExCnt + 1;
                        monthlyExAmt = monthlyExAmt + userEx.getAmount();
                    }

                }

                Heading.setText("Summary for month "+Config.months[current_month-1]);
                Balance.setText("Rs: "+String.valueOf(calcTotalBalance()));
                ExAmt.setText("Rs: "+String.valueOf(monthlyExAmt));
                ExCnt.setText(String.valueOf(monthlyExCnt));
                InAmt.setText("Rs: "+String.valueOf(monthlyInAmt));
                InCnt.setText(String.valueOf(monthlyInCnt));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
    public double calcTotalBalance(){
        return totalIncome - totalExpense;
    }
    private void generatePdfFromView(View view, String fileName) throws IOException {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(view.getWidth(), view.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        pdfDocument.finishPage(page);

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            pdfDocument.writeTo(fos);
        }
        pdfDocument.close();
        Config.showSuccessMsg(SummaryReport.this);
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Config.hideMsg();
            }
        }, 3000);
        Toast.makeText(this, "PDF generated successfully: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        Log.d("PDF Path", file.getAbsolutePath());
    }
}