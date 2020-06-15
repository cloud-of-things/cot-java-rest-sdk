package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Iterator;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by Patrick Steinert on 29.08.16.
 */
public class ManagedObjectTest {

    @Test
    public void testManagedObject() {

        String inventoryJsonExample = "{\n" +
                "  \"id\" : \"42\",\n" +
                "  \"name\" : \"SomeName\",\n" +
                "  \"self\" : \"<<This ManagedObject URL>>\",\n" +
                "  \"type\" :\"com_nsn_cumulocity_example_Clazz\",\n" +
                "  \"lastUpdated\": \"2012-05-02T19:48:40.006+02:00\",\n" +
                "  \"com_othercompany_StrongTypedClass\" : { \"id\": 1},\n" +
                "  \"childDevices\": {\n" +
                "    \"self\" : \"<<ManagedObjectReferenceCollection URL>>\",\n" +
                "    \"references\" : [\n" +
                "      {\n" +
                "        \"self\" : \"<<ManagedObjectReference URL>>\",\n" +
                "        \"managedObject\": {\n" +
                "          \"id\": \"1\",\n" +
                "          \"self\" : \"<<ManagedObject URL>>\",\n" +
                "          \"name\": \"Some Child\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]    \n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                inventoryJsonExample);

        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObject mo = inventoryApi.get("abc");

        Assert.assertEquals(mo.getId(), "42");
        Assert.assertEquals(mo.getName(), "SomeName");
        Assert.assertEquals(mo.getType(), "com_nsn_cumulocity_example_Clazz");
        Assert.assertEquals(mo.getLastUpdated(), new Date(1335980920006L));

    }

    @Test
    public void testEmptyChildAssets() {

        String inventoryJsonExample = "{\n" +
                "  \"id\" : \"42\",\n" +
                "  \"name\" : \"SomeName\",\n" +
                "  \"self\" : \"<<This ManagedObject URL>>\",\n" +
                "  \"type\" :\"com_nsn_cumulocity_example_Clazz\",\n" +
                "  \"lastUpdated\": \"2012-05-02T19:48:40.006+02:00\",\n" +
                "  \"com_othercompany_StrongTypedClass\" : { \"id\": 1}\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                inventoryJsonExample);

        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObject mo = inventoryApi.get("abc");


        ManagedObjectReferenceCollection morc = mo.getChildAssets();
        Iterable<ManagedObjectReference> cMOs = morc.get();
        Assert.assertFalse(cMOs.iterator().hasNext());
    }

    @Test
    public void testEmptyParentDevices() {

        String inventoryJsonExample = "{\n" +
                "  \"id\" : \"42\",\n" +
                "  \"name\" : \"SomeName\",\n" +
                "  \"self\" : \"<<This ManagedObject URL>>\",\n" +
                "  \"type\" :\"com_nsn_cumulocity_example_Clazz\",\n" +
                "  \"lastUpdated\": \"2012-05-02T19:48:40.006+02:00\",\n" +
                "  \"com_othercompany_StrongTypedClass\" : { \"id\": 1}\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                inventoryJsonExample);

        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObject mo = inventoryApi.get("abc");


        ManagedObjectReferenceCollection morc = mo.getParentDevices();
        Iterable<ManagedObjectReference> cMOs = morc.get();
        Assert.assertFalse(cMOs.iterator().hasNext());
    }

    @Test
    public void testChildDevices() {

        String inventoryJsonExample = "{\n" +
                "  \"id\" : \"42\",\n" +
                "  \"name\" : \"SomeName\",\n" +
                "  \"self\" : \"<<This ManagedObject URL>>\",\n" +
                "  \"type\" :\"com_nsn_cumulocity_example_Clazz\",\n" +
                "  \"lastUpdated\": \"2012-05-02T19:48:40.006+02:00\",\n" +
                "  \"com_othercompany_StrongTypedClass\" : { \"id\": 1},\n" +
                "  \"childDevices\": {\n" +
                "    \"self\" : \"<<ManagedObjectReferenceCollection URL>>\",\n" +
                "    \"references\" : [\n" +
                "      {\n" +
                "        \"self\" : \"<<ManagedObjectReference URL>>\",\n" +
                "        \"managedObject\": {\n" +
                "          \"id\": \"1\",\n" +
                "          \"self\" : \"<<ManagedObject URL>>\",\n" +
                "          \"name\": \"Some Child\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]    \n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                inventoryJsonExample);

        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObject mo = inventoryApi.get("abc");

        ManagedObjectReferenceCollection morc = mo.getChildDevices();
        Iterable<ManagedObjectReference> cMOs = morc.get();
        Iterator<ManagedObjectReference> iter = cMOs.iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference mor = iter.next();
        ManagedObject cMo = mor.getManagedObject();
        Assert.assertEquals(cMo.getId(), "1");

    }

