package com.telekom.m2m.cot.restsdk.users;

import org.junit.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;

public class UsersApiTest {

	// TODO fix the logger problem below
	// private static final Logger logger =
	// LoggerFactory.getLogger(UsersApiTest.class);
	// (test inside the Maven, idea: keep it outside of the library, keep the
	// dependencies as low as possible.)
	// for integration test: assertion.
	//

	@Test
	public void testUserApiMethods() {

		CloudOfThingsPlatform platform = new CloudOfThingsPlatform("https://nbiotdemo.int2-ram.m2m.telekom.com",
				"telekom-nbiot@lists.tarent.de", "nbiot-Test-Pw");

		User user = new User();

		user = platform.getUsersApi().getUserByName("telekom-nbiot@lists.tarent.de", "nbiotdemo");
		user.setLastName("Black");
		// TODO: switch to a proper logger.
		System.out.println(user.getId());
		System.out.println(user.getEmail());
		System.out.println(user.getLastName());

		UserCollection collection = platform.getUsersApi().getUsers("nbiotdemo");

		// TODO: switch to a proper logger.
		System.out.println(collection.getUsers().length);

		GroupCollection groups = platform.getUsersApi().getGroups("nbiotdemo");

		// TODO: switch to a proper logger.
		System.out.println(groups.getGroups().length);

		Group group = platform.getUsersApi().getGroupByName("nbiotdemo", "admins");
		// TODO: switch to a proper logger.
		System.out.println(group.getName());

		CurrentUser currentuser = platform.getUsersApi().getCurrentUser();
		System.out.println(currentuser.getUserName());
		RoleCollection roles = platform.getUsersApi().getRoles();
		System.out.println(roles.getRoles().length);
	}

}
