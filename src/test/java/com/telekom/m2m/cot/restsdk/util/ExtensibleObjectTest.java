package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class ExtensibleObjectTest {

    @Test
    public void testSerializationOfExtensibleObjects() {
        ManagedObject mo = new ManagedObject();
        mo.setId("1");
        mo.setName("Foo");
        mo.set("test_element", "foo");
        Gson gson = GsonUtils.createGson();
        String moJson = gson.toJson(mo);

        JsonObject deserialzed = gson.fromJson(moJson, JsonObject.class);

        Assert.assertEquals(deserialzed.get("test_element").getAsString(), "foo");

        //First it was suggested, that it deserializes the id, but this is not
        // wanted, b/c PUT/POST requests don't allow IDs
        // (PUT -> part of URL, POST -> will be generated).
        //Assert.assertEquals(deserialzed.get("id").getAsString(), "1");
        Assert.assertNull(deserialzed.get("id"));

        Assert.assertEquals(deserialzed.get("name").getAsString(), "Foo");

    }

    @Test
    public void testSerializationOfExtensibleObjectsWithObjects() {
        ManagedObject mo = new ManagedObject();
        mo.setId("1");
        mo.setName("Foo");
        mo.set("test_element", "foo");

        HashMap<String, String> object = new HashMap<>();
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
    public void testSerializationOfExtensibleObjectsWithEmptyObjects() {
        ManagedObject mo = new ManagedObject();
        mo.setId("1");
        mo.setName("Foo");
        mo.set("nested_empty", new HashMap<>());
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
    public void testDeserializationOfExtensibleObjects() {
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
    public void testDeserializationOfExtensibleObjectsWithObjects() {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("mo-test.json");
        InputStreamReader br = new InputStreamReader(in);

        Gson gson = GsonUtils.createGson();
        ManagedObject mo = new ManagedObject(gson.fromJson(br, ExtensibleObject.class));

        Assert.assertTrue(mo.has("nested"));

        Object deserializedObject = mo.get("nested");

        Assert.assertTrue(deserializedObject instanceof ExtensibleObject);

        ExtensibleObject obj = (ExtensibleObject) deserializedObject;
        Assert.assertTrue(obj.has("foo"));
        Assert.assertEquals(obj.get("foo"), "bar");

    }


    @Test
    public void testDeserializationOfExtensibleObjectsWithEmptyObjects() {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("mo-test.json");
        InputStreamReader br = new InputStreamReader(in);

        Gson gson = GsonUtils.createGson();
        ManagedObject mo = new ManagedObject(gson.fromJson(br, ExtensibleObject.class));

        Assert.assertTrue(mo.has("nested_empty"));

        Object deserializedObject = mo.get("nested_empty");

        Assert.assertTrue(deserializedObject instanceof ExtensibleObject);

        ExtensibleObject obj = (ExtensibleObject) deserializedObject;
        Assert.assertFalse(obj.has("foo"));
    }

    @Test
    public void testSetAttributesNull() {
        ExtensibleObject eo = new ExtensibleObject();
        eo.set("test1", "String");
        eo.set("test2", 4711);
        eo.set("test3", null);
        eo.set("test4", new Position(1, 2, 3));
        eo.set("test5", new Date());

        Map<String, Object> attributesBefore = eo.getAttributes();

        Assert.assertEquals(attributesBefore.get("test1"), "String");
        Assert.assertEquals(attributesBefore.get("test2"), 4711);
        Assert.assertNull(attributesBefore.get("test3"));

        eo.setAttributes(null);

        Map<String, Object> attributesAfter = eo.getAttributes();

        Assert.assertEquals(attributesAfter.get("test1"), "String");
        Assert.assertEquals(attributesAfter.get("test2"), 4711);
        Assert.assertNull(attributesAfter.get("test3"));
        Assert.assertEquals(attributesAfter, attributesBefore);

    }

    @Test
    public void testShallowCopyAttributes() {
        ExtensibleObject eo = new ExtensibleObject();
        Map<String, Object> attr = eo.getAttributes();

        Assert.assertNotNull(attr);
        Assert.assertEquals(attr.values().size(), 0);
        attr.put("foo", "bar");

        Assert.assertNull(eo.get("foo"));
    }

    @Test
    public void testGetSetAttributes() {
        ExtensibleObject eo = new ExtensibleObject();
        Map<String, Object> attr = eo.getAttributes();

        Assert.assertNotNull(attr);
        Assert.assertEquals(attr.values().size(), 0);

        attr.put("foo", "bar");

        Assert.assertNull(eo.get("foo"));

        eo.setAttributes(attr);

        Assert.assertEquals(eo.get("foo"), "bar");

        attr.put("foo", "boo");

        Assert.assertEquals(eo.get("foo"), "bar");
    }
}
