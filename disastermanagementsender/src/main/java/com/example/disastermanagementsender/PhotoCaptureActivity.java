package com.example.disastermanagementsender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PhotoCaptureActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private Camera _camera;
    private Activity _activity;
    byte[] mydata;
    int mPreviewImageFormat = ImageFormat.NV21;
    Intent intent;
    String ip;
    static ArrayList<String> list1;
    Socket s1, s2, s3;
    ObjectOutputStream o1, o2, o3;

    String TAG = "PhotoCaptureActivity";
    String encKey = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        _activity = this;
        super.onCreate(savedInstanceState);

        encKey = getIntent().getStringExtra("encKey");

        new Thread() {
            @Override
            public void run() {
                try {
                    s1 = new Socket(MainActivity.senderIP, 6002);
                    o1 = new ObjectOutputStream(s1.getOutputStream());
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }.start();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new CameraPreview(this));
    }

    public void DisplayToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "" + message, 6000000).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_camera != null) {

            _camera.stopPreview();
            _camera.release();
            _camera = null;
        }

        try {
            if (s1 != null)
                s1.close();
            if (s2 != null)
                s2.close();
            if (s3 != null)
                s3.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    List<byte[]> dataList = new ArrayList<>();
    final byte[] myimage = null;
    private Camera.PreviewCallback mPreviewListener = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {


            Camera.Parameters parameters = _camera.getParameters();
            Size size = parameters.getPreviewSize();

            YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
                    size.width, size.height, null);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            image.compressToJpeg(
                    new Rect(0, 0, image.getWidth(), image.getHeight()), 90,
                    bos);

            byte[] myimage = bos.toByteArray();

            Log.i(TAG, "PNG compressed");

            final byte[] encryptData = AES.encrypt(myimage, encKey);
            Log.i(TAG, "Encryption successful");


            new Thread(){
                @Override
                public void run(){
                    try {
                        o1.writeObject(encryptData);
                        o1.close();
                        s1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Encrypted data sent successfull");

                    finish();
                }
            }.start();

        }
    };


    public void saveToFile(byte[] data) {
        File file = new File("/sdcard/aesenc.dat");
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
            Log.i(TAG, "file written success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takepicture() {
        _camera.setOneShotPreviewCallback(mPreviewListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _camera.setOneShotPreviewCallback(mPreviewListener);
        }

        return super.onTouchEvent(event);
    }

    public void setCameraDisplayOrientation(Camera camera, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();

        Camera.getCameraInfo(cameraId, info);

        int rotation = _activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Log.d("camera", "result= " + result);
        camera.setDisplayOrientation(result);
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder holder;

        CameraPreview(Context context) {
            super(context);
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            configure(format, width, height);
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (_camera != null) {

                _camera.stopPreview();
                _camera.release();
                _camera = null;
            }

        }

        public void surfaceCreated(SurfaceHolder holder) {
            _camera = Camera.open();
            try {
                _camera.setPreviewDisplay(holder);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            setCameraDisplayOrientation(_camera, 0);
            _camera.startPreview();
        }

        // not necessary
        protected void setPictureFormat(int format) {
            Camera.Parameters params = _camera.getParameters();
            List<Integer> supported = params.getSupportedPictureFormats();
            if (supported != null) {
                for (int f : supported) {
                    if (f == format) {
                        params.setPreviewFormat(format);
                        _camera.setParameters(params);
                        break;
                    }
                }
            }
        }

        // not necessary
        protected void setPreviewSize(int width, int height) {
            Camera.Parameters params = _camera.getParameters();
            List<Size> supported = params.getSupportedPreviewSizes();
            if (supported != null) {
                for (Size size : supported) {
                    if (size.width <= width && size.height <= height) {
                        params.setPreviewSize(size.width, size.height);
                        _camera.setParameters(params);
                        break;
                    }
                }
            }
        }

        // not necessary
        public void configure(int format, int width, int height) {
            _camera.stopPreview();
            setPictureFormat(format);
            setPreviewSize(width, height);
            _camera.startPreview();
        }
    }


}