package com.telekom.m2m.cot.restsdk.users;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;

public class UserApiIT {

    private String host = "https://nbiotdemo.int2-ram.m2m.telekom.com";
    private String userName = "telekom-nbiot@lists.tarent.de";
    private String tenant = "nbiotdemo";
    private String password = "nbiot-Test-Pw";
    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(host, userName, password);
    private String exampleGroup = "admins";
    private String exampleGroup2 = "business";

    @Test
    public void testGenericMethods() throws Exception {

        // given
        User user = new User();
        user.setUserName("UserNameIT");
        user.setEmail("mail@mail.com");
        user.setFirstName("FName");
        user.setLastName("LName");
        user.setPassword("verysecret");

        final UserApi usersApi = cotPlat.getUserApi();

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
        UserCollection collection = cotPlat.getUserApi().getUsers(tenant);

        // then
        Assert.assertNotNull(collection, "It cannot be empty.");
        Assert.assertNotNull(collection.getUsers(), "It cannot be empty.");
        Assert.assertTrue(collection.getUsers().length > 0, "We are logged in, there must be at least one user (us).");

        // when
        RoleCollection roles = cotPlat.getUserApi().getRoles();

        // then
        Assert.assertNotNull(roles, "A list of roles should everytime exist in the cloud.");

    }

    @Test
    public void testCurrentUserMethods() throws Exception {

        // given
        CurrentUser currentuser;

        // when
        currentuser = cotPlat.getUserApi().getCurrentUser();

        // then
        Assert.assertNotNull(currentuser,
                "Current user must always exist, it is the logged in user who performs this operation, which is appearently you, do you exist?");

        // when
        cotPlat.getUserApi().updateCurrentUserFirstName("FirstNameUpdated");
        currentuser.setFirstName(cotPlat.getUserApi().getCurrentUserFirstName());
        // then
        Assert.assertEquals(currentuser.getFirstName(), "FirstNameUpdated",
                "Current user  first name is not equal to the updated value, update failed.");

        // when
        cotPlat.getUserApi().updateCurrentUserLastName("LastNameUpdated");
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

        User testUser = cotPlat.getUserApi().createUser(usertocreate, tenant);

        // then
        Assert.assertNotNull(testUser, "The user must exist.");
        Assert.assertEquals(testUser.getUserName(), "testUser", "User name must match to the one in the cloud");
        Assert.assertEquals(testUser.getFirstName(), "FirstName", "User first name must match to the one in the cloud");
        Assert.assertEquals(testUser.getLastName(), "LastName", "User last name must match to the one in the cloud");

        cotPlat.getUserApi().deleteUser(testUser, tenant);

        // given:
        // (testing the method that creates a user but does not return it)
        User userForNoReturn = new User();
        userForNoReturn.setLastName("lastName");
        userForNoReturn.setFirstName("firstName");
        userForNoReturn.setPassword("password1234");
        userForNoReturn.setUserName("UserforNoReturn");
        // when:
        cotPlat.getUserApi().createUserNoReturn(userForNoReturn, tenant);
        // cotPlat.getUserApi().addUserToGroup(userForNoReturn, tenant, group);
        // then:

        Assert.assertEquals(cotPlat.getUserApi().getUserByName("UserforNoReturn", tenant).getUserName(),
                "UserforNoReturn", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("UserforNoReturn", tenant).getFirstName(), "firstName",
                "First name should match to the first name of the user in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("UserforNoReturn", tenant).getLastName(), "lastName",
                "Last name should match to the last name of the user in the cloud.");

        Assert.assertNull(cotPlat.getUserApi().getUserByName("UserforNoReturn", tenant).getPassword(),
                "Get operation on user password must return a null.");
        cotPlat.getUserApi().deleteUserByUserName("UserforNoReturn", tenant);

        // when
        // (testing the method that creates a user by taking user fields as
        // input)

        cotPlat.getUserApi().createUserNoReturn("NoReturnWithUserFields", tenant, "firstName", "lastName", "password");

        // then:
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("NoReturnWithUserFields", tenant).getUserName(),
                "NoReturnWithUserFields", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("NoReturnWithUserFields", tenant).getLastName(),
                "lastName", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("NoReturnWithUserFields", tenant).getFirstName(),
                "firstName", "Username should match to the user name of the object in the cloud.");
        cotPlat.getUserApi().deleteUserByUserName("NoReturnWithUserFields", tenant);

        // given:
        // Testing the update methods of the user fields:
        User userToUpdateFields = new User();
        userToUpdateFields.setUserName("InitialUserName");
        userToUpdateFields.setFirstName("InitialFirstName");
        userToUpdateFields.setLastName("InitialLastName");
        userToUpdateFields.setPassword("InitialPassword1234");

        // when:
        cotPlat.getUserApi().createUserNoReturn(userToUpdateFields, tenant);
        cotPlat.getUserApi().updateUserFirstName(userToUpdateFields, tenant, "firstNameAfterUpdate");
        cotPlat.getUserApi().updateUserLastName(userToUpdateFields, tenant, "LastNameAfterUpdate");

        // then:

