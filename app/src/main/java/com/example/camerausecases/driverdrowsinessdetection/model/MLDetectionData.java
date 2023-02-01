package com.example.camerausecases.driverdrowsinessdetection.model;

import androidx.lifecycle.MutableLiveData;
/**
 * model class to capture change in state of any variable
 */
public class MLDetectionData {
    private static MLDetectionData EyeDetection = null;
    public MutableLiveData<String> faceDirection = new MutableLiveData<String>();
    public MutableLiveData<String> yawning = new MutableLiveData<String>();
    public MutableLiveData<String> eyeStatus = new MutableLiveData<String>();

    public static MLDetectionData getInstance() {
        if (EyeDetection == null) {
            EyeDetection = new MLDetectionData();
        }
        return EyeDetection;
    }

    /**
     * get the changed In MutableLiveData for face Direction
     * @return  MutableLiveData<String> of driver not looking straight
     */
    public MutableLiveData<String> getFaceDirection() {
        return faceDirection;
    }

    /**
     * set the changed In MutableLiveData for face Direction
     * @param faceDirection  Alert or Warning
     */
    public void setFaceDirection(String faceDirection) {
        if (!(this.faceDirection.getValue() != null && this.faceDirection.getValue().equals(faceDirection)))
            this.faceDirection.postValue(faceDirection);
    }
    /**
     * get the changed In MutableLiveData for Yawning
     * @return  MutableLiveData<String> of change Yawning
     */
    public MutableLiveData<String> getIsYawning() {
        return yawning;
    }
    /**
     * set the changed In MutableLiveData for Yawning
     * @param isYawning  Alert or Warning
     */
    public void setIsYawning(String isYawning) {
        if (!(this.yawning.getValue() != null && this.yawning.getValue().equals(isYawning)))
            this.yawning.postValue(isYawning);
    }
    /**
     * get the changed In MutableLiveData for eye open or close
     * @return  MutableLiveData<String> of change in eye status eye open or close
     */
    public MutableLiveData<String> getEyeStatus() {
        return eyeStatus;
    }
    /**
     * set the changed In MutableLiveData for eye open or close
     * @param eyeStatus  Alert or Warning change in eye status eye open or close
     */
    public void setEyeStatus(String eyeStatus) {
        if (!(this.eyeStatus.getValue() != null && this.eyeStatus.getValue().equals(eyeStatus)))
            this.eyeStatus.postValue(eyeStatus);
    }

}
