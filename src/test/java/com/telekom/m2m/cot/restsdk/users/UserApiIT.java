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
        user.setUserName("GenericUserName12");
        user.setEmail("mail@mail2233.com");
        user.setFirstName("FName2233");
        user.setLastName("LName2233");
        user.setPassword("verysecret2233");

        final UserApi usersApi = cotPlat.getUserApi();

        // when
        final User storedUser = usersApi.createUser(user, tenant);

        // then
        Assert.assertNotNull(storedUser.getId(), "Should now have an Id!");
        Assert.assertNotNull(storedUser.getUserName(), "Should have a userName");
        Assert.assertNotNull(storedUser.getFirstName(), "Should have a firstName");
        Assert.assertNotNull(storedUser.getLastName(), "Should have a lastName");

        usersApi.deleteUserByUserName("GenericUserName12", tenant);

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
        usertocreate.setUserName("testUser003");
        usertocreate.setLastName("LastName007");
        usertocreate.setFirstName("FirstName007");
        usertocreate.setPassword("password1234007");

        // when
        // cotPlat.getUserApi().deleteUser(usertocreate, tenant);

        User testUser = cotPlat.getUserApi().createUser(usertocreate, tenant);

        // then
        Assert.assertNotNull(testUser, "The user must exist.");
        Assert.assertEquals(testUser.getUserName(), "testUser003", "User name must match to the one in the cloud");
        Assert.assertEquals(testUser.getFirstName(), "FirstName007",
                "User first name must match to the one in the cloud");
        Assert.assertEquals(testUser.getLastName(), "LastName007", "User last name must match to the one in the cloud");

        cotPlat.getUserApi().deleteUser(testUser, tenant);

        // given:
        // (testing the method that creates a user but does not return it)
        User userForNoReturn = new User();
        userForNoReturn.setLastName("lastName12");
        userForNoReturn.setFirstName("firstName12");
        userForNoReturn.setPassword("password123411");
        userForNoReturn.setUserName("UserforNoReturn12");
        // when:
        cotPlat.getUserApi().createUserNoReturn(userForNoReturn, tenant);

        Assert.assertEquals(cotPlat.getUserApi().getUserByName("UserforNoReturn12", tenant).getUserName(),
                userForNoReturn.getUserName(), "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("UserforNoReturn12", tenant).getFirstName(),
                userForNoReturn.getFirstName(), "First name should match to the first name of the user in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("UserforNoReturn12", tenant).getLastName(),
                userForNoReturn.getLastName(), "Last name should match to the last name of the user in the cloud.");

        Assert.assertNull(cotPlat.getUserApi().getUserByName("UserforNoReturn12", tenant).getPassword(),
                "Get operation on user password must return a null.");

        // Now delete that user.
        cotPlat.getUserApi().deleteUser(userForNoReturn, tenant);
        // when
        // (testing the method that creates a user by taking user fields as
        // input)
        // cotPlat.getUserApi().deleteUserByUserName("NoReturnWithUserFields12",
        // tenant);
        cotPlat.getUserApi().createUserNoReturn("NoReturnWithUserFields12", tenant, "firstName22", "lastName22",
                "password22");

        // then:
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("NoReturnWithUserFields12", tenant).getUserName(),
                "NoReturnWithUserFields12", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("NoReturnWithUserFields12", tenant).getLastName(),
                "lastName22", "Username should match to the user name of the object in the cloud.");
        Assert.assertEquals(cotPlat.getUserApi().getUserByName("NoReturnWithUserFields12", tenant).getFirstName(),
                "firstName22", "Username should match to the user name of the object in the cloud.");
        cotPlat.getUserApi().deleteUserByUserName("NoReturnWithUserFields12", tenant);

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
        // cotPlat.getUserApi().deleteUserByUserName("userToCheckGrp", tenant);
        User user = cotPlat.getUserApi().createUser("userToCheckGrp", tenant, "firstName33", "lastName33", password);
        Group group1 = new Group();
        group1.setName("testGroup00001");
        Group group2 = new Group();
        group2.setName("testGroup00002"); //

        // cotPlat.getUserApi().deleteGroup(cotPlat.getUserApi().getGroupByName(tenant,
        // group1.getName()), tenant);
        // cotPlat.getUserApi().deleteGroup(cotPlat.getUserApi().getGroupByName(tenant,
        // group2.getName()), tenant);

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
                "the group id that the user is added to in the cloud should match to the group that is created locally.");
        Assert.assertEquals(gettedGroup2.getId(), createdGroup2.getId(),
                "the group Id that the user is added to in the cloud should match to the group that is created locally.");

        // When: (now test the remove user from a group method)
        cotPlat.getUserApi().removeUserFromGroup(user, tenant, gettedGroup2);
        // then (now confirm that this group has no users anymore):
        UserReferenceCollection UserCollectionOfGroup2 = cotPlat.getUserApi().getUserReferencesOfGroup(tenant,
                gettedGroup2);
        UserReference[] arrayOfRefOfGroup2 = UserCollectionOfGroup2.getUserReferences();

        Assert.assertEquals(arrayOfRefOfGroup2.length, 0,
                "After removing the only user in the group, the group should have zero users.");
        // now delete that user and the groups:
        cotPlat.getUserApi().deleteUserByUserName("userToCheckGrp", tenant);
        cotPlat.getUserApi().deleteGroup(createdGroup1, tenant);
        cotPlat.getUserApi().deleteGroup(createdGroup2, tenant);

    }

    @Test
    public void testRoleMethods() throws Exception {
        /*
         * Cumulosity has four pre-defined roles that can be assigned. RoleName
         * and Roleid are identical. The roles are as follows:
         * 
         * 1. ROLE_TENANT_STATISTICS_READ 2. ROLE_OPTION_MANAGEMENT_ADMIN 3.
         * ROLE_OPTION_MANAGEMENT_READ 4. ROLE_APPLICATION_MANAGEMENT_ADMIN
         * 5.ROLE_APPLICATION_MANAGEMENT_READ (Please keep this comment for
         * future tests)
         */

        // when (testing getRoles method)

        RoleCollection roles = cotPlat.getUserApi().getRoles();

        // then
        Assert.assertNotNull(roles, "Roles object cannot be empty.");
        Assert.assertTrue(roles.getRoles().length > 0, "There must be at least one role.");

        Assert.assertNotEquals(roles.getRoles().length, 0,
                "By default the roles exist therefore the number of pre-defined roles cannot be zero.");

        // when (testing getting a role by its name)

        Role returnedRole = cotPlat.getUserApi().getRoleByName("ROLE_OPTION_MANAGEMENT_READ");

        // then

        Assert.assertEquals(returnedRole.getName(), "ROLE_OPTION_MANAGEMENT_READ",
                "Returned role name does not match to the requested role name.");

        // when (testing assigning a role to a user)

        User userForRole = cotPlat.getUserApi().createUser("TestUserForRole25", tenant, "firstName", "lastName",
                password);

        cotPlat.getUserApi().assignRoleToUser(userForRole, returnedRole, tenant);

        RoleReferenceCollection roleRefCol = cotPlat.getUserApi().getRolesReferencesOfUser(userForRole, tenant);

        RoleReference[] arrayofRoles2 = roleRefCol.getRoleReferences();
        System.out.println("Array size of roles:" + arrayofRoles2.length);

        RoleReference roleRef1 = arrayofRoles2[0];

        Role testNameRole = roleRef1.getRole();

        Assert.assertEquals(testNameRole.getName(), returnedRole.getName(),
                "The role name should be the same as the role name that is intended to be assigned to the user");

        cotPlat.getUserApi().unassignRoleFromUser(userForRole, returnedRole, tenant);

        System.out.println("number of roles NOW:"
                + cotPlat.getUserApi().getRolesReferencesOfUser(userForRole, tenant).getRoleReferences().length);

        Assert.assertEquals(
                cotPlat.getUserApi().getRolesReferencesOfUser(userForRole, tenant).getRoleReferences().length, 2,
                "After unassigning a role from the user, the number of roles the user has should be two because when a user is created it has by default two roles.");

        cotPlat.getUserApi().deleteUser(userForRole, tenant);

        // Now assign roles to groups:

        Group groupForRoles = new Group();
        groupForRoles.setName("GroupForRoles5");

        Group returnedGroup = cotPlat.getUserApi().createGroup(groupForRoles, tenant);
        cotPlat.getUserApi().assignRoleToGroup(returnedGroup, returnedRole, tenant);

        Assert.assertEquals(
                cotPlat.getUserApi().getRolesReferencesOfGroup(returnedGroup, tenant).getRoleReferences().length, 1,
                "After assigning a role to a newly created group, the number of roles that this group has should be 1 because a new group has zero roles.");

        cotPlat.getUserApi().unassignRoleFromGroup(returnedGroup, returnedRole, tenant);

        Assert.assertEquals(
                cotPlat.getUserApi().getRolesReferencesOfGroup(returnedGroup, tenant).getRoleReferences().length, 0,
                "After removing the only role that a group has, the remaining number of roles should be zero.");

        // now delete that group:
        cotPlat.getUserApi().deleteGroup(returnedGroup, tenant);

    }

}