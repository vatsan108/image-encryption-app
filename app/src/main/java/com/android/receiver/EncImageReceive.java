package com.android.receiver;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EncImageReceive extends Activity {

    ServerSocket ss;
    Socket soc;
    ObjectOutputStream oos1;
    ObjectInputStream oos;

    static String secretKey = "123456!!!!";
    TextView tvStatus;
    EditText etKey;
    Button btnSubmit;
    ImageView ivImageView;

    String TAG = "EncImageReceive";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagereceive);


        tvStatus = (TextView) findViewById(R.id.status);
        btnSubmit = (Button) findViewById(R.id.submit);
        etKey = (EditText) findViewById(R.id.key);
        ivImageView = (ImageView) findViewById(R.id.imageview);

        receiveData();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etKey.getText().toString().equals("")){
                    displayToast("Enter secret key");
                }else{
                    secretKey = etKey.getText().toString();
                    readFile();
                }

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

    }
    public void displayToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), message, 600000000)
                        .show();

                tvStatus.setText(message);
            }
        });
    }

    public void receiveData() {

        new Thread() {

            @Override
            public void run() {
                try {
                    displayToast("Opening socket");
                    ss = new ServerSocket(6002);
                    soc = ss.accept();
                    displayToast("Socket connected");
                    oos = new ObjectInputStream(soc.getInputStream());
                    byte[] data = (byte[]) oos.readObject();

                    displayToast("Data received");
                    savefile(data);
                    ss.close();
                    soc.close();
                    oos.close();

                } catch (Exception e) {
                    System.out.println("EEEEEEEEEEEEE" + e.getMessage());
                    e.printStackTrace();
                    displayToast("Photo " + e.getMessage());
                }

            }
        }.start();
    }

    public void onPause() {
        super.onPause();
/*
        try {
            if (ss != null || soc != null) {
                ss.close();
//                soc.close();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/

    }

    public void savefile(byte[] data) {
        try {

            String fname = "aesenc.dat";
            File file = new File("/sdcard/aes/");
            file.mkdirs();
            FileOutputStream fs = new FileOutputStream("/sdcard/aes/"
                    + fname);
            fs.write(data);
            fs.close();
            displayToast("File saved");
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
            displayToast("File save " + e.getMessage());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (ss != null) {
                ss.close();
                oos.close();
                soc.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void readFile(){
        File file = new File("/sdcard/aes/aesenc.dat");


        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileData = new byte[fileInputStream.available()];
            fileInputStream.read(fileData, 0, fileData.length);
            Log.i(TAG, "File read successful");
            byte[] decryptData = AES.decrypt(fileData, secretKey);
            Log.i(TAG, "File read decrypt successful");
            fileInputStream.close();
            if(decryptData == null){
                displayToast("Decryption failed. Check the key");
                return;
            }
            saveToFile(decryptData);
            Log.i(TAG, "File read decrypt data written to file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(byte[] data){
        File file = new File("/sdcard/aes/"+ System.currentTimeMillis()+ "aesdec.png");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.write(data);
            fileOutputStream.close();
            displayToast("File decrypted Successfully");
            displayImage(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayImage(String pathName){
        Bitmap bmp = BitmapFactory.decodeFile(pathName);
        ivImageView.setImageBitmap(bmp);
    }
}
