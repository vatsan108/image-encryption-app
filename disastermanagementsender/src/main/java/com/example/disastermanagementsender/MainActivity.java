package com.example.disastermanagementsender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.disastermanagementsender.selectfile.FileSelecter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity<call, photo, searchIP> extends Activity {

    String TAG = "MainActivity";
    Button text, call, photo, video, searchIP, mFile;
    EditText mFilename;
    String ip;
    Spinner spin;
    static String senderIP = "192.168.43.1";
    String filePath = "/sdcard/instapp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spin = (Spinner) findViewById(R.id.spinner1);
        mFilename = (EditText) findViewById(R.id.editText1);

        call = (Button) findViewById(R.id.button3);
        photo = (Button) findViewById(R.id.button2);
        searchIP = (Button) findViewById(R.id.button11);

//        text.setVisibility(View.GONE);
//        video.setVisibility(View.GONE);
//        searchIP.setVisibility(View.GONE);

        call.setText("Select Photo");

        getIps();

        spin.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                senderIP = ips.get(arg2);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

//        mFilename.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                filePath = filePath + mFilename.getText().toString();
//
//                /*new Thread() {
//                    @Override
//                    public void run() {
//                        Connectsocket("FILE", senderIP);
//                    }
//                }.start();*/
//            }
//        });

        searchIP.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new Thread() {
                    @Override
                    public void run() {
                        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                        String ipaddr = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

                        ArrayList<InetAddress> ret = getConnectedDevices(ipaddr);
                        final ArrayList<String> ips = new ArrayList<String>();
                        for (int i = 0; i < ret.size(); i++) {

                            ips.add(ret.get(i).getHostAddress());
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                ArrayAdapter<String> aa = new ArrayAdapter<String>(
                                        MainActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        ips);
                                spin.setAdapter(aa);
                            }
                        });
                    }
                }.start();
            }
        });


        call.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                // AES project
                Intent intent = new Intent(MainActivity.this, FileSelecter.class);
                startActivityForResult(intent, 2000);

            }
        });

        photo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // AES
                if (mFilename.getText().toString().equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter secret key for encryption");
                    return;
                }

                Intent intent = new Intent(MainActivity.this, PhotoCaptureActivity.class);
                intent.putExtra("encKey", mFilename.getText().toString());
                startActivity(intent);

            }
        });



    }

    public void updateSpinner() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ArrayAdapter<String> aa = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item, ips);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setAdapter(aa);
            }
        });
    }

    ArrayList<String> ips = new ArrayList<String>();

    public void getIps() {

        new Thread() {
            @Override
            public void run() {
                WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                String ipaddr = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

                ArrayList<InetAddress> ret = getConnectedDevices(ipaddr);
                // final ArrayList<String> ips = new ArrayList<String>();
                ips.clear();
                for (int i = 0; i < ret.size(); i++) {

                    ips.add(ret.get(i).getHostAddress());
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        ArrayAdapter<String> aa = new ArrayAdapter<String>(
                                MainActivity.this,
                                android.R.layout.simple_spinner_item, ips);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin.setAdapter(aa);
                    }
                });
            }
        }.start();

    }

    public void DisplayToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), message, 600).show();
            }
        });
    }



    private int LoopCurrentIP = 0;

    public ArrayList<InetAddress> getConnectedDevices(String YourPhoneIPAddress) {
        ArrayList<InetAddress> ret = new ArrayList<InetAddress>();

        LoopCurrentIP = 0;

        String IPAddress = "";
        String[] myIPArray = YourPhoneIPAddress.split("\\.");
        InetAddress currentPingAddr;


        try {
            for (int i = 0; i <= 255; i++) {
                try {

                    // build the next IP address
                    currentPingAddr = InetAddress.getByName(myIPArray[0] + "."
                            + myIPArray[1] + "." + myIPArray[2] + "."
                            + i);

                    // 50ms Timeout for the "ping"
                    if (currentPingAddr.isReachable(500)) {

                        ret.add(currentPingAddr);
                        ips.add(currentPingAddr.getHostAddress());
                        updateSpinner();
                        DisplayToast("Found ip:: "
                                + currentPingAddr.getHostAddress());
                    } else {
                        // DisplayToast("not Found ip:: "+currentPingAddr.getHostAddress());
                    }
                } catch (UnknownHostException ex) {
                    DisplayToast("Ip config:: " + ex.getMessage());
                } catch (IOException ex) {
                    DisplayToast("Ip config:: " + ex.getMessage());
                }
                LoopCurrentIP++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        DisplayToast("Total ip:: " + ret.size());
        return ret;
    }

    public byte[] Readfile(String path) {

        try {

            File file = new File(path);
            FileInputStream fin = new FileInputStream(file);

            byte[] buf = new byte[fin.available()];
            fin.read(buf);
            fin.close();

            DisplayToast(MainActivity.senderIP);
            return buf;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }


    //    final static String secretKey = "ssshhhhhhhhhhh!!!!";
    final static String secretKey = "123456!!!!";

    public void aesEncryption() {


        String originalString = "howtodoinjava.com";
        byte[] encryptedString = AES.encrypt(originalString, secretKey);
        String decryptedString = AES.decryptString(encryptedString, secretKey);

        Log.i(TAG, "OriginalString: " + originalString);
        Log.i(TAG, "encrypted: " + encryptedString.toString());
        Log.i(TAG, "decrypted: " + decryptedString);
    }

    public void readFile() {
        File file = new File("/sdcard/aesenc.dat");


        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileData = new byte[fileInputStream.available()];
            fileInputStream.read(fileData, 0, fileData.length);
            Log.i(TAG, "File read successful");
            byte[] decryptData = AES.decrypt(fileData, secretKey);
            Log.i(TAG, "File read decrypt successful");
            fileInputStream.close();
            saveToFile(decryptData);
            Log.i(TAG, "File read decrypt data written to file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(byte[] data) {
        File file = new File("/sdcard/aesdec.png");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    String selectedFilePath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){
            Utils.showToast(getApplicationContext(), "please select file");
            return;
        }

        String file = data.getStringExtra("name");

        if(file == null){
            Utils.showToast(getApplicationContext(), "please select file");
            return;
        }
        if (file.contains("jpg")) {
            sendFile(file);
        } else if (file.contains("JPG")) {
            sendFile(file);
        } else {
            Utils.showToast(getApplicationContext(), "Invalid file selected please select only jpg");
        }
    }

    public void sendFile(final String filePath) {

        final String encKey = mFilename.getText().toString();
        final byte[][] myimage = {null};


        new Thread(){
            @Override
            public void run(){
                try {
                    Socket socket = new Socket(senderIP, 6002);
                    DisplayToast("Socket connected");
                    // ObjectInputStream oin = new
                    // ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream o1 = new ObjectOutputStream(
                            socket.getOutputStream());
                    DisplayToast("Writing data");

                    FileInputStream fileInputStream = new FileInputStream(new File(filePath));
                    myimage[0] = new byte[fileInputStream.available()];
                    fileInputStream.read(myimage[0], 0, myimage[0].length);

                    final byte[] encryptData = AES.encrypt(myimage[0], encKey);

                    o1.writeObject(encryptData);

                    Log.i(TAG, "Encryption successful");

                    socket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }
}
