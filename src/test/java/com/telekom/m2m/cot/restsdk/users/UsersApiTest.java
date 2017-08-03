package com.telekom.m2m.cot.restsdk.users;

import org.junit.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;

public class UsersApiTest {

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

		// Test the functionality of getUsers:
		UserCollection collection = platform.getUsersApi().getUsers("nbiotdemo");
		System.out.println(collection.getUsers().length);
		// Test the functionality of getGroups:
		GroupCollection groups = platform.getUsersApi().getGroups("nbiotdemo");
		System.out.println(groups.getGroups().length);
		// Test the functionality of getGroupByName:
		Group group = platform.getUsersApi().getGroupByName("nbiotdemo", "admins");
		System.out.println(group.getName());
		// Test the functionality of getRoles:
		RoleCollection roles = platform.getUsersApi().getRoles();
		System.out.println(roles.getRoles().length);

		// Test operations on current user:
		CurrentUser currentuser = platform.getUsersApi().getCurrentUser();
		platform.getUsersApi().updateCurrentUserFirstName("FirstNameFirstUpdate");

		System.out.println("Current user first name first update: " + currentuser.getFirstName());
		platform.getUsersApi().updateCurrentUserFirstName("FirstNameSecondUpdate");
		System.out.println("Current user first name second update: " + currentuser.getFirstName());

		User usertocreate = new User();
		usertocreate.setUserName("testUserNameabcdefghi");
		usertocreate.setLastName("testLastNameabcdef");
		usertocreate.setFirstName("testFirstNameabcdef");
		usertocreate.setPassword("TestStrongPWabcdef");

		User createduser = platform.getUsersApi().createUser(usertocreate, "nbiotdemo");
		System.out.println("User name: of the created user:" + createduser.getUserName());

		// deleteuser:
		platform.getUsersApi().deleteUser(createduser, "nbiotdemo");

		System.out.println("User name of the deleted user: " + usertocreate.getUserName());

		// Now create a user and delete it by its name:
		User seconduser = new User();
		seconduser.setLastName("testLastNameabcdefh");
		seconduser.setFirstName("testFirstNameabcdefh");
		seconduser.setPassword("TestStrongPWabcdefh");
		seconduser.setUserName("SecondUserToDeleteByName4");
		User SecondCreatedUser = platform.getUsersApi().createUser(seconduser, "nbiotdemo");
		platform.getUsersApi().deleteUserByUserName("SecondUserToDeleteByName4", "nbiotdemo");

		// Create a user with no return method:
		User userForNoReturn = new User();
		userForNoReturn.setLastName("lastName");
		userForNoReturn.setFirstName("firstName");
		userForNoReturn.setPassword("password1234");
		userForNoReturn.setUserName("UserforNoReturn");
		platform.getUsersApi().createUserNoReturn(userForNoReturn, "nbiotdemo");
		// Now check if this user has successfully been created in the cloud by
		// calling it back:
		System.out.println("The name of the user created by no return method: "
				+ platform.getUsersApi().getUserByName("UserforNoReturn", "nbiotdemo").getUserName());
		// Now delete that user:
		platform.getUsersApi().deleteUserByUserName("UserforNoReturn", "nbiotdemo");

		// Create a user with no return by providing user information:
		platform.getUsersApi().createUserNoReturn("NoReturnWithUserName", "nbiotdemo", "firstname", "lastname",
				"password");
		// Now check if this user has been created in the cloud:
		System.out.println("The name of the user created by no return method: "
				+ platform.getUsersApi().getUserByName("NoReturnWithUserName", "nbiotdemo").getUserName());

		// Now delete that user:
		platform.getUsersApi().deleteUserByUserName("NoReturnWithUserName", "nbiotdemo");
	}

}
