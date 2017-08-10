package com.telekom.m2m.cot.restsdk.users;

import org.junit.Test;
import org.testng.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

public class GroupTest {
    // TODO fix the logger problem below
    // private static final Logger logger =
    // LoggerFactory.getLogger(GroupsApiTest.class);

    @Test
    public void testGroupMethods() {

        Group group = new Group();
        group.setId("667");
        group.setName("testgroup");
        group.setDescription("group description");

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(group);

        JsonObject o = gson.fromJson(json, JsonObject.class);
        Assert.assertEquals(o.get("id").getAsString(), "667");
        // Assert.assertEquals(o.get("firstName").getAsString(), "FName");
        // Assert.assertEquals(o.get("lastName").getAsString(), "LName");
        // Assert.assertEquals(o.get("email").getAsString(), "mail@mail.com");
        // Assert.assertEquals(o.get("password").getAsString(), "verysecret");

    }
}