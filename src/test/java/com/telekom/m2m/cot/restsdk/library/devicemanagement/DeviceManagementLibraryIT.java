package com.telekom.m2m.cot.restsdk.library.devicemanagement;

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

public class DeviceManagementLibraryIT {

    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private final InventoryApi inventoryApi = cotPlat.getInventoryApi();

    private ManagedObject testManagedObject;


    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    @Test
    public void testManagedObjectWithWholeDeviceManagementLibrary() {
        testManagedObject = new ManagedObject();
        testManagedObject.setName("Hello!");

        Fragment[] ff = DeviceManagementLibraryTest.getFragments();
        for (Fragment f : ff) {
            testManagedObject.addFragment(f);
        }

        inventoryApi.create(testManagedObject);
        assertNotNull(testManagedObject.getId(), "Should now have an Id");

        ManagedObject moOut = inventoryApi.get(testManagedObject.getId());

        // We check only one field of each fragment, so see that they are all present.
        assertEquals(moOut.getName(), "Hello!");
        assertEquals(((JsonObject)moOut.get("c8y_Mobile")).get("cellId").getAsString(), "23FF");
        assertEquals(((JsonArray)moOut.get("c8y_SoftwareList")).get(0).getAsJsonObject().get("version").getAsString(), "v1");
        assertEquals(((JsonArray)moOut.get("c8y_SupportedOperations")).get(0).getAsString(), "op1");
        assertTrue(moOut.has("c8y_IsDevice"));
        assertEquals(((JsonObject)moOut.get("c8y_RequiredAvailability")).get("responseInterval").getAsInt(), 10);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_Battery")).get("level")).get("value").getAsFloat(), 80f);
        assertEquals(((JsonObject)((JsonObject)moOut.get("c8y_SignalStrength")).get("rssi")).get("value").getAsFloat(), -30.2f);
        assertEquals(((JsonObject)moOut.get("c8y_Hardware")).get("model").getAsString(), "mod");
        assertEquals(((JsonObject)moOut.get("c8y_Firmware")).get("version").getAsString(), "vers");
        assertEquals(((JsonObject)moOut.get("c8y_Configuration")).get("config").getAsString(), "myConfig-1\\nmyConfig-2\\n");
        assertEquals(((JsonObject)moOut.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(2).getAsJsonObject().get("signalStrength").getAsString(), "10");
        assertEquals(((JsonObject)moOut.get("c8y_CommunicationMode")).get("mode").getAsString(), "SMS");
    }

}
