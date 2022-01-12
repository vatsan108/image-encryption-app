package com.example.disastermanagementsender.acommon;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.disastermanagementsender.R;
import com.example.disastermanagementsender.Utils;
import com.example.disastermanagementsender.acommon.data.Users;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class RegisterActivity extends Activity {


    EditText etUsernmae;


    EditText etPassword;


    EditText etName;


    EditText etPhone;


    EditText etAddress;


    EditText etLocality;


    EditText etBloodGroup;


    EditText etDepartment;


    Button btnSubmit;


    ImageView ivPassword;

    boolean isHide = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etUsernmae = (EditText) findViewById(R.id.editText);
        etPassword = (EditText) findViewById(R.id.editText2);
        etName = (EditText) findViewById(R.id.editText3);
        etPhone = (EditText) findViewById(R.id.editText4);
        etAddress = (EditText) findViewById(R.id.editText5);
//        etLocality = (EditText) findViewById(R.id.editText6);
//        etBloodGroup = (EditText) findViewById(R.id.editText7);
//        etDepartment = (EditText) findViewById(R.id.editText8);
        ivPassword = (ImageView) findViewById(R.id.ivpwdhideshow);
        btnSubmit = (Button) findViewById(R.id.button2);


//        etLocality.setVisibility(View.GONE);
//        etBloodGroup.setVisibility(View.GONE);
//        etDepartment.setVisibility(View.GONE);


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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = etUsernmae.getText().toString();
                String password = etPassword.getText().toString();
                String name = etName.getText().toString();
                final String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String locality = etLocality.getText().toString();
                String bloodGroup = etBloodGroup.getText().toString();
                String department = etDepartment.getText().toString();

                if (username.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter username");
                } else if (password.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter password");
                } else if (name.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter name");
                } else if (phone.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter phone");
                } else if (address.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter address");
                } else {
                    if (phone.length() != 10) {
                        Utils.showToast(getApplicationContext(), "Invalid phone");
                    } else {

                        ParseObject parseObject = new ParseObject(Users.class.getSimpleName());
                        parseObject.put(Users.username, username);
                        parseObject.put(Users.password, password);
                        parseObject.put(Users.name, name);
                        parseObject.put(Users.phone, phone);
                        parseObject.put(Users.address, address);
                        parseObject.put(Users.dob, locality);

                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Utils.showToast(getApplicationContext(), "User added successfully");
                                    finish();
                                } else {
                                    e.printStackTrace();
                                    Utils.showToast(getApplicationContext(), "User add error: " + e.getMessage());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
