package com.telekom.m2m.cot.restsdk.alarm;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Matchers.any;

/**
 * Created by Patrick Steinert on 03.02.16.
 */
public class AlarmApiTest {

    @Test
    public void testGetAlarm() {

        String alarmJsonExample = "{\n" +
                "  \"id\" : \"10\",\n" +
                "  \"self\" : \"<<Alarm URL>>\",\n" +
                "  \"creationTime\" : \"2011-09-06T12:03:27.927Z\",\n" +
                "  \"type\" : \"com_telekom_events_TamperEvent\",\n" +
                "  \"time\" : \"2011-09-06T12:03:27.845Z\",\n" +
                "  \"text\" : \"Tamper sensor triggered\",\n" +
                "  \"status\" : \"ACTIVE\",\n" +
                "  \"severity\" : \"MAJOR\",\n" +
                "  \"source\" : { \"id\" : \"12345\", \"self\" : \"...\" },\n" +
                "  \"com_mycorp_MyProp\" : {  },\n" +
                "  \"history\" : {\n" +
                "    \"self\" : \"...\",\n" +
                "    \"auditRecords\" : [ ]\n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient client = Mockito.mock(CloudOfThingsRestClient.class);
        Mockito.when(client.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(alarmJsonExample);


        AlarmApi alarmApi = new AlarmApi(client);
        Alarm alarm = alarmApi.getAlarm("10");

        Assert.assertEquals(alarm.getId(), "10");
        Assert.assertEquals(alarm.getType(), "com_telekom_events_TamperEvent");
        Assert.assertEquals(alarm.getText(), "Tamper sensor triggered");
        Assert.assertEquals(alarm.getCreationTime().compareTo(new Date(1315310607927L)), 0);
        Assert.assertEquals(alarm.getTime().compareTo(new Date(1315310607845L)), 0);

        Assert.assertNotNull(alarm.get("com_mycorp_MyProp"));
    }
}
