package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class DeviceManagementLibraryTest {


    @Test
    public void testIsDevice() {
        IsDevice f = new IsDevice();
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertTrue(m.has("c8y_IsDevice"));
    }


    @Test
    public void testRequiredAvailability() {
        RequiredAvailability f = new RequiredAvailability(2);
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)m.get("c8y_RequiredAvailability")).get("responseInterval").getAsInt(), 2);
    }


    @Test
    public void testMobile() {
        Mobile f = new Mobile("123456789012345", "23FF", "0123456789ABCDEF0123");
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)m.get("c8y_Mobile")).get("imei").getAsString(), "123456789012345");
        assertEquals(((JsonObject)m.get("c8y_Mobile")).get("cellId").getAsString(), "23FF");
        assertEquals(((JsonObject)m.get("c8y_Mobile")).get("iccid").getAsString(), "0123456789ABCDEF0123");
    }


    @Test
    public void testSupportedOperations() {
        SupportedOperations f = new SupportedOperations("op1", "op2");
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonArray)m.get("c8y_SupportedOperations")).get(0).getAsString(), "op1");
        assertEquals(((JsonArray)m.get("c8y_SupportedOperations")).get(1).getAsString(), "op2");
        assertEquals(((JsonArray)m.get("c8y_SupportedOperations")).size(), 2);
    }


    @Test
    public void testBattery() {
        Battery f = new Battery(80.5f, "%");
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)((JsonObject)m.get("c8y_Battery")).get("level")).get("value").getAsFloat(), 80.5f);
        assertEquals(((JsonObject)((JsonObject)m.get("c8y_Battery")).get("level")).get("unit").getAsString(), "%");
    }


    @Test
    public void testSignalStrength() {
        SignalStrength f = new SignalStrength(-30.2f, "dBm", 5.5f, "%");
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject) ((JsonObject) m.get("c8y_SignalStrength")).get("rssi")).get("value").getAsFloat(), -30.2f);
        assertEquals(((JsonObject) ((JsonObject) m.get("c8y_SignalStrength")).get("rssi")).get("unit").getAsString(), "dBm");
        assertEquals(((JsonObject) ((JsonObject) m.get("c8y_SignalStrength")).get("ber")).get("value").getAsFloat(), 5.5f);
        assertEquals(((JsonObject) ((JsonObject) m.get("c8y_SignalStrength")).get("ber")).get("unit").getAsString(), "%");
    }


    @Test
    public void testHardware() {
        Hardware f = new Hardware("mod", "rev", "SN");
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)m.get("c8y_Hardware")).get("model").getAsString(), "mod");
        assertEquals(((JsonObject)m.get("c8y_Hardware")).get("revision").getAsString(), "rev");
        assertEquals(((JsonObject)m.get("c8y_Hardware")).get("serialNumber").getAsString(), "SN");
    }


    @Test
    public void testFirmware() {
        Firmware f = new Firmware("name", "vers", "url");
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)m.get("c8y_Firmware")).get("name").getAsString(), "name");
        assertEquals(((JsonObject)m.get("c8y_Firmware")).get("version").getAsString(), "vers");
        assertEquals(((JsonObject)m.get("c8y_Firmware")).get("url").getAsString(), "url");
    }


    @Test
    public void testSoftwareList() {
        SoftwareList f = new SoftwareList(new SoftwareList.Software("n1", "v1", "u1"),
                new SoftwareList.Software("n2", "v2", "u2")).
                addSoftware(new SoftwareList.Software("n3", "v3", "u3")).
                addSoftware(new SoftwareList.Software("n4", "v4", "u4"));
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonArray)m.get("c8y_SoftwareList")).get(0).getAsJsonObject().get("version").getAsString(), "v1");
        assertEquals(((JsonArray)m.get("c8y_SoftwareList")).get(1).getAsJsonObject().get("url").getAsString(), "u2");
        assertEquals(((JsonArray)m.get("c8y_SoftwareList")).get(2).getAsJsonObject().get("name").getAsString(), "n3");
    }


    @Test
    public void testConfiguration() {
        Configuration f = new Configuration("myConfig-1\\nmyConfig-2\\n");
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)m.get("c8y_Configuration")).get("config").getAsString(), "myConfig-1\\nmyConfig-2\\n");
    }


    @Test
    public void testCellInfo() {
        CellInfo f = new CellInfo("gsm", new CellInfo.CellTower("gsm", 49, 100, 200, 30, 2, 20, 12345678L, 1),
                new CellInfo.CellTower(null, 48, 10, 20, 31, null, null, null, 0)).
                addCellTower(new CellInfo.CellTower("gsm", 48, 10, 20, 32, 3, 10, 12345678L, 1)).
                addCellTower(new CellInfo.CellTower(null, 48, 10, 20, 33, null, null, null, null));
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)m.get("c8y_CellInfo")).get("radioType").getAsString(), "gsm");
        JsonObject firstTower = ((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(firstTower.get("mobileCountryCode").getAsString(), "49");
        assertEquals(firstTower.get("mobileNetworkCode").getAsString(), "100");
        assertEquals(firstTower.get("locationAreaCode").getAsString(), "200");
        assertEquals(firstTower.get("timingAdvance").getAsString(), "2");
        assertEquals(firstTower.get("radioType").getAsString(), "gsm");
        assertEquals(firstTower.get("serving").getAsString(), "1");
        assertFalse(((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(1).getAsJsonObject().has("primaryScramblingCode"));
        assertEquals(((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(2).getAsJsonObject().get("signalStrength").getAsString(), "10");
        assertEquals(((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(3).getAsJsonObject().get("cellId").getAsString(), "33");
    }




    @Test
    public void showFragmentSerialisation() {
        Fragment[] ff = getFragments();

        for (Fragment f : ff) {
            System.out.println(f.getId() + " -> " + f.getJson());
        }

    }

    static Fragment[] getFragments() {
        return new Fragment[]{
                new IsDevice(),
                new RequiredAvailability(10),
                new Mobile("123456789012345", "23FF", "0123456789ABCDEF0123"),
                new SupportedOperations("op1", "op2"),
                new Battery(80, "%"),
                new SignalStrength(-30.2f, "dBm", 5.5f, "%"),
                new Hardware("mod", "rev", "SN"),
                new Firmware("name", "vers", "url"),
                new SoftwareList(new SoftwareList.Software("n1", "v1", "u1"),
                                 new SoftwareList.Software("n2", "v2", "u2")).
                                addSoftware(new SoftwareList.Software("n3", "v3", "u3")).
                                addSoftware(new SoftwareList.Software("n4", "v4", "u4")),
                new Configuration("myConfig-1\\nmyConfig-2\\n"),
                new CellInfo("gsm", new CellInfo.CellTower("gsm", 49, 10, 20, 30, 2, 20, 12345678L, 1),
                                    new CellInfo.CellTower(null, 49, 10, 20, 31, null, null, null, 0)).
                            addCellTower(new CellInfo.CellTower("gsm", 49, 10, 20, 32, 2, 10, 12345678L, 1)).
                            addCellTower(new CellInfo.CellTower(null, 49, 10, 20, 33, null, null, null, null))
            };
    }

}
