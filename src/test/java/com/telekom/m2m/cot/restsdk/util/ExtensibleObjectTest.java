package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by breucking on 31.01.16.
 */
public class ExtensibleObjectTest {

    @Test
    public void testSerializationOfExtensibleObjects() throws Exception {
        ManagedObject mo = new ManagedObject();
        mo.setId("1");
        mo.setName("Foo");
        mo.set("test_element", "foo");
        Gson gson = GsonUtils.createGson();
        String moJson = gson.toJson(mo);

        JsonObject deserialzed = gson.fromJson(moJson, JsonObject.class);

        Assert.assertEquals(deserialzed.get("test_element").getAsString(), "foo");
        Assert.assertEquals(deserialzed.get("id").getAsString(), "1");
        Assert.assertEquals(deserialzed.get("name").getAsString(), "Foo");

    }

    @Test
    public void testSerializationOfExtensibleObjectsWithObjects() throws Exception {
        ManagedObject mo = new ManagedObject();
        mo.setId("1");
        mo.setName("Foo");
        mo.set("test_element", "foo");

        HashMap<String, String> object = new HashMap<String, String>();
        object.put("foo", "bar");
        mo.set("nested", object);

        Gson gson = GsonUtils.createGson();
        String moJson = gson.toJson(mo);

        JsonObject deserialzed = gson.fromJson(moJson, JsonObject.class);

        Assert.assertTrue(deserialzed.has("nested"));
        Assert.assertTrue(deserialzed.get("nested").isJsonObject());
        Assert.assertTrue(deserialzed.get("nested").getAsJsonObject().has("foo"));
        Assert.assertEquals(deserialzed.get("nested").getAsJsonObject().get("foo").getAsString(), "bar");
    }

    @Test
    public void testSerializationOfExtensibleObjectsWithEmptyObjects() throws Exception {
        ManagedObject mo = new ManagedObject();
        mo.setId("1");
        mo.setName("Foo");
        mo.set("nested_empty", new HashMap<Object, Object>());
        mo.set("nested_empty2", new Object());

        Gson gson = GsonUtils.createGson();
        String moJson = gson.toJson(mo);

        JsonObject deserialzed = gson.fromJson(moJson, JsonObject.class);

        Assert.assertTrue(deserialzed.has("nested_empty"));
        Assert.assertTrue(deserialzed.get("nested_empty").isJsonObject());
        Assert.assertFalse(deserialzed.get("nested_empty").getAsJsonObject().has("foo"));

        Assert.assertTrue(deserialzed.has("nested_empty2"));
        Assert.assertTrue(deserialzed.get("nested_empty2").isJsonObject());
        Assert.assertFalse(deserialzed.get("nested_empty2").getAsJsonObject().has("foo"));
    }

    @Test
    public void testDeserializationOfExtensibleObjects() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("mo-test.json");
        InputStreamReader br = new InputStreamReader(in);

        Gson gson = GsonUtils.createGson();
        ManagedObject mo = new ManagedObject(gson.fromJson(br, ExtensibleObject.class));

        Assert.assertEquals(mo.get("test_element"), "foo");
        Assert.assertEquals(mo.get("id"), "1");
        Assert.assertEquals(mo.get("name"), "Foo");

    }

    @Test
    public void testDeserializationOfExtensibleObjectsWithObjects() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("mo-test.json");
        InputStreamReader br = new InputStreamReader(in);

        Gson gson = GsonUtils.createGson();
        ManagedObject mo = new ManagedObject(gson.fromJson(br, ExtensibleObject.class));

        Assert.assertTrue(mo.has("nested"));

        Object deserializedObject = mo.get("nested");

        Assert.assertTrue(deserializedObject instanceof JsonObject);

        JsonObject obj = (JsonObject) deserializedObject;
        Assert.assertTrue(obj.has("foo"));
        Assert.assertEquals(obj.get("foo").getAsString(), "bar");

    }


    @Test
    public void testDeserializationOfExtensibleObjectsWithEmptyObjects() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("mo-test.json");
        InputStreamReader br = new InputStreamReader(in);

        Gson gson = GsonUtils.createGson();
        ManagedObject mo = new ManagedObject(gson.fromJson(br, ExtensibleObject.class));

        Assert.assertTrue(mo.has("nested_empty"));

        Object deserializedObject = mo.get("nested_empty");

        Assert.assertTrue(deserializedObject instanceof JsonObject);

        JsonObject obj = (JsonObject) deserializedObject;
        Assert.assertFalse(obj.has("foo"));
    }

}
