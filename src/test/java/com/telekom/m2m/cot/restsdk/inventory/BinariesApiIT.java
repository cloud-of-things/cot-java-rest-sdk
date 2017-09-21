package com.telekom.m2m.cot.restsdk.inventory;


import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class BinariesApiIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private BinariesApi api = cotPlat.getBinariesApi();

    private List<String> binaryIds = new ArrayList<>();


    @AfterMethod
    public void tearDown() {
        for (String binaryId : binaryIds) {
            try {
                api.deleteBinary(binaryId);
            } catch (CotSdkException ex) {
                if (ex.getHttpStatus() != 404) {
                    fail("Failed deleting a binary with http status: "+ex.getHttpStatus());
                }
            }
        }
        binaryIds.clear();
    }


    @Test
    public void testUploadDelete() {
        Binary bin = new Binary("myFile4", "text/plain", "blablabla\nfoobar\nyolo\n".getBytes());
        String binaryId = api.uploadBinary(bin);
        binaryIds.add(binaryId);
        assertNotNull(binaryId);

        byte[] data = api.getData(bin);
        assertEquals(data.length, 22);

        api.deleteBinary(bin);
        data = api.getData(bin);
        assertNull(data);

        binaryIds.remove(binaryId);
    }


    @Test
    public void testReplace() {
        Binary bin = new Binary("myFile4", "text/plain", "foo=bar".getBytes());
        String binaryId = api.uploadBinary(bin);
        binaryIds.add(binaryId);
        assertNotNull(binaryId);

        String data = new String(api.getData(bin));
        assertEquals(data, "foo=bar");

        bin.set("type", "application/json");
        bin.set("data", "{\"foo\":\"bar\"}".getBytes());
        api.replaceBinary(bin);

        data = new String(api.getData(bin));
        assertEquals(data, "{\"foo\":\"bar\"}");
    }


    @Test
    public void testGetCollection() {
        // We upload two binaries so we can be sure there is a real collection to get:
        String filenameA = "myFileA-"+System.currentTimeMillis();
        Binary bin = new Binary(filenameA, "text/plain", "blablabla\nfoobar\nyolo\n".getBytes());
        String binaryId = api.uploadBinary(bin);
        binaryIds.add(binaryId);

        String filenameB = "myFileB-"+System.currentTimeMillis();
        bin = new Binary(filenameB, "application/xml", "<xml>whatever<tag n=1/></xml>".getBytes());
        binaryId = api.uploadBinary(bin);
        binaryIds.add(binaryId);


        BinariesCollection c = api.getBinaries(null, 1000);
        Binary[] bb = c.getBinaries();

        // We can't know how many other binaries were there already.
        assertTrue(bb.length >= 2);

        boolean foundA = false;
        boolean foundB = false;
        for (Binary b : bb) {
            if (b.getName().equals(filenameA)) {
                foundA = true;
                assertEquals(b.getType(), "text/plain");
            }
            if (b.getName().equals(filenameB)) {
                foundB = true;
                assertEquals(b.getType(), "application/xml");
            }
        }

        assertTrue(foundA);
        assertTrue(foundB);
    }
}
