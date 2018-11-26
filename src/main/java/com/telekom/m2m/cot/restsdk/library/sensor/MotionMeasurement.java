package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class MotionMeasurement implements Fragment {

    private static final String MOTION_VALUE_TYPE ="BOOLEAN";
    private static final String NO_MOTION_UNIT = "";

    private boolean detectedMotionValue;
    private float motionSpeedValue;
    private String motionSpeedUnit;

    
    public MotionMeasurement(boolean detectedMotionValue, float motionSpeedValue, String motionSpeedUnit) {
        this.detectedMotionValue = detectedMotionValue;
        this.motionSpeedValue = motionSpeedValue;
        this.motionSpeedUnit = motionSpeedUnit;
    }


    public boolean getDetectedMotionValue() {
        return detectedMotionValue;
    }

    public String getDetectedMotionUnit() {
        return NO_MOTION_UNIT;
    }

    public float getMotionSpeedValue() {
        return motionSpeedValue;
    }

    public String getMotionSpeedUnit() {
        return motionSpeedUnit;
    }

    
    @Override
    public String getId() {
        return "c8y_MotionMeasurement";
    }
    

    @Override
    public JsonElement getJson() {
        JsonObject motionDetected = new JsonObject();
        motionDetected.addProperty("value", detectedMotionValue);
        motionDetected.addProperty("unit", NO_MOTION_UNIT);
        motionDetected.addProperty("type", MOTION_VALUE_TYPE);

        JsonObject speed = new JsonObject();
        speed.addProperty("value", motionSpeedValue);
        speed.addProperty("unit", motionSpeedUnit);

        JsonObject motionObject = new JsonObject();
        motionObject.add("motionDetected", motionDetected);
        motionObject.add("speed", speed);

        return motionObject;
    }

}
