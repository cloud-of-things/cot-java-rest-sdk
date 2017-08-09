package com.telekom.m2m.cot.restsdk.smartrest;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectCollection;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class SmartRestApiIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private SmartRestApi smartRestApi = cotPlat.getSmartRestApi();

    private String gId = null;
    private String xId = null;

    @BeforeMethod
    public void setUp() {
        xId = "test-xId" + System.currentTimeMillis();
        System.out.println("Running integration test with X-Id = " + xId);
    }

    @AfterMethod
    public void tearDown() {
        if (gId != null) {
            cotPlat.getInventoryApi().delete(gId);
        }
    }


    @Test
    public void testCreateTemplatesAndCheckExistence() {
        String id = smartRestApi.checkTemplateExistence(xId);
        assertNull(id, "In the beginning there should be no templates.");

        id = storeTestTemplates();

        gId = smartRestApi.checkTemplateExistence(xId);
        assertNotNull(gId, "Now there should be some templates and the server needs to tell us their id.");

        assertEquals(id, gId, "The IDs returned from template creation and checking are not the same!");
    }


    @Test
    public void testGetAndDeleteTemplates() {
        String id = storeTestTemplates();

        gId = smartRestApi.checkTemplateExistence(xId);

        assertEquals(id, gId, "The IDs returned from template creation and checking are not the same!");

        SmartTemplate[] templates = smartRestApi.getTemplatesByGId(gId);
        assertEquals(templates.length, 3, "Now we have created three templates.");
        assertTrue(templates[0] instanceof SmartRequestTemplate);
        assertTrue(templates[1] instanceof SmartRequestTemplate);
        assertTrue(templates[2] instanceof SmartResponseTemplate);
        assertEquals(templates[0].toString(), "101,POST,/inventory/managedObjects,application/vnd.com.nsn.cumulocity.managedObject+json,application/vnd.com.nsn.cumulocity.managedObject+json,,STRING,\"{\"\"name\"\":\"\"foo\"\",\"\"type\"\":\"\"com_example_TestDevice\"\",\"\"c8y_IsDevice\"\":{}}\"");
        assertEquals(templates[1].toString(), "999,DELETE,/inventory/managedObjects/&&,,,&&,,");
        assertEquals(templates[2].toString(), "102,$,$.c8y_IsDevice,$.id");

        smartRestApi.deleteByGId(gId);

        templates = smartRestApi.getTemplatesByGId(gId);
        assertEquals(templates.length, 0, "All templates should be gone.");
        gId = null;
    }


    @Test
    public void testDoubleStore() {
        gId = storeTestTemplates();
        try {
            storeTestTemplates();
            fail();
        } catch (CotSdkException ex) {
            // Second store should fail ("Template already existing")
        }
    }


    public String storeTestTemplates() {
        SmartRequestTemplate requestTemplate1 = new SmartRequestTemplate(
                "101", "POST", "/inventory/managedObjects",
                "application/vnd.com.nsn.cumulocity.managedObject+json",
                "application/vnd.com.nsn.cumulocity.managedObject+json",
                null,
                new String[]{"STRING"},
                "{\"name\":\"foo\",\"type\":\"com_example_TestDevice\",\"c8y_IsDevice\":{}}");

        SmartRequestTemplate requestTemplate2 = new SmartRequestTemplate();
        requestTemplate2.setMsgId("999");
        requestTemplate2.setMethod("DELETE");
        requestTemplate2.setResourceUri("/inventory/managedObjects/&&");
        requestTemplate2.setPlaceholder("&&");

        SmartResponseTemplate responseTemplate1 = new SmartResponseTemplate(
                "102", "$", "$.c8y_IsDevice", new String[]{"$.id"});

        return smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[]{requestTemplate1, requestTemplate2},
                new SmartResponseTemplate[]{responseTemplate1});
    }


}
