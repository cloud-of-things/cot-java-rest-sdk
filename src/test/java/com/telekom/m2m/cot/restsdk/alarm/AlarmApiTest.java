package com.telekom.m2m.cot.restsdk.alarm;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;


/**
 * Created by Patrick Steinert on 03.02.16.
 */
public class AlarmApiTest {

    private static final String ALARM_JSON_EXAMPLE = "{\n" +
        "  \"id\" : \"10\",\n" +
        "  \"self\" : \"<<Alarm URL>>\",\n" +
        "  \"creationTime\" : \"2011-09-06T12:03:27.927Z\",\n" +
        "  \"type\" : \"com_telekom_events_TamperEvent\",\n" +
        "  \"time\" : \"2011-09-06T12:03:27.845Z\",\n" +
        "  \"text\" : \"Tamper sensor triggered\",\n" +
        "  \"status\" : \"ACTIVE\",\n" +
        "  \"severity\" : \"MAJOR\",\n" +
        "  \"source\" : { \"id\" : \"12345\", \"self\" : \"...\" },\n" +
        "  \"com_mycorp_MyProp\" : { \"prop1\": 123 },\n" +
        "  \"history\" : {\n" +
        "    \"self\" : \"...\",\n" +
        "    \"auditRecords\" : [ ]\n" +
        "  }\n" +
        "}";

    /**
     * System under test.
     */
    private AlarmApi alarmApi;

    @BeforeMethod
    public void setup() {
        CloudOfThingsRestClient client = createClient();
        alarmApi = new AlarmApi(client);
    }

    @Test
    public void testGetAlarm() {
        Alarm alarm = alarmApi.getAlarm("10");

        Assert.assertEquals(alarm.getId(), "10");
        Assert.assertEquals(alarm.getType(), "com_telekom_events_TamperEvent");
        Assert.assertEquals(alarm.getText(), "Tamper sensor triggered");
        Assert.assertEquals(alarm.getCreationTime().compareTo(new Date(1315310607927L)), 0);
        Assert.assertEquals(alarm.getTime().compareTo(new Date(1315310607845L)), 0);

        Assert.assertEquals(((ExtensibleObject)alarm.get("com_mycorp_MyProp")).get("prop1"), new BigDecimal("123"));
    }

    @Test
    public void deleteAlarmsWithoutFilterDoesNotThrowException() {
        alarmApi.deleteAlarms(null);
    }

    @Test
    public void deleteAlarmsWithFilterDoesNotThrowException() {
        alarmApi.deleteAlarms(Filter.build().bySource("test"));
    }

    /**
     * @return A mocked Cloud of Things REST client.
     */
    @Nonnull
    private CloudOfThingsRestClient createClient() {
        final CloudOfThingsRestClient client = Mockito.mock(CloudOfThingsRestClient.class);
        Mockito.doReturn(ALARM_JSON_EXAMPLE)
            .when(client)
            .getResponse(any(String.class), any(String.class), any(String.class));
        return client;
    }
}
