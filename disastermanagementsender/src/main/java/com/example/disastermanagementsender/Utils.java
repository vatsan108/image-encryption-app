package com.example.disastermanagementsender;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import static android.R.id.message;

/**
 * Created by dayanand on 20/05/17.
 */

public class Utils {

    public static void setUsernameandPassword(Context context, String username, String password){

        SharedPreferences sharedPreferences = context.getSharedPreferences("disaster", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("username", username);
        edit.putString("password", password);
        edit.commit();

    }

    public static String getUsername(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("disaster", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", null);
    }

    public static String getPassword(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("disaster", Context.MODE_PRIVATE);
        return sharedPreferences.getString("password", null);
    }

    public static void showToast(Context context, String message){

        Toast.makeText(context, message, 600000).show();

    }
}
