package com.movindu.pub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.movindu.pub.Activities.SignIn.SignIn;
import com.movindu.pub.Db.SharedpreferenceHelper;
import com.movindu.pub.Models.Datee;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    public static String EMAIL = "movaimori18@gmail.com";
    public static String EMAIL_PASS = "yqlgfwpshlidynwf";
    public static String OTP_SUBJECT = "Personalized User Budget";
    public static String OTP_BODY = "This is your one time password: ";
    public static String OTP = "otp";
    public static String mySharedPref = "sp";
    public static String USER = "user";
    public static String USER_NAME = "username";
    public static String FIREBASE_USER_ID = "firebase_userid";
    public static String PASSWORD = "pass";
    public static String TWO_FACTOR_STATUS_KEY = "2FAKey";
    public static double ACCOUNT_LIMIT = 5000.0;
    public static String APP_STATE = "state";//0 - start up,1 - otp sent, 2 - registered,3 - logged
    public static String CATEGORIES[] = {"Household Items/Supplies","Housing","Transportation","Food","Utilities","Clothing","Medical/Healthcare",
            "Insurance","Debt","Education","Savings","Entertainment","Other"};

    public static String[] months = {"January","February","March","April","May","June","July","August","September",
            "October","November","December"};
    public static AlertDialog dialog;

    public enum AppState  {
        FRESH,
        R_OTP_SENT,
        TWO_FA_OTP_SENT,
        REGISTERED,
        LOGGED
    }

    public Config() {
    }

    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static Datee getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Datee date = new Datee(dayOfMonth,month,year);
        return date;
    }
    public static void showSuccessMsg(Context context){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.success_msg, null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }
    public static void showProgress(Context context){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }
    public static void hideMsg(){
        dialog.cancel();
    }

    public static void showFailedMsg(Context context){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.failed_msg, null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }
    public static void errorDialog(Context context,String error){


        Dialog dialog = new Dialog(context);
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

    public static boolean is2FAEnabled(Context c){
        SharedpreferenceHelper sphelper = new SharedpreferenceHelper(c);
        return sphelper.getBoolean(TWO_FACTOR_STATUS_KEY);
    }

}