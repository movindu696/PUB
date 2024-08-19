package com.movindu.pub.Db;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.movindu.pub.Config;

public class SharedpreferenceHelper {

    private  SharedPreferences sharedPreferences;
    Context context;
    public SharedpreferenceHelper(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Config.mySharedPref,MODE_PRIVATE);
    }
    public void saveString(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value );
        editor.commit();
    }
    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }
    public void saveInt(String key,int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,value );
        editor.commit();
    }
    public Integer getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

//    public void clear(){
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.commit();
//    }

    public void clear(){
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.commit();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Config.OTP);
        editor.remove(Config.USER);
        editor.remove(Config.USER_NAME);
        editor.remove(Config.FIREBASE_USER_ID);
        editor.remove(Config.PASSWORD);
        editor.remove(Config.APP_STATE);
        editor.apply();
    }

    public void saveBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value );
        editor.commit();
    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, true);
    }


}