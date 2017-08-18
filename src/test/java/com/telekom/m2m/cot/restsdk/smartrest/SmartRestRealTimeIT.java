package com.telekom.m2m.cot.restsdk.smartrest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class SmartRestRealTimeIT {


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
            //cotPlat.getInventoryApi().delete(gId);
        }
    }

    @Test
    public void testX() throws InterruptedException {
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
        requestTemplate2.setResourceUri("/inventory/foo/managedObjects/&&");
        requestTemplate2.setPlaceholder("&&");

        SmartResponseTemplate responseTemplate1 = new SmartResponseTemplate(
                "102", "$", "$.c8y_IsDevice", new String[]{"$.id"});

        smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[]{requestTemplate1, requestTemplate2},
                new SmartResponseTemplate[]{responseTemplate1});


        SmartCepConnector c = smartRestApi.getNotificationsConnector(xId);
        c.subscribe("/alarms/*", new SmartSubscriptionListener() {
            @Override
            public void onNotification(SmartSubscription subscription, SmartNotification notification) {
            }

            @Override
            public void onError(SmartSubscription subscription, Throwable error) {

            }
        });
        c.connect();
        for (int i=0; i<3000; i++) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void testConnectAndSubscribe() throws InterruptedException {
        CloudOfThingsRestClient client = cotPlat.getRestClient();

        String handshakeJson =
                "{\n" +
                "    \"channel\": \"/meta/handshake\",\n" +
                "    \"version\": \"1.0\",\n" +
                "    \"mininumVersion\": \"1.0beta\",\n" +
                "    \"supportedConnectionTypes\": [\"long-polling\",\"callback-polling\"],\n" +
                "    \"advice\":{\"timeout\":120000,\"interval\":30000}\n" +
                "}";

        String responseBody = client.doPostRequest(handshakeJson, "cep/realtime", "application/json");

        JsonArray r = new Gson().fromJson(responseBody, JsonArray.class);
        String clientId = r.get(0).getAsJsonObject().get("clientId").getAsString();

        String subscribeJson =
                "{\n" +
                "    \"channel\": \"/meta/subscribe\",\n" +
                "    \"clientId\": \"" + clientId +"\",\n" +
                "    \"subscription\": \"/alarms/*\"\n" +
                "}";

        responseBody = client.doPostRequest(subscribeJson, "cep/realtime", "application/json");
//[{"channel":"/meta/subscribe","subscription":"/alarms/robOverHeatAlarms","error":"403:denied_by_security_policy:create_denied","successful":false}]
/*
        String sendJson =
                "{\n" +
                        "    \"id\": \"123456rrr\",\n" +
                        "    \"channel\": \"/robTestChannel\",\n" +
                        "    \"clientId\": \"" + clientId +"\",\n" +
                        "    \"data\": \"HelloWorld\"\n" +
                        "}";

        responseBody = client.doPostRequest(sendJson, "cep/realtime", "application/json");

*/
// - Connect to a channel that is not subscribed doesn't give an error, but returns status 200?!
// - run test that creates an alarm:  mvn -Dit.test=AlarmApiIT#testCreateAlarm integration-test
// - connection-holder-thread, der runnables mit channel-filter registriert hat, um die antworten auszuwerten?
        String connectJson = "{\n" +
                "    \"channel\": \"/meta/connect\",\n" +
                "    \"clientId\": \"" + clientId +"\",\n" +
                "    \"connectionType\": \"long-polling\",\n" +
                "    \"advice\":{\"timeout\":1200000,\"interval\":30000}}";

        for (int i=0; i<3; i++) {
            responseBody = client.doPostRequest(connectJson, "cep/realtime", "application/json");
            System.out.println("response: "+responseBody);

            Thread.sleep(1000);
        }
        //Pattern.compile("[^\\s\\\\+\\$/:]");
        String unsubscribeJson =
                "{\n" +
                "    \"channel\": \"/meta/unsubscribe\",\n" +
                "    \"clientId\": \"" + clientId +"\",\n" +
                "    \"subscription\": \"/alarms/*\"\n" +
                "}";

        responseBody = client.doPostRequest(unsubscribeJson, "cep/realtime", "application/json");

    }
}
