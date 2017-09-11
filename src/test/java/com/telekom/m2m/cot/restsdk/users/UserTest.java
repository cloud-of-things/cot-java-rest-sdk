package com.telekom.m2m.cot.restsdk.users;

import org.testng.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.testng.Assert.assertEquals;


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
        Map<String, List<String>> permissionsIn = user.getDevicePermissions();
        assertNull(permissionsIn);

        permissionsIn = new HashMap<>();
        permissionsIn.put("Device-A", Arrays.asList("EVENT:*:READ", "ALARM:*:*READ"));
        permissionsIn.put("Device-B", Arrays.asList("*:*:*"));
        user.setDevicePermissions(permissionsIn);

        Map<String, List<String>> permissionsOut = user.getDevicePermissions();
        assertEquals(permissionsOut, permissionsIn);
    }

}