package com.telekom.m2m.cot.restsdk.users;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;

public class UsersAPIIT {

	private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME,
			TestHelper.TEST_PASSWORD);

	@Test
	public void testUsersApi() throws Exception {

		// given

		User user = new User();
		user.setUserName("UserNameIT");
		user.setEmail("mail@mail.com");
		user.setFirstName("FName");
		user.setLastName("LName");
		user.setPassword("verysecret");

		final UsersApi usersApi = cotPlat.getUsersApi();

		// when
		final User storedUser = usersApi.createUser(user, TestHelper.TEST_TENANT);

		// then
		Assert.assertNotNull(storedUser.getId(), "Should now have an Id!");
		Assert.assertNotNull(storedUser.getUserName(), "Should have a userName");
		Assert.assertNotNull(storedUser.getFirstName(), "Should have a firstName");
		Assert.assertNotNull(storedUser.getLastName(), "Should have a lastName");

		usersApi.deleteUserByUserName("UserNameIT", TestHelper.TEST_TENANT);
	}
}