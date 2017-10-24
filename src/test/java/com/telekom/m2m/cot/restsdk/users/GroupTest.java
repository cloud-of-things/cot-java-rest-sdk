package com.telekom.m2m.cot.restsdk.users;

import org.testng.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GroupTest {

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

        Assert.assertEquals(ref.getGroup().getName(),group.getName());

    }


    @Test
    public void testDevicePermissions() {
        Group group = new Group();

        Map<String, List<DevicePermission>> permissionsIn = group.getDevicePermissions();
        assertTrue(permissionsIn.isEmpty());

        // check empty field
        group.setDevicePermissions(null);
        permissionsIn = group.getDevicePermissions();
        assertTrue(permissionsIn.isEmpty());

        permissionsIn = new HashMap<String, List<DevicePermission>>();
        permissionsIn.put("Device-A", Arrays.asList(new DevicePermission("EVENT:c8y_Restart:READ"), new DevicePermission("ALARM:*:ADMIN")));
        permissionsIn.put("Device-B", Arrays.asList(new DevicePermission(DevicePermission.Api.ALL, null, DevicePermission.Permission.ALL)));
        group.setDevicePermissions(permissionsIn);

        Map<String, List<DevicePermission>> permissionsOut = group.getDevicePermissions();
        for(Map.Entry<String, List<DevicePermission>> permissions : permissionsIn.entrySet()) {
            assertEquals(permissionsOut.get(permissions.getKey()).toString(), permissions.getValue().toString());
        }
    }


}