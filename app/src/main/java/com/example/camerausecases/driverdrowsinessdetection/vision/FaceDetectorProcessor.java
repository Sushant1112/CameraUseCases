/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.camerausecases.driverdrowsinessdetection.vision;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.example.camerausecases.driverdrowsinessdetection.enumpackage.WarningConstant;
import com.example.camerausecases.driverdrowsinessdetection.helperClasses.MLDetectionConditions;
import com.example.camerausecases.driverdrowsinessdetection.model.MLDetectionData;

import java.util.List;
import java.util.Stack;

/**
 * Face Detector Demo.
 */
public class FaceDetectorProcessor extends VisionProcessorBase<List<Face>> {
    private static final String TAG = "FaceDetectorProcessor";
    private final FaceDetector detector;
    private Context context;
    private MLDetectionData mlDetection;
    private MLDetectionConditions mlDetectionConditions;
    private long convertToMillSecond = 1000;
    private final Stack<String> historyTrackerEyeClose = new Stack<>();
    private final Stack<String> historyTrackerYawning = new Stack<>();
    private final Stack<String> historyTrackingNotStraight = new Stack<>();
    private Stack<Long> eyeStack = new Stack<>();
    private Stack<Long> faceStack = new Stack<>();
    private Stack<Long> yawingStack = new Stack<>();
    private static final String OPEN = "Open", FALSE = "false", TRUE = "true", STRAIGHT = "Straight";


    public FaceDetectorProcessor(Context context) {
        super(context);
        this.context = context;
        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build();
        Log.v(MANUAL_TESTING_LOG, "Face detector options: " + faceDetectorOptions);
        detector = FaceDetection.getClient(faceDetectorOptions);
        mlDetection = MLDetectionData.getInstance();
        mlDetectionConditions = MLDetectionConditions.getInstance();
        historyTrackerEyeClose.add(OPEN);
        historyTrackerYawning.add(FALSE);
        faceStack.add(System.currentTimeMillis());
        historyTrackingNotStraight.add(STRAIGHT);
    }

    @Override
    public void stop() {
        super.stop();
        detector.close();
    }

    @Override
    protected Task<List<Face>> detectInImage(InputImage image) {
        return detector.process(image);
    }

    @Override
    protected void onSuccess(@NonNull List<Face> faces, @NonNull GraphicOverlay graphicOverlay) {
        for (Face face : faces) {
            faceDetection(face);
            eyeDetection(face);
            yawningDetection(face);
        }

    }
//this method  use to detect face is driver looking straight or not and send warning and Alert accordingly
    private void faceDetection(Face face) {
        mlDetectionConditions = new MLDetectionConditions();
        mlDetectionConditions.driverIsNotLookingStraight(face);
        if (MLDetectionConditions.startTimeOfNotLookingStraight - MLDetectionConditions.startTimeOfLookingStraight > convertToMillSecond * 2 && MLDetectionConditions.startTimeOfNotLookingStraight - MLDetectionConditions.startTimeOfLookingStraight < convertToMillSecond * 3) {
            mlDetection.setFaceDirection(WarningConstant.Warning);
            if (!(WarningConstant.Warning).equals(historyTrackingNotStraight.peek())) {
                historyTrackingNotStraight.add(WarningConstant.Warning);
                faceStack.add(System.currentTimeMillis());
                if (faceStack.size() > 9 && faceStack.peek() - faceStack.firstElement() < 61 * convertToMillSecond) {
                    mlDetection.setFaceDirection(WarningConstant.Alert);
                    faceStack.clear();
                } else if (faceStack.size() > 9) {
                    faceStack.remove(faceStack.firstElement());
                }

            }
        } else if (MLDetectionConditions.startTimeOfNotLookingStraight - MLDetectionConditions.startTimeOfLookingStraight > convertToMillSecond * 5) {
            mlDetection.setFaceDirection(WarningConstant.Alert);
        } else {
            if (!(STRAIGHT).equals(historyTrackingNotStraight.peek())) {
                historyTrackingNotStraight.add(STRAIGHT);
            }

            mlDetection.setFaceDirection(STRAIGHT);
        }
    }
// this method use to detect eye open probability and send warning massage and Alert
    private void eyeDetection(Face face) {
        //Open, Close
        boolean eyeOpen = mlDetectionConditions.isEyesOpen(face);
        if (MLDetectionConditions.startTimeOfEyeClose - MLDetectionConditions.startTimeOfEyeOpen > convertToMillSecond * 2 && MLDetectionConditions.startTimeOfEyeClose - MLDetectionConditions.startTimeOfEyeOpen < convertToMillSecond * 3) {
            mlDetection.setEyeStatus(WarningConstant.Warning);

        } else if (MLDetectionConditions.startTimeOfEyeClose - MLDetectionConditions.startTimeOfEyeOpen > convertToMillSecond * 3) {
            mlDetection.setEyeStatus(WarningConstant.Alert);
        } else {
            mlDetection.setEyeStatus(OPEN);
        }
        if (!eyeOpen) {
            if (!(WarningConstant.Warning).equals(historyTrackerEyeClose.peek())) {
                historyTrackerEyeClose.add(WarningConstant.Warning);
                eyeStack.add(System.currentTimeMillis());
                if (eyeStack.size() > 24 && eyeStack.peek() - eyeStack.firstElement() < 61 * convertToMillSecond) {
                    mlDetection.setEyeStatus(WarningConstant.Alert);
                    eyeStack.clear();
                } else if (eyeStack.size() > 24) {
                    eyeStack.remove(eyeStack.firstElement());
                }
            }
        } else {
            if (!(OPEN).equals(historyTrackerEyeClose.peek())) {
                {
                    historyTrackerEyeClose.add(OPEN);
                }
            }
        }
    }

    // this method use to send yawing condition and send warining and Alert
    private void yawningDetection(Face face) {
      boolean yawing=  mlDetectionConditions.isYawning(face);
        if (yawing) {
          if (!historyTrackerYawning.peek().equals(TRUE)) {
                historyTrackerYawning.add(TRUE);
                yawingStack.add(System.currentTimeMillis());
                if (yawingStack.size() > 11 && yawingStack.peek() - yawingStack.firstElement() <= 59 * convertToMillSecond) {
                    mlDetection.setIsYawning(WarningConstant.Alert);
                    yawingStack.clear();
                }

               else if (yawingStack.size() == 11 && yawingStack.peek() - yawingStack.firstElement() < 61 * convertToMillSecond) {
                    mlDetection.setIsYawning(WarningConstant.Warning);
                }

            }
        } else {
            if (!historyTrackerYawning.peek().equals(FALSE)) {
                historyTrackerYawning.add(FALSE);
            }
            mlDetection.setIsYawning("Not Found");
        }

    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }


}
