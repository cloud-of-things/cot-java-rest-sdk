package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


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

        String[] ops = f.getOperations();
        assertEquals(ops.length, 2);

        // The operations inside the SupportedOperations instance shall not be modifiable:
        ops[0] = "foo";
        assertEquals(f.getOperations()[0], "op1");

        // The SupportedOperations need to make a copy of the input array should:
        f = new SupportedOperations(ops);
        assertEquals(f.getOperations()[0], "foo");
        ops[0] = "bar";
        assertEquals(f.getOperations()[0], "foo");
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
        SoftwareList.Software software = new SoftwareList.Software("n4", "v4", "u4");
        SoftwareList f = new SoftwareList(new SoftwareList.Software("n1", "v1", "u1"),
                                          new SoftwareList.Software("n2", "v2", "u2")).
                                         addSoftware(new SoftwareList.Software("n3", "v3", "u3")).
                                         addSoftware(software);
        f.removeSoftware(software); // Just to see if it can be removed...
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonArray)m.get("c8y_SoftwareList")).size(), 3);
        assertEquals(((JsonArray)m.get("c8y_SoftwareList")).get(0).getAsJsonObject().get("version").getAsString(), "v1");
        assertEquals(((JsonArray)m.get("c8y_SoftwareList")).get(1).getAsJsonObject().get("url").getAsString(), "u2");
        assertEquals(((JsonArray)m.get("c8y_SoftwareList")).get(2).getAsJsonObject().get("name").getAsString(), "n3");

        List<SoftwareList.Software> list = f.getSoftwareList();
        assertEquals(list.size(), 3);

        // The Software inside the SoftwareList instance shall not be modifiable from the side:
        list.set(0, new SoftwareList.Software("foo", "foo", "foo"));
        list = f.getSoftwareList();
        assertEquals(list.get(0).name, "n1");
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
        CellInfo.CellTower cellTower = new CellInfo.CellTower("gsm", 48, 10, 20, 32, 3, 10, 12345678L, 1);
        CellInfo f = new CellInfo("gsm", new CellInfo.CellTower("gsm", 49, 100, 200, 30, 2, 20, 12345678L, 1),
                                         new CellInfo.CellTower(null, 48, 10, 20, 31, null, null, null, 0)).
                                 addCellTower(cellTower).
                                 addCellTower(new CellInfo.CellTower(null, 48, 10, 20, 33, null, null, null, null));
        f.removeCellTower(cellTower); // Just to see if it can be removed...
        ManagedObject m = new ManagedObject();
        m.setName("myObject");
        m.addFragment(f);

        assertEquals(((JsonObject)m.get("c8y_CellInfo")).get("radioType").getAsString(), "gsm");
        assertEquals(((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().size(), 3);
        JsonObject firstTower = ((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(firstTower.get("mobileCountryCode").getAsString(), "49");
        assertEquals(firstTower.get("mobileNetworkCode").getAsString(), "100");
        assertEquals(firstTower.get("locationAreaCode").getAsString(), "200");
        assertEquals(firstTower.get("timingAdvance").getAsString(), "2");
        assertEquals(firstTower.get("radioType").getAsString(), "gsm");
        assertEquals(firstTower.get("serving").getAsString(), "1");
        assertFalse(((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(1).getAsJsonObject().has("primaryScramblingCode"));
        assertEquals(((JsonObject)m.get("c8y_CellInfo")).get("cellTowers").getAsJsonArray().get(2).getAsJsonObject().get("cellId").getAsString(), "33");
    }


    @Test
    public void testCellInfoRadioType() {
        CellInfo cellInfo = new CellInfo();

        cellInfo.addCellTower(new CellInfo.CellTower("gsm", 49, 10, 20, 32, 2, 10, 12345678L, 1));
        cellInfo.addCellTower(new CellInfo.CellTower("umts", 49, 10, 20, 32, 2, 10, 12345678L, 1));
        try {
            cellInfo.addCellTower(new CellInfo.CellTower(null, 49, 10, 20, 32, 2, 10, 12345678L, 1));
            fail("Adding a CellTower without radioType to a CellInfo without radioType must fail.");
        } catch (Exception e) {
            // ok!
        }

        List<CellInfo.CellTower> towers = cellInfo.getCellTowers();
        assertEquals(towers.size(), 2);
        assertNull(cellInfo.getRadioType()); // Has to remain null because we have towers with different radioTypes.
        assertEquals(cellInfo.getCellTowers().get(0).radioType, "gsm");
        assertEquals(cellInfo.getCellTowers().get(1).radioType, "umts");

        cellInfo = new CellInfo("gsm");
        cellInfo.addCellTower(new CellInfo.CellTower("gsm", 49, 10, 20, 32, 2, 10, 12345678L, 1));
        cellInfo.addCellTower(new CellInfo.CellTower(null, 49, 10, 20, 32, 2, 10, 12345678L, 1));
        try {
            cellInfo.addCellTower(new CellInfo.CellTower("umts", 49, 10, 20, 32, 2, 10, 12345678L, 1));
            fail("Adding a CellTower without a radioType that doesn't match the one in CellInfo must fail.");
        } catch (Exception e) {
            // ok!
        }

        try {
        cellInfo = new CellInfo("gsm", new CellInfo.CellTower("gsm", 49, 10, 20, 32, 2, 10, 12345678L, 1),
                                       new CellInfo.CellTower(null, 49, 10, 20, 32, 2, 10, 12345678L, 1),
                                       new CellInfo.CellTower("umts", 49, 10, 20, 32, 2, 10, 12345678L, 1));
            fail("CellInfo with mismatching radioTypes cannot be constructed.");
        } catch (Exception e) {
            // ok!
        }
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
                            addCellTower(new CellInfo.CellTower(null, 49, 10, 20, 33, null, null, null, null)),
                new CommunicationMode("SMS")
            };
    }

}
