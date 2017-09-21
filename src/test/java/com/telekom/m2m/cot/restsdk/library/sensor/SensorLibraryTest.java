package com.telekom.m2m.cot.restsdk.library.sensor;

import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.library.Fragment;

public class SensorLibraryTest {

    
    
@Test
public void showSensorAndMeasurementFields() {
    Fragment[] ff = getFragments();

    for (Fragment f : ff) {
        System.out.println(f.getId() + " -> " + f.getJson());
    }

}


static Fragment[] getFragments() {
    return new Fragment[]{
            new AccelerationSensor(),
            new CurrentSensor(),
            new DistanceSensor(),
            new HumiditySensor(),
            new LightSensor(),
            new MoistureSensor(),
            new MotionSensor(),
            new SinglePhaseElectricitySensor(),
            new ThreePhaseElectricitySensor(),
            new TemperatureSensor(),
            new VoltageSensor()
    };
}



}
