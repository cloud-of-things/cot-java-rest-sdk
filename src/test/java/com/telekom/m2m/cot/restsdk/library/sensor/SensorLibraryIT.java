package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SensorLibraryIT {

    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private final InventoryApi inventoryApi = cotPlat.getInventoryApi();

    private ManagedObject testManagedObject;

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }
    
    @Test
    public void testSensorsAndMeasurements() {
        testManagedObject = new ManagedObject();
        testManagedObject.setName("TestObjectForSensorsAndMeasurements");
        Fragment[] ff = SensorLibraryTest.getFragments();
        for (Fragment f : ff) {
            testManagedObject.addFragment(f);
        }

        inventoryApi.create(testManagedObject);
        assertNotNull(testManagedObject.getId(), "Should now have an Id");

        ManagedObject moOut = inventoryApi.get(testManagedObject.getId());
        assertTrue(moOut.has("c8y_AccelerationSensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_AccelerationMeasurement")).get("acceleration")).get("value").getAsFloat(), 8.36f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_AccelerationMeasurement")).get("acceleration")).get("unit").getAsString(), "m/s2");
        
        assertTrue(moOut.has("c8y_CurrentSensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_CurrentMeasurement")).get("current")).get("value").getAsFloat(), 13.37f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_CurrentMeasurement")).get("current")).get("unit").getAsString(), "A");
        
        assertTrue(moOut.has("c8y_DistanceSensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_DistanceMeasurement")).get("distance")).get("value").getAsFloat(), 13.37f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_DistanceMeasurement")).get("distance")).get("unit").getAsString(), "mm");
        
        assertTrue(moOut.has("c8y_HumiditySensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_HumidityMeasurement")).get("h")).get("value").getAsFloat(), 13.37f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_HumidityMeasurement")).get("h")).get("unit").getAsString(), "%RH");
        
        assertTrue(moOut.has("c8y_LightSensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_LightMeasurement")).get("e")).get("value").getAsFloat(), 8.36f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_LightMeasurement")).get("e")).get("unit").getAsString(), "lux");
        
        assertTrue(moOut.has("c8y_MoistureSensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_MoistureMeasurement")).get("moisture")).get("value").getAsFloat(), 13.37f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_MoistureMeasurement")).get("moisture")).get("unit").getAsString(), "%");
        
        assertTrue(moOut.has("c8y_MotionSensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_MotionMeasurement")).get("speed")).get("value").getAsFloat(), 13.37f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_MotionMeasurement")).get("speed")).get("unit").getAsString(), "m/s");
        
        assertTrue(moOut.has("c8y_SinglePhaseElectricitySensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("A+")).get("value").getAsFloat(), 1.1f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("A+")).get("unit").getAsString(), "kWh");
        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("A-")).get("value").getAsFloat(), 1.2f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("A-")).get("unit").getAsString(), "kWh");
        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("P+")).get("value").getAsFloat(), 1.3f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("P+")).get("unit").getAsString(), "W");
        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("P-")).get("value").getAsFloat(), 1.4f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SinglePhaseElectricityMeasurement")).get("P-")).get("unit").getAsString(), "W");
        
        
        
        
      /////////
        
        
        assertTrue(moOut.has("c8y_ThreePhaseElectricitySensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+")).get("value").getAsFloat(), 1.1f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+")).get("unit").getAsString(), "kWh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-")).get("value").getAsFloat(), 1.2f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-")).get("unit").getAsString(), "kWh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+:1")).get("value").getAsFloat(), 1.3f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+:1")).get("unit").getAsString(), "kWh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+:2")).get("value").getAsFloat(), 1.4f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+:2")).get("unit").getAsString(), "kWh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+:3")).get("value").getAsFloat(), 1.5f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A+:3")).get("unit").getAsString(), "kWh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-:1")).get("value").getAsFloat(), 1.6f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-:1")).get("unit").getAsString(), "kWh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-:2")).get("value").getAsFloat(), 1.7f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-:2")).get("unit").getAsString(), "kWh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-:3")).get("value").getAsFloat(), 1.8f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("A-:3")).get("unit").getAsString(), "kWh");
    
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+")).get("value").getAsFloat(), 1.9f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+")).get("unit").getAsString(), "W");        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-")).get("value").getAsFloat(), 2.0f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-")).get("unit").getAsString(), "W");        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+:1")).get("value").getAsFloat(), 2.1f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+:1")).get("unit").getAsString(), "W");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+:2")).get("value").getAsFloat(), 2.2f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+:2")).get("unit").getAsString(), "W");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+:3")).get("value").getAsFloat(), 2.3f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P+:3")).get("unit").getAsString(), "W");        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-:1")).get("value").getAsFloat(), 2.4f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-:1")).get("unit").getAsString(), "W");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-:2")).get("value").getAsFloat(), 2.5f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-:2")).get("unit").getAsString(), "W");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-:3")).get("value").getAsFloat(), 2.6f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("P-:3")).get("unit").getAsString(), "W");
        
        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Ri+")).get("value").getAsFloat(), 2.7f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Ri+")).get("unit").getAsString(), "kVArh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Ri-")).get("value").getAsFloat(), 2.8f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Ri-")).get("unit").getAsString(), "kVArh");
    
        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Rc+")).get("value").getAsFloat(), 2.9f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Rc+")).get("unit").getAsString(), "kVArh");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Rc-")).get("value").getAsFloat(), 3.0f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Rc-")).get("unit").getAsString(), "kVArh");
        
             
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qi+")).get("value").getAsFloat(), 3.1f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qi+")).get("unit").getAsString(), "kVAr");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qi-")).get("value").getAsFloat(), 3.2f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qi-")).get("unit").getAsString(), "kVAr");
        
        
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qc+")).get("value").getAsFloat(), 3.3f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qc+")).get("unit").getAsString(), "kVAr");
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qc-")).get("value").getAsFloat(), 3.4f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_ThreePhaseElectricityMeasurement")).get("Qc-")).get("unit").getAsString(), "kVAr");
        
        assertTrue(moOut.has("c8y_TemperatureSensor"));
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_TemperatureMeasurement")).get("T")).get("value").getAsFloat(), 23.0f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_TemperatureMeasurement")).get("T")).get("unit").getAsString(), "C");
        
        
        assertTrue(moOut.has("c8y_Position"));
        assertEquals(((JsonObject)moOut.get("c8y_Position")).get("alt").getAsFloat(), 24f);
        assertEquals(((JsonObject)moOut.get("c8y_Position")).get("lng").getAsFloat(), 25f);
        assertEquals(((JsonObject)moOut.get("c8y_Position")).get("lat").getAsFloat(), 26f);
        assertEquals(((JsonObject)moOut.get("c8y_Position")).get("trackingProtocol").getAsString(), "TELIC");
        assertEquals(((JsonObject)moOut.get("c8y_Position")).get("reportReason").getAsString(), "Time Event");

        assertTrue(moOut.has("c8y_Relay"));
        assertEquals(((JsonObject)moOut.get("c8y_Relay")).get("relayState").getAsString(), "OPEN");
        assertTrue(moOut.has("c8y_RelayArray"));
        assertTrue((moOut.get("c8y_RelayArray")).toString().contains("OPEN"));

        
    }    
}