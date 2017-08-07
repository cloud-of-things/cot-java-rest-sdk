package com.telekom.m2m.cot.restsdk.users;

import org.junit.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;

public class UserApiTest {

    @Test
    public void testUserApiMethods() {

        CloudOfThingsPlatform platform = new CloudOfThingsPlatform("https://nbiotdemo.int2-ram.m2m.telekom.com",
                "telekom-nbiot@lists.tarent.de", "nbiot-Test-Pw");

        User user = platform.getUserApi().getUserByName("telekom-nbiot@lists.tarent.de", "nbiotdemo");
        user.setLastName("Black");
        System.out.println(user.getId());
        System.out.println(user.getEmail());
        System.out.println(user.getLastName());

        // Test the functionality of getUsers:
        UserCollection collection = platform.getUserApi().getUsers("nbiotdemo");
        System.out.println(collection.getUsers().length);
        // Test the functionality of getGroups:
        GroupCollection groups = platform.getUserApi().getGroups("nbiotdemo");
        System.out.println(groups.getGroups().length);
        // Test the functionality of getGroupByName:
        Group group = platform.getUserApi().getGroupByName("nbiotdemo", "admins");
        System.out.println(group.getName());
        // Test the functionality of getRoles:
        RoleCollection roles = platform.getUserApi().getRoles();
        System.out.println(roles.getRoles().length);

        // Test operations on current user:
        CurrentUser currentuser = platform.getUserApi().getCurrentUser();
        platform.getUserApi().updateCurrentUserFirstName("FirstNameFirstUpdate");

        System.out.println("Current user first name first update: " + currentuser.getFirstName());
        platform.getUserApi().updateCurrentUserFirstName("FirstNameSecondUpdate");
        System.out.println("Current user first name second update: " + currentuser.getFirstName());
        // Now switch the current user name to the first value:
        platform.getUserApi().updateCurrentUserFirstName("FirstNameFirstUpdate");

        // Test operations on a generic user:

        // Create a user in the cloud by using a User object:
        User usertocreate = new User();
        usertocreate.setUserName("testUser");
        usertocreate.setLastName("LastName");
        usertocreate.setFirstName("FirstName");
        usertocreate.setPassword("password1234");

        User createduser = platform.getUserApi().createUser(usertocreate, "nbiotdemo");
        System.out.println("User name: of the created user:" + createduser.getUserName());

        // Now delete that user:
        platform.getUserApi().deleteUser(createduser, "nbiotdemo");

        // Now create a user and delete it by its name:
        User seconduser = new User();
        seconduser.setLastName("testLastNameabcdefh");
        seconduser.setFirstName("testFirstNameabcdefh");
        seconduser.setPassword("TestStrongPWabcdefh");
        seconduser.setUserName("SecondUserToDeleteByName4");
        User SecondCreatedUser = platform.getUserApi().createUser(seconduser, "nbiotdemo");
        platform.getUserApi().deleteUserByUserName("SecondUserToDeleteByName4", "nbiotdemo");

        // Create a user with no return method:
        User userForNoReturn = new User();
        userForNoReturn.setLastName("lastName");
        userForNoReturn.setFirstName("firstName");
        userForNoReturn.setPassword("password1234");
        userForNoReturn.setUserName("UserforNoReturn");
        platform.getUserApi().createUserNoReturn(userForNoReturn, "nbiotdemo");
        // Now check if this user has successfully been created in the cloud by
        // calling it back:
        System.out.println("The name of the user created by no return method: "
                + platform.getUserApi().getUserByName("UserforNoReturn", "nbiotdemo").getUserName());
        // Now delete that user:
        platform.getUserApi().deleteUserByUserName("UserforNoReturn", "nbiotdemo");

        // Create a user with no return by providing user information:
        platform.getUserApi().createUserNoReturn("NoReturnWithUserName", "nbiotdemo", "firstname", "lastname",
                "password");
        // Now check if this user has been created in the cloud:
        System.out.println("The name of the user created by no return method: "
                + platform.getUserApi().getUserByName("NoReturnWithUserName", "nbiotdemo").getUserName());

        // Now delete that user:
        platform.getUserApi().deleteUserByUserName("NoReturnWithUserName", "nbiotdemo");

        // Create a user in the cloud and update its fields:
        User userToUpdateFields = new User();
        userToUpdateFields.setUserName("InitialUserName");
        userToUpdateFields.setFirstName("InitialFirstName");
        userToUpdateFields.setLastName("InitialLastName");
        userToUpdateFields.setPassword("InitialPassword1234");
        platform.getUserApi().createUserNoReturn(userToUpdateFields, "nbiotdemo");
        // check if that worked:
        System.out.println("User first name before update: "
                + platform.getUserApi().getUserByName("InitialUserName", "nbiotdemo").getFirstName());
        // update its first name:
        platform.getUserApi().updateUserFirstName(userToUpdateFields, "nbiotdemo", "firstNameAfterUpdate");
        // check if that worked:
        System.out.println("First Name after Update:"
                + platform.getUserApi().getUserByName("InitialUserName", "nbiotdemo").getFirstName());
        // update its last name:
        platform.getUserApi().updateUserLastName(userToUpdateFields, "nbiotdemo", "LastNameAfterUpdate");
        // check if that worked:
        System.out.println("Last Name after Update:"
                + platform.getUserApi().getUserByName("InitialUserName", "nbiotdemo").getLastName());
        // update its password:
        platform.getUserApi().updateUserPassword(userToUpdateFields, "nbiotdemo", "passwordAfterUpdate");
        // check if that worked:
        System.out.println("Password before update (should be null):"
                + platform.getUserApi().getUserByName("InitialUserName", "nbiotdemo").getPassword());
        System.out.println("Password after Update (should be null):"
                + platform.getUserApi().getUserByName("InitialUserName", "nbiotdemo").getPassword());
        // now delete that user from the cloud:
        platform.getUserApi().deleteUserByUserName("InitialUserName", "nbiotdemo");
    }

}
