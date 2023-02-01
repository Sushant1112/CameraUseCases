package com.example.camerausecases.driverdrowsinessdetection.helperClasses;

import android.util.Log;

import com.google.mlkit.vision.face.Face;

/**
 * this class help us in finding out all driver distraction condition
 *
 */
public class MLDetectionConditions {
    private static MLDetectionConditions eyeDetectionConditions = null;
    public static long startTimeOfEyeClose = 0,startTimeOfEyeOpen = 0,startTimeOfLookingStraight = 0,startTimeOfNotLookingStraight = 0,yawingTimeStart=0,yawingTimeStop=0;
    private static final int faceMovementThreshold = 5;
    final float eyeOpenTHRESHOLD = 0.75f;
    float cRightMouthX, cRightMouthY, cBottomMouthX, cBottomMouthY, cLeftMouthX, cLeftMouthY;
// singleton class method
    public static MLDetectionConditions getInstance() {
        if (eyeDetectionConditions == null) {
            eyeDetectionConditions = new MLDetectionConditions();
        }
        return eyeDetectionConditions;
    }

    /**
     *
     * @param face
     * @return  ture if not looking straight else false
     */
    public boolean driverIsNotLookingStraight(Face face) {
        if (face!=null&&face.getHeadEulerAngleY() <= faceMovementThreshold && face.getHeadEulerAngleY() >= -faceMovementThreshold) {
            startTimeOfLookingStraight = System.currentTimeMillis();
            return true;
        } else {
            startTimeOfNotLookingStraight = System.currentTimeMillis();
            return false;
        }
    }

    /**
     *
     * @param face
     * @return true   if  both eyes are open   more than 75%  else false
     */
    public boolean isEyesOpen(Face face) {
        if(face!=null){
        if (face.getLeftEyeOpenProbability() > eyeOpenTHRESHOLD || face.getRightEyeOpenProbability() > eyeOpenTHRESHOLD) {
            startTimeOfEyeOpen = System.currentTimeMillis();
            return true;
        } else {
            startTimeOfEyeClose = System.currentTimeMillis();
            return false;
        }
        }
        else
            return false;
    }
/**
this method  will return true if driver looking straight, eyes are close and mouth is open
 todo:  accurate
 */
    public boolean isYawning(Face face) {
        cRightMouthX = face.getLandmark(11).getPosition().x;
        cRightMouthY = face.getLandmark(11).getPosition().y;
        cBottomMouthX = face.getLandmark(0).getPosition().x;
        cBottomMouthY = face.getLandmark(0).getPosition().y;
        cLeftMouthX = face.getLandmark(5).getPosition().x;
        cLeftMouthY = face.getLandmark(5).getPosition().y;
        /**for future reference
         *
         */
//        double BC = Math.sqrt(Math.pow(cRightMouthX - cLeftMouthX, 2) + Math.pow(cRightMouthY - cLeftMouthY, 2));
//        double AC = Math.sqrt(Math.pow(cRightMouthX - cBottomNoseX, 2) + Math.pow(cRightMouthY - cBottomNoseY, 2));
//        double AB = Math.sqrt(Math.pow(cLeftMouthX - cBottomNoseX, 2) + Math.pow(cLeftMouthY - cBottomNoseY, 2));
//       // Log.i("MouthOpenClose", "isYawning: >>AB"+AB+">>AC"+AC+">>BC"+BC);
//        double ratio = (AB * AB + AC * AC - BC * BC) / (2 * AC * AB);
//        double degree = Math.acos(ratio) * (180 / Math.PI);
//         Log.i("New MouthOpenClose", "isYawning: "+(degree));
        float centerPointX = (cLeftMouthX + cRightMouthX) / 2;
        float centerPointY = (cLeftMouthY + cRightMouthY) / 2;
        float differenceX = centerPointX - cBottomMouthX;
        float differenceY = centerPointY - cBottomMouthY;

        if (face!=null&&differenceY >-2&&face.getRightEyeOpenProbability()<.85f&&face.getLeftEyeOpenProbability()<.85f) {
            yawingTimeStart=System.currentTimeMillis();
            if(differenceX>20)
                return true;
            else
                return false;
           // Log.i("MouthOpenClose", "draw: difference X >> " + differenceX + "     Y >> " + differenceY); return true;
        } else {
            yawingTimeStop=System.currentTimeMillis();
            //Log.i("MouthOpenClose", "draw: difference - Mouth is CLOSED "+differenceY);
            return false;
        }
      //  Log.i("MouthOpenClose", "draw: difference - Mouth is CLOSED diff "+degree);
//        if (degree <7 && driverIsLookingStraight(face)  && face.getLeftEyeOpenProbability() < .85 && face.getRightEyeOpenProbability() <.85) {
//            yawingTimeStart=System.currentTimeMillis();
//           // Log.i("MouthOpenClose", "draw: difference - Mouth is CLOSED  start"+yawingTimeStart);
//            return true;
//        } else {
//            yawingTimeStop=System.currentTimeMillis();
//           //Log.i("MouthOpenClose", "draw: difference - Mouth is CLOSED stop "+yawingTimeStop);
//            return false;
//        }

    }


}
