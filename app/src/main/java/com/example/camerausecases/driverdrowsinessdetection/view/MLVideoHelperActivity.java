package com.example.camerausecases.driverdrowsinessdetection.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.camerausecases.FaceRecognition.DetectorActivity;
import com.example.camerausecases.R;
import com.example.camerausecases.driverdrowsinessdetection.enumpackage.DriverWarningMessages;
import com.example.camerausecases.driverdrowsinessdetection.model.MLDetectionData;
import com.example.camerausecases.driverdrowsinessdetection.vision.CameraSource;
import com.example.camerausecases.driverdrowsinessdetection.vision.CameraSourcePreview;
import com.example.camerausecases.driverdrowsinessdetection.vision.GraphicOverlay;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
public abstract class MLVideoHelperActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1001;
    private static final String TAG = "MLVideoHelperActivity";
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    protected CameraSource cameraSource;
    private TextView textViewDriverLookingStraight, textViewYawing, textViewDrowsiness, textViewEyeStatus;
    private HandlerThread handlerThread;
    private Handler handler;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    Button user_profile;
    private static final int REDIRECTED_SERVERPORT = 6000;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_helper);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        preview = findViewById(R.id.camera_source_preview);
        textViewDriverLookingStraight = findViewById(R.id.textView);
        textViewYawing = findViewById(R.id.textView1);
        textViewEyeStatus = findViewById(R.id.textView2);
        user_profile=findViewById(R.id.user_profile);
        user_profile.setOnClickListener(view -> {
            Intent intent=new Intent(this, DetectorActivity.class);
            startActivity(intent);
        });
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            initSource();
            startCameraSource();
        }
        handlerThread = new HandlerThread("SocketConnection");
        handlerThread.start();
        handler = new MyHandler(handlerThread.getLooper());
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    socket = new Socket("10.0.2.2", REDIRECTED_SERVERPORT);
                    Log.d("SocketTest","SocketCreated");
                    socket.setSoTimeout(600000);
                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLDetectionData eyeDetection = MLDetectionData.getInstance();
        eyeDetection.getFaceDirection().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if(s.equals("Warning"))
                {
                    textViewDriverLookingStraight.setText("Warning Focus On Road");
                    textViewDriverLookingStraight.setTextColor(Color.GREEN);
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", DriverWarningMessages.DRIVER_NOT_LOOKING_STRAIGHT_WARNING);
                    Log.d("onchange", "getfacedirection: Warning");
                    Message mesg = handler.obtainMessage();
                    mesg.setData(bundle);
                    handler.sendMessage(mesg);
                }
                else if(s.equals("Alert"))
                {
                    textViewDriverLookingStraight.setText("Alert Focus On Road");
                    textViewDriverLookingStraight.setTextColor(Color.RED);
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", DriverWarningMessages.DRIVER_NOT_LOOKING_STRAIGHT_ALERT);
                    Log.d("onchange", "getfacedirection: Alert");
                    Message mesg = handler.obtainMessage();
                    mesg.setData(bundle);
                    handler.sendMessage(mesg);

                }
                else{
                    textViewDriverLookingStraight.setText("Driver Looking " + s);
                    textViewDriverLookingStraight.setTextColor(Color.BLACK);
                }
            }
        });
        eyeDetection.getIsYawning().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String aBoolean) {
                if(aBoolean.equals("Warning")){
                    textViewYawing.setText("Yawning " + aBoolean);
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", DriverWarningMessages.DRIVER_FELLING_SLEEPY_WARNING);
                    Message mesg = handler.obtainMessage();
                    mesg.setData(bundle);
                    handler.sendMessage(mesg);
                }
                else if(aBoolean.equals("Alert"))
                {
                    textViewYawing.setText("Yawning " + aBoolean);
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", DriverWarningMessages.DRIVER_FELLING_SLEEPY_ALERT);
                    Message mesg = handler.obtainMessage();
                    mesg.setData(bundle);
                    handler.sendMessage(mesg);
                }
                else {
                    textViewYawing.setText("Yawning"+aBoolean);
                }
            }
        });
        eyeDetection.getEyeStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("Warning"))
                {
                    textViewEyeStatus.setText("Warning Take Break");
                    textViewEyeStatus.setTextColor(Color.GREEN);
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", DriverWarningMessages.DRIVER_EYE_CLOSED_WARNING);
                    Log.d("onchange", "getEyeStatus: Warning");
                    Message mesg = handler.obtainMessage();
                    mesg.setData(bundle);
                    handler.sendMessage(mesg);
                }
               else if(s.equals("Alert"))
                {
                    textViewEyeStatus.setText("Alert Take Break");
                    textViewEyeStatus.setTextColor(Color.RED);
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", DriverWarningMessages.DRIVER_EYE_CLOSED_ALERT);
                    Log.d("onchange", "getEyeStatus: Alert");
                    Message mesg = handler.obtainMessage();
                    mesg.setData(bundle);
                    handler.sendMessage(mesg);
                }
                else{
                    textViewEyeStatus.setText(s + " Eyes Detected");
                    textViewEyeStatus.setTextColor(Color.BLACK);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initSource();
            startCameraSource();
        }
    }

    private void initSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        setProcessor();
    }

    protected abstract void setProcessor();

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                    return;
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }


    }

    class MyHandler extends Handler {
        public MyHandler(Looper myLooper) {

            super(myLooper);
        }

//        public void handleMessage(Message msg) {
//            try {
//                Log.d("SocketTest", String.valueOf(msg.getData().getInt("key")));
//                dataOutputStream.writeInt(msg.getData().getInt("key"));
//                dataOutputStream.flush();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
    }
}