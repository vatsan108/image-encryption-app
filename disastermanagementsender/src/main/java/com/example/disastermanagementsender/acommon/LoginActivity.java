package com.example.disastermanagementsender.acommon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.disastermanagementsender.MainActivity;
import com.example.disastermanagementsender.R;
import com.example.disastermanagementsender.Utils;
import com.example.disastermanagementsender.acommon.data.Users;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class LoginActivity extends Activity {



    EditText etUsernmae;


    EditText etPassword;


    Button btnLogin;


    Button btnRegister;


    Button btnForgotpassword;


    ImageView ivPassword;


    Button btnGuest;

    boolean isHide = true;



    public static String name = "", username = "", password, phone = "8747858632", address, fatherPhone, type = "", subjects;

    public static boolean isHod;

    private String TAG = "LoginActivity";

    private ParseObject userObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Bundle is used to pass data between activities
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsernmae = (EditText) findViewById(R.id.editText);
        etPassword = (EditText) findViewById(R.id.editText2);
        btnLogin = (Button) findViewById(R.id.button1);
        btnRegister = (Button) findViewById(R.id.button2);
        btnForgotpassword = (Button) findViewById(R.id.forgot);
        ivPassword = (ImageView) findViewById(R.id.ivpwdhideshow);



        ivPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isHide) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivPassword.setImageResource(R.drawable.showpassword);
                    isHide = false;
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivPassword.setImageResource(R.drawable.hidepassword);
                    isHide = true;
                }
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);// Intent is the message that is passed between components such as activities
                startActivity(intent);
            }
        });

        btnForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsernmae.getText().toString();
                String password = etPassword.getText().toString();

//                username = "daya";
//                password = "dad";

                if (username.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter username");//showToast is used to display text through popups
                } else if (password.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter password");
                } else {

//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
                    validateCredentials(username, password);

                }

            }
        });
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
    }*/

    public void validateCredentials(String username, String password) {

        Log.i(TAG, "Username: " + username + " Password: " + password);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Users.class.getSimpleName());
        query.whereEqualTo(Users.username, username);
        query.whereEqualTo(Users.password, password);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        //userObject = objects.get(0);
                       /* LoginActivity.username = userObject.getString("username");
                        LoginActivity.phone = userObject.getString("phone");
                        LoginActivity.name = userObject.getString("name");*/
//                        subjects = userObject.getString(Faculty.subjects);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        //intent.putExtra("hod", false);//an Activity can send an Intents to the Android system which starts another Activity
                        //isHod = false;
                        startActivity(intent);
                    } else {
                        Utils.showToast(getApplicationContext(), "Invalid credentials");
                    }

                } else {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "fetch username error: " + e.getMessage());
                }
            }
        });
    }


//    public void saveLogingData() {
//        SharedPreferences pref = getSharedPreferences("project", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putBoolean("login", true);
//        editor.commit();
//    }
//
//    public static boolean isLoggedIn(Context context) {
//        SharedPreferences pref = context.getSharedPreferences("project", MODE_PRIVATE);
//        return pref.getBoolean("login", false);
//    }
}
