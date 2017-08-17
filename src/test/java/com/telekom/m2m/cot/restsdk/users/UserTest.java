package com.telekom.m2m.cot.restsdk.users;

import org.junit.Test;
import org.testng.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

public class UserTest {

	// TODO fix the logger problem below
	// private static final Logger logger =
	// LoggerFactory.getLogger(UsersApiTest.class);

	@Test
	public void testUserMethods() {

		User user = new User();
		user.setId("667");
		user.setEmail("mail@mail.com");
		user.setFirstName("FName");
		user.setLastName("LName");
		user.setPassword("verysecret");

		Gson gson = GsonUtils.createGson();
		String json = gson.toJson(user);

		JsonObject o = gson.fromJson(json, JsonObject.class);
		Assert.assertEquals(o.get("id").getAsString(), "667");
		Assert.assertEquals(o.get("firstName").getAsString(), "FName");
		Assert.assertEquals(o.get("lastName").getAsString(), "LName");
		Assert.assertEquals(o.get("email").getAsString(), "mail@mail.com");
		Assert.assertEquals(o.get("password").getAsString(), "verysecret");

	}
}