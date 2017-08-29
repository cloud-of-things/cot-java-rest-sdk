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

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(group);

        JsonObject o = gson.fromJson(json, JsonObject.class);
        Assert.assertEquals(o.get("id").getAsString(), "667");

        
        //Now test groupReference set/get functionalities:
        GroupReference ref=new GroupReference();
        ref.setGroup(group);
        //System.out.println(ref.getGroup().getId());
        Assert.assertEquals(ref.getGroup().getName(),group.getName());

    }
}