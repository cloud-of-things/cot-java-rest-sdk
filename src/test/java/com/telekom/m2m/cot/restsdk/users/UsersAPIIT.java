package com.telekom.m2m.cot.restsdk.users;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

public class UsersAPIIT {

    private String host = "https://nbiotdemo.int2-ram.m2m.telekom.com";
    private String userName = "telekom-nbiot@lists.tarent.de";
    private String tenant = "nbiotdemo";
    private String password = "nbiot-Test-Pw";
    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(host, userName, password);
    private String exampleGroup = "admins";
    private CloudOfThingsRestClient cloudOfThingsRestClient;

    @Test
    public void testGenericMethods() throws Exception {

        // given
        User user = new User();
        user.setUserName("UserNameIT");
        user.setEmail("mail@mail.com");
        user.setFirstName("FName");
        user.setLastName("LName");
        user.setPassword("verysecret");

        final UsersApi usersApi = cotPlat.getUsersApi();

        // when
        final User storedUser = usersApi.createUser(user, tenant);

        // then
        Assert.assertNotNull(storedUser.getId(), "Should now have an Id!");
        Assert.assertNotNull(storedUser.getUserName(), "Should have a userName");
        Assert.assertNotNull(storedUser.getFirstName(), "Should have a firstName");
        Assert.assertNotNull(storedUser.getLastName(), "Should have a lastName");

        usersApi.deleteUserByUserName("UserNameIT", tenant);

        /////////

        // when
        UserCollection collection = cotPlat.getUsersApi().getUsers(tenant);

        // then
        Assert.assertNotNull(collection, "It cannot be empty.");
        Assert.assertNotNull(collection.getUsers(), "It cannot be empty.");
        Assert.assertTrue(collection.getUsers().length > 0, "We are logged in, there must be at least one user (us).");

        // when
        GroupCollection groups = cotPlat.getUsersApi().getGroups(tenant);
        // then
        Assert.assertNotNull(groups, "Groups object cannot be empty.");
        Assert.assertTrue(groups.getGroups().length > 0,
                "There must be at least one group as long as at least one user is in the cloud (that would be the logged in user, which is us)");

        // when
        Group group = cotPlat.getUsersApi().getGroupByName(tenant, exampleGroup);

        // then
        Assert.assertNotNull(group, "The group that is retrieved from the cloud should exist.");
        Assert.assertEquals(group.getName(), "admins",
                "The group name does not match to the name of the group that was retrieved from the cloud.");
        // when
        RoleCollection roles = cotPlat.getUsersApi().getRoles();

        // then
        Assert.assertNotNull(roles, "A list of roles should everytime exist in the cloud.");

    }

    @Test
    public void testCurrentUserMethods() throws Exception {

        // given
        CurrentUser currentuser = new CurrentUser();

        // when
        currentuser = cotPlat.getUsersApi().getCurrentUser();

        // then
        Assert.assertNotNull(currentuser,
                "Current user must always exist, it is the logged in user who performs this operation, which is appearently you, do you exist?");

        // when
        cotPlat.getUsersApi().updateCurrentUserFirstName("FirstNameUpdated");
        currentuser.setFirstName(cotPlat.getUsersApi().getCurrentUserFirstName());
        // then
        Assert.assertEquals(currentuser.getFirstName(), "FirstNameUpdated",
                "Current user  first name is not equal to the updated value, update failed.");

        // when
        cotPlat.getUsersApi().updateCurrentUserLastName("LastNameUpdated");
        Assert.assertEquals(currentuser.getLastName(), "LastNameUpdated",
                "Current user last name is not equal to the updated value, update failed.");

    }

    @Test
    public void testGenericUserMethods() throws Exception {

        // given
        User usertocreate = new User();
        usertocreate.setUserName("testUser");
        usertocreate.setLastName("LastName");
        usertocreate.setFirstName("FirstName");
        usertocreate.setPassword("password1234");

        // when

        User testUser = cotPlat.getUsersApi().createUser(usertocreate, tenant);

        // then
        Assert.assertNotNull(testUser, "The user must exist.");
        Assert.assertEquals(testUser.getUserName(), "testUser", "User name must match to the one in the cloud");
        Assert.assertEquals(testUser.getFirstName(), "FirstName", "User first name must match to the one in the cloud");
        Assert.assertEquals(testUser.getLastName(), "LastName", "User last name must match to the one in the cloud");

        cotPlat.getUsersApi().deleteUser(testUser, tenant);

        // given:
        // (testing the method that creates a user but does not return it)
        User userForNoReturn = new User();
        userForNoReturn.setLastName("lastName");
        userForNoReturn.setFirstName("firstName");
        userForNoReturn.setPassword("password1234");
        userForNoReturn.setUserName("UserforNoReturn");

        // when:
        cotPlat.getUsersApi().createUserNoReturn(userForNoReturn, tenant);

        // then:

        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("UserforNoReturn", tenant).getUserName(),
                "UserforNoReturn", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("UserforNoReturn", tenant).getFirstName(), "firstName",
                "First name should match to the first name of the user in the cloud.");
        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("UserforNoReturn", tenant).getLastName(), "lastName",
                "Last name should match to the last name of the user in the cloud.");

        Assert.assertNull(cotPlat.getUsersApi().getUserByName("UserforNoReturn", tenant).getPassword(),
                "Get operation on user password must return a null.");
        cotPlat.getUsersApi().deleteUserByUserName("UserforNoReturn", tenant);

        // when
        // (testing the method that creates a user by taking user fields as
        // input)

        cotPlat.getUsersApi().createUserNoReturn("NoReturnWithUserFields", tenant, "firstName", "lastName", "password");

        // then:
        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("NoReturnWithUserFields", tenant).getUserName(),
                "NoReturnWithUserFields", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("NoReturnWithUserFields", tenant).getLastName(),
                "lastName", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("NoReturnWithUserFields", tenant).getFirstName(),
                "firstName", "Username should match to the user name of the object in the cloud.");
        cotPlat.getUsersApi().deleteUserByUserName("NoReturnWithUserFields", tenant);

        // given:
        // Testing the update methods of the user fields:
        User userToUpdateFields = new User();
        userToUpdateFields.setUserName("InitialUserName");
        userToUpdateFields.setFirstName("InitialFirstName");
        userToUpdateFields.setLastName("InitialLastName");
        userToUpdateFields.setPassword("InitialPassword1234");

        // when:
        cotPlat.getUsersApi().createUserNoReturn(userToUpdateFields, tenant);
        cotPlat.getUsersApi().updateUserFirstName(userToUpdateFields, tenant, "firstNameAfterUpdate");
        cotPlat.getUsersApi().updateUserLastName(userToUpdateFields, tenant, "LastNameAfterUpdate");

        // then:

        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("InitialUserName", tenant).getUserName(),
                "InitialUserName");
        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("InitialUserName", tenant).getFirstName(),
                "firstNameAfterUpdate");
        Assert.assertEquals(cotPlat.getUsersApi().getUserByName("InitialUserName", tenant).getLastName(),
                "LastNameAfterUpdate");

        cotPlat.getUsersApi().deleteUserByUserName("InitialUserName", tenant);
    }
}