    @Test
    public void testChildAssets() {

        String inventoryJsonExample = "{\n" +
                "  \"id\" : \"42\",\n" +
                "  \"name\" : \"SomeName\",\n" +
                "  \"self\" : \"<<This ManagedObject URL>>\",\n" +
                "  \"type\" :\"com_nsn_cumulocity_example_Clazz\",\n" +
                "  \"lastUpdated\": \"2012-05-02T19:48:40.006+02:00\",\n" +
                "  \"com_othercompany_StrongTypedClass\" : { \"id\": 1},\n" +
                "  \"childAssets\": {\n" +
                "    \"self\" : \"<<ManagedObjectReferenceCollection URL>>\",\n" +
                "    \"references\" : [\n" +
                "      {\n" +
                "        \"self\" : \"<<ManagedObjectReference URL>>\",\n" +
                "        \"managedObject\": {\n" +
                "          \"id\": \"1\",\n" +
                "          \"self\" : \"<<ManagedObject URL>>\",\n" +
                "          \"name\": \"Some Child\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]    \n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                inventoryJsonExample);

        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObject mo = inventoryApi.get("abc");

        ManagedObjectReferenceCollection morc = mo.getChildAssets();
        Iterable<ManagedObjectReference> cMOs = morc.get();
        Iterator<ManagedObjectReference> iter = cMOs.iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference mor = iter.next();
        ManagedObject cMo = mor.getManagedObject();
        Assert.assertEquals(cMo.getId(), "1");

    }

    @Test
    public void testParentDevices() {

        String inventoryJsonExample = "{\n" +
                "  \"id\" : \"42\",\n" +
                "  \"name\" : \"SomeName\",\n" +
                "  \"self\" : \"<<This ManagedObject URL>>\",\n" +
                "  \"type\" :\"com_nsn_cumulocity_example_Clazz\",\n" +
                "  \"lastUpdated\": \"2012-05-02T19:48:40.006+02:00\",\n" +
                "  \"com_othercompany_StrongTypedClass\" : { \"id\": 1},\n" +
                "  \"deviceParents\": {\n" +
                "    \"self\" : \"<<ManagedObjectReferenceCollection URL>>\",\n" +
                "    \"references\" : [\n" +
                "      {\n" +
                "        \"self\" : \"<<ManagedObjectReference URL>>\",\n" +
                "        \"managedObject\": {\n" +
                "          \"id\": \"1\",\n" +
                "          \"self\" : \"<<ManagedObject URL>>\",\n" +
                "          \"name\": \"Some Child\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]    \n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                inventoryJsonExample);

        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObject mo = inventoryApi.get("abc");

        ManagedObjectReferenceCollection morc = mo.getParentDevices();
        Iterable<ManagedObjectReference> cMOs = morc.get();
        Iterator<ManagedObjectReference> iter = cMOs.iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference mor = iter.next();
        ManagedObject cMo = mor.getManagedObject();
        Assert.assertEquals(cMo.getId(), "1");

    }


    @Test
    public void testParentAssets() {

        String inventoryJsonExample = "{\n" +
                "  \"id\" : \"42\",\n" +
                "  \"name\" : \"SomeName\",\n" +
                "  \"self\" : \"<<This ManagedObject URL>>\",\n" +
                "  \"type\" :\"com_nsn_cumulocity_example_Clazz\",\n" +
                "  \"lastUpdated\": \"2012-05-02T19:48:40.006+02:00\",\n" +
                "  \"com_othercompany_StrongTypedClass\" : { \"id\": 1},\n" +
                "  \"assetParents\": {\n" +
                "    \"self\" : \"<<ManagedObjectReferenceCollection URL>>\",\n" +
                "    \"references\" : [\n" +
                "      {\n" +
                "        \"self\" : \"<<ManagedObjectReference URL>>\",\n" +
                "        \"managedObject\": {\n" +
                "          \"id\": \"1\",\n" +
                "          \"self\" : \"<<ManagedObject URL>>\",\n" +
                "          \"name\": \"Some Child\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]    \n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                inventoryJsonExample);

        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObject mo = inventoryApi.get("abc");

        ManagedObjectReferenceCollection morc = mo.getParentAssets();
        Iterable<ManagedObjectReference> cMOs = morc.get();
        Iterator<ManagedObjectReference> iter = cMOs.iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference mor = iter.next();
        ManagedObject cMo = mor.getManagedObject();
        Assert.assertEquals(cMo.getId(), "1");

    }

}
