package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.TestHelper;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import org.testng.annotations.Test;


public class UserApiTest {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME,
            TestHelper.TEST_PASSWORD);

    @Test
    public void testUserApiMethods() {
        UserApi userApi = cotPlat.getUserApi();

        // Test operations on a generic user:

        // Create a user in the cloud by using a User object:
        User usertocreate = new User();
        usertocreate.setUserName("testUser");
        usertocreate.setLastName("LastName");
        usertocreate.setFirstName("FirstName");
        usertocreate.setPassword("password1234");

        User createduser = userApi.createUser(usertocreate, TestHelper.TEST_TENANT);
        System.out.println("User name: of the created user:" + createduser.getUserName());

        User user = userApi.getUserByName("testUser", TestHelper.TEST_TENANT);
        user.setLastName("Black");
        System.out.println(user.getId());
        System.out.println(user.getEmail());
        System.out.println(user.getLastName());

        // Test the functionality of getUsers:
        UserCollection collection = userApi.getUsers(TestHelper.TEST_TENANT);
        System.out.println(collection.getUsers().length);
        // Test the functionality of getGroups:
        GroupCollection groups = userApi.getGroups(TestHelper.TEST_TENANT);
        System.out.println(groups.getGroups().length);
        // Test the functionality of getGroupByName:
        Group group = userApi.getGroupByName(TestHelper.TEST_TENANT, "admins");
        System.out.println(group.getName());
        // Test the functionality of getRoles:
        RoleCollection roles = userApi.getRoles();
        System.out.println(roles.getRoles().length);

        // Now delete that user:
        userApi.deleteUser(createduser, TestHelper.TEST_TENANT);

        // Now create a user and delete it by its name:
        User seconduser = new User();
        seconduser.setLastName("testLastNameabcdefh");
        seconduser.setFirstName("testFirstNameabcdefh");
        seconduser.setPassword("TestStrongPWabcdefh");
        seconduser.setUserName("SecondUserToDeleteByName4");
        userApi.createUser(seconduser, TestHelper.TEST_TENANT);
        userApi.deleteUserByUserName("SecondUserToDeleteByName4", TestHelper.TEST_TENANT);

        // Create a user with no return method:
        User userForNoReturn = new User();
        userForNoReturn.setLastName("lastName");
        userForNoReturn.setFirstName("firstName");
        userForNoReturn.setPassword("password1234");
        userForNoReturn.setUserName("UserforNoReturn");
        userApi.createUser(userForNoReturn, TestHelper.TEST_TENANT);
        // Now check if this user has successfully been created in the cloud by
        // calling it back:
        System.out.println("The name of the user created by no return method: "
                + userApi.getUserByName("UserforNoReturn", TestHelper.TEST_TENANT).getUserName());
        // Now delete that user:
        userApi.deleteUserByUserName("UserforNoReturn", TestHelper.TEST_TENANT);

        // Create a user with no return by providing user information:
        userApi.createUser("NoReturnWithUserName", TestHelper.TEST_TENANT, "firstname", "lastname",
                "password");
        // Now check if this user has been created in the cloud:
        System.out.println("The name of the user created by no return method: "
                + userApi.getUserByName("NoReturnWithUserName", TestHelper.TEST_TENANT).getUserName());

        // Now delete that user:
        userApi.deleteUserByUserName("NoReturnWithUserName", TestHelper.TEST_TENANT);

        // Create a user in the cloud and update its fields:
        User userToUpdateFields = new User();
        userToUpdateFields.setUserName("InitialUserName");
        userToUpdateFields.setFirstName("InitialFirstName");
        userToUpdateFields.setLastName("InitialLastName");
        userToUpdateFields.setPassword("InitialPassword1234");
        userApi.createUser(userToUpdateFields, TestHelper.TEST_TENANT);
        // check if that worked:
        System.out.println("User first name before update: "
                + userApi.getUserByName("InitialUserName", TestHelper.TEST_TENANT).getFirstName());

        // check if that worked:
        System.out.println("Password before update (should be null):"
                + userApi.getUserByName("InitialUserName", TestHelper.TEST_TENANT).getPassword());
        System.out.println("Password after Update (should be null):"
                + userApi.getUserByName("InitialUserName", TestHelper.TEST_TENANT).getPassword());
        // now delete that user from the cloud:
        userApi.deleteUserByUserName("InitialUserName", TestHelper.TEST_TENANT);
    }

}
