package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class UserTest {

    @Test
    public void testUserMethods() {

        User user = new User();
        user.setUserName("RandomName");
        user.setId("667");
        user.setEmail("mail@mail.com");
        user.setFirstName("FName");
        user.setLastName("LName");
        user.setPassword("verysecret");

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(user);

        JsonObject o = gson.fromJson(json, JsonObject.class);
        assertEquals(o.get("userName").getAsString(), "RandomName");
        assertEquals(o.get("id").getAsString(), "667");
        assertEquals(o.get("firstName").getAsString(), "FName");
        assertEquals(o.get("lastName").getAsString(), "LName");
        assertEquals(o.get("email").getAsString(), "mail@mail.com");
        assertEquals(o.get("password").getAsString(), "verysecret");
        
        String[] forbidden= {" ","$","\\","/","+",":","nameWith Space", "DollarSign$Name",
                "Back\\SlashName", "Slash/Name", "NameWith+Sign","NameWith:Symbol","Mixture$\\/+: Name"};
        for (String x : forbidden) {
            try {
                user.setUserName(x);
                Assert.fail("Restricted characters should not be allowed in  userName. Failed name is "+x);
               } catch (CotSdkException ex  ) {
               }
        }
    }

    @Test
    public void testDevicePermissions() {
        User user = new User();
        Map<String, List<DevicePermission>> permissionsIn = user.getDevicePermissions();
        assertTrue(permissionsIn.isEmpty());

        // check empty field
        user.setDevicePermissions(null);
        permissionsIn = user.getDevicePermissions();
        assertTrue(permissionsIn.isEmpty());

        permissionsIn = new HashMap<>();
        permissionsIn.put("Device-A", Arrays.asList(new DevicePermission("EVENT:c8y_Restart:READ"), new DevicePermission("ALARM:*:ADMIN")));
        permissionsIn.put("Device-B", Arrays.asList(new DevicePermission(DevicePermission.Api.ALL, null, DevicePermission.Permission.ALL)));
        user.setDevicePermissions(permissionsIn);

        Map<String, List<DevicePermission>> permissionsOut = user.getDevicePermissions();
        for(Map.Entry<String, List<DevicePermission>> permissions : permissionsIn.entrySet()) {
            assertEquals(permissionsOut.get(permissions.getKey()).toString(), permissions.getValue().toString());
        }
    }

}