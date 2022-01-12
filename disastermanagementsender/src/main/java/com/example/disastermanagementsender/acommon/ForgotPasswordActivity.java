package com.example.disastermanagementsender.acommon;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.disastermanagementsender.R;
import com.example.disastermanagementsender.Utils;
import com.example.disastermanagementsender.acommon.data.Users;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class ForgotPasswordActivity extends Activity {

    EditText etUsernmae;

    Button btnSubmit;


    public static String name = "", username, password, phone, address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);


        etUsernmae = (EditText) findViewById(R.id.editText);
        btnSubmit = (Button) findViewById(R.id.button1);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsernmae.getText().toString();

                if(username.equals("")){
                    Utils.showToast(getApplicationContext(), "Enter username");
                }else{


                    ParseQuery<ParseObject> query = ParseQuery.getQuery(Users.class.getSimpleName());
                    query.whereEqualTo("username", username);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e == null){

                                if(objects.size() > 0){
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(objects.get(0).getString(Users.phone), null , "Your password: " + objects.get(0).getString(Users.password),null, null);
                                    Utils.showToast(getApplicationContext(), "Password  sent to register phone number.");
                                }else{
                                    Utils.showToast(getApplicationContext(), "Invalid username");
                                }


                            }else{
                                e.printStackTrace();
                                Utils.showToast(getApplicationContext(), "fetch username error: "+e.getMessage());
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