        Assert.assertEquals(cotPlat.getUserApi().getUserByName("InitialUserName", tenant).getUserName(),
                "InitialUserName");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("InitialUserName", tenant).getFirstName(),
                "firstNameAfterUpdate");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("InitialUserName", tenant).getLastName(),
                "LastNameAfterUpdate");

        cotPlat.getUserApi().deleteUserByUserName("InitialUserName", tenant);
    }

    @Test
    public void testGroupMethods() throws Exception {
        // given:
        Group group = new Group();
        group.setName("TestGroup");

        // when:
        Group returnedGroup = cotPlat.getUserApi().createGroup(group, tenant);

        // then
        Assert.assertEquals(group.getName(), returnedGroup.getName(),
                "The group that is created in the cloud should have the same name as the group object created locally.");

        // Now delete that group:
        cotPlat.getUserApi().deleteGroup(returnedGroup, tenant);

        // when
        GroupCollection groups = cotPlat.getUserApi().getGroups(tenant);
        // then
        Assert.assertNotNull(groups, "Groups object cannot be empty.");
        Assert.assertTrue(groups.getGroups().length > 0,
                "There must be at least one group as long as at least one user is in the cloud (that would be the logged in user, which is us)");

        // when (testing to get group by name)
        group = cotPlat.getUserApi().getGroupByName(tenant, exampleGroup);

        // then
        Assert.assertNotNull(group, "The group that is retrieved from the cloud should exist.");
        Assert.assertEquals(group.getName(), "admins",
                "The group name does not match to the name of the group that was retrieved from the cloud.");

        // when (testing to get group by a group object)
        Group newgroup = cotPlat.getUserApi().getGroup(group, tenant);

        // then
        Assert.assertEquals(group.getId(), newgroup.getId(),
                "The returned group name from the cloud should be the same as the group name of the group object created locally.");

        // given: (Create a user and create two groups. Add this user to these
        // groups and retrieve the groups that this user belongs to)
        // cotPlat.getUserApi().deleteUserByUserName("userToCheckGroups",
        // tenant);
        User user = cotPlat.getUserApi().createUser("userToCheckGroups", tenant, "firstName", "lastName", password);
        Group group1 = new Group();
        group1.setName("testGroup1");
        Group group2 = new Group();
        group2.setName("testGroup2"); //

        // cotPlat.getUserApi().deleteGroup(group2, tenant);
        // cotPlat.getUserApi().deleteGroup(group1, tenant);

        Group createdGroup1 = cotPlat.getUserApi().createGroup(group1, tenant);
        Group createdGroup2 = cotPlat.getUserApi().createGroup(group2, tenant);

        // when:

        cotPlat.getUserApi().addUserToGroup(user, tenant, createdGroup1);
        cotPlat.getUserApi().addUserToGroup(user, tenant, createdGroup2);

        // then: (now check if this user is added to the groups)
        GroupReferenceCollection groupCol = cotPlat.getUserApi().getGroupReferencesOfUser(tenant, user);
        GroupReference[] arrayofGroups = groupCol.getGroupReferences();
        GroupReference groupRef1 = arrayofGroups[0];
        GroupReference groupRef2 = arrayofGroups[1];

        Group gettedGroup1 = groupRef1.getGroup();
        Group gettedGroup2 = groupRef2.getGroup();

        Assert.assertEquals(gettedGroup1.getId(), createdGroup1.getId(),
                "the group Id that the user is added to in the cloud should match to the group that is created locally.");
        Assert.assertEquals(gettedGroup2.getId(), createdGroup2.getId(),
                "the group Id that the user is added to in the cloud should match to the group that is created locally.");

        System.out.println("GroupName= " + gettedGroup1.getName());
        System.out.println("GroupName= " + gettedGroup2.getName());

        // When: (now test the remove user from a group method)

        cotPlat.getUserApi().removeUserFromGroup(user, tenant, gettedGroup2);
        // then (now confirm that this group has no users anymore):
        UserReferenceCollection UserCollectionOfGroup2 = cotPlat.getUserApi().getUserReferencesOfGroup(tenant,
                gettedGroup2);
        UserReference[] arrayOfRefOfGroup2 = UserCollectionOfGroup2.getUserReferences();

        System.out.println(arrayOfRefOfGroup2.length);

        // now delete that user and the groups:
        cotPlat.getUserApi().deleteUserByUserName("userToCheckGroups", tenant);
        cotPlat.getUserApi().deleteGroup(createdGroup1, tenant);
        cotPlat.getUserApi().deleteGroup(createdGroup2, tenant);

        /////////

        // when: (testing to get)
        // Group mygroup = cotPlat.getUserApi().getGroupByName(tenant,
        ///////// "testGroupForUserRefs3");

        // UserReferenceCollection collection =
        // cotPlat.getUserApi().getUserReferencesOfGroup(tenant, mygroup);
        // final UserReference[] arrayofref = collection.getUserReferences();
        // final UserReference userRef = arrayofref[0];

        // User user = userRef.getUser();

        // System.out.println("SOMETHING:" + user.getUserName());

    }

}