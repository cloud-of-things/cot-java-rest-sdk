package com.telekom.m2m.cot.restsdk.smartrest;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
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

        id = storeSomeTestTemplates();

        gId = smartRestApi.checkTemplateExistence(xId);
        assertNotNull(gId, "Now there should be some templates and the server needs to tell us their id.");

        assertEquals(id, gId, "The IDs returned from template creation and checking are not the same!");
    }


    @Test
    public void testGetAndDeleteTemplates() {
        String id = storeSomeTestTemplates();

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
        gId = storeSomeTestTemplates();
        try {
            storeSomeTestTemplates();
            fail();
        } catch (CotSdkException ex) {
            // Second store should fail ("Template already existing")
        }
    }


    @Test
    public void testDoSmartRequest() {
        // This request templates queries the ManagedObject that contains the templates themselves.
        // It has one unsigned parameter, which will be appended to the URL via the && placeholder.
        // This parameter needs to be the id of the ManagedObject.
        SmartRequestTemplate getManagedObjectRequest = new SmartRequestTemplate(
                "300", "GET", "/inventory/managedObjects/&&",
                "application/vnd.com.nsn.cumulocity.managedObject+json",
                null, // GET request doesn't need a content-type since it has no body
                "&&", // The placeholder which is to be used (multiple times, if needed) in resourceUri and templateString
                new String[]{"UNSIGNED"},
                null); // No template payload/body

        // From all responses that contain "$.com_cumulocity_model_smartrest_SmartRestTemplate"
        // return $.id and $.lastUpdated:
        SmartResponseTemplate managedObjectResponse1 = new SmartResponseTemplate(
                "400",
                null,
                "$.com_cumulocity_model_smartrest_SmartRestTemplate",
                new String[]{"$.id", "$.lastUpdated"});

        // From all responses that contain the array $.com_cumulocity_model_smartrest_SmartRestTemplate.responseTemplates
        // use that as the base and return the $.msgId for each array element.
        SmartResponseTemplate managedObjectResponse2 = new SmartResponseTemplate(
                "401",
                "$.com_cumulocity_model_smartrest_SmartRestTemplate.responseTemplates",
                null,
                new String[]{"$.msgId"});

        // From all responses that contain the array $.com_cumulocity_model_smartrest_SmartRestTemplate.requestTemplates
        // use that as the base and return the $.msgId and the $.method for each array element.
        SmartResponseTemplate managedObjectResponse3 = new SmartResponseTemplate(
                "402",
                "$.com_cumulocity_model_smartrest_SmartRestTemplate.requestTemplates",
                null,
                new String[]{"$.msgId", "$.method"});

        // The response to storing templates is the gId of the ManagedObject where the templates can then be found.
        gId = smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[]{getManagedObjectRequest},
                new SmartResponseTemplate[]{managedObjectResponse1, managedObjectResponse2, managedObjectResponse3});

        // We call SmartREST-template number 300 and pass the gId as the parameter.
        String[] response = smartRestApi.execute(xId, "300,"+gId, true);

        // The response templates are evaluated in order, so we can exactly know, what answers we should receive:

        // Response template 400, in response to the request from line 1 (the only line),
        // with the gId and a date, that starts with the year 20...: (the test will break after the year 2099, which should be ok ;-)
        assertTrue(response[0].startsWith("400,1,"+gId+",20"));

        // A response line for each response template in the array, extracted by response template 401,
        // and also triggered by request line 1:
        assertEquals(response[1], "401,1,400");
        assertEquals(response[2], "401,1,401");
        assertEquals(response[3], "401,1,402");

        // The response line for the template 402: request template ID and method:
        assertEquals(response[4], "402,1,300,GET");
    }


    public String storeSomeTestTemplates() {
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
