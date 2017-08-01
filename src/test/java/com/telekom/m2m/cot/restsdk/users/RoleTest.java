package com.telekom.m2m.cot.restsdk.users;

import org.junit.Test;
import org.testng.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

public class RoleTest {

	// TODO fix the logger problem below
	// private static final Logger logger =
	// LoggerFactory.getLogger(RolesApiTest.class);

	@Test
	public void testRoleMethods() {

		Role role = new Role();
		role.setId("667");
		role.setName("rolename");

		Gson gson = GsonUtils.createGson();
		String json = gson.toJson(role);

		JsonObject o = gson.fromJson(json, JsonObject.class);
		Assert.assertEquals(o.get("id").getAsString(), "667");
		Assert.assertEquals(o.get("name").getAsString(), "rolename");

	}
}