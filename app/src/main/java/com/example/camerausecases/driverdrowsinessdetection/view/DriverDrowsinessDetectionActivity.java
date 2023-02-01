package com.example.camerausecases.driverdrowsinessdetection.view;

import android.os.Bundle;

import com.example.camerausecases.driverdrowsinessdetection.vision.FaceDetectorProcessor;

public class DriverDrowsinessDetectionActivity extends MLVideoHelperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setProcessor() {
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this));
    }
}
