
package com.telekom.m2m.cot.restsdk.users;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;

public class UserApiIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME,
            TestHelper.TEST_PASSWORD);
    final UserApi userApi = cotPlat.getUserApi();

    private String tenant = TestHelper.TEST_TENANT; // This has to be a tenant,
                                                    // for which the account
                                                    // from TestHelper has the
                                                    // necessary permissions!
                                                    // Be carefully by using of
                                                    // delete functionality to
                                                    // avoid a deletion of the
                                                    // "main" user configured in
                                                    // TestHelper.TEST_USERNAME
    private String password = "test-password";

    private String exampleGroup = "admins";

    private String testUserName = "GenericUserName77";
    private Group group = new Group();
    private String testGroupName = "GenericGroupName77";
    
    private String email = "mail@mail77.com";

    private List<Group> groupsToDelete = new ArrayList<>();
    private List<User> usersToDelete = new ArrayList<>();


    @AfterMethod
    public void tearDown() {
        // We need this in case a test failed in the middle, causing it to
        // skip the delete call.
        for (User user : usersToDelete) {
            try {
                userApi.deleteUser(user, tenant);
            } catch (CotSdkException ex) {
                // This exception is ok, because then the test method managed to
                // delete its own user (should be the norm):
                assertEquals(ex.getHttpStatus(), 404);
            }
        }
        usersToDelete.clear();

        for (Group group : groupsToDelete) {
            try {
                if (group.getId() != null) {
                    userApi.deleteGroup(group, tenant);
                }
            } catch (CotSdkException ex) {
                // This exception is ok, because then the test method managed to
                // delete its own group (should be the norm):
                assertEquals(ex.getHttpStatus(), 404);
            }
        }
        groupsToDelete.clear();
    }

    @Test
    public void testGenericMethods() throws Exception {
        // given
        User user = new User();
        user.setUserName(testUserName);
        user.setEmail(email);
        user.setFirstName("FName2233");
        user.setLastName("LName2233");
        user.setPassword("verysecret2233");

        // when
        final User storedUser = userApi.createUser(user, tenant);
        usersToDelete.add(user);

        // then
        Assert.assertNotNull(storedUser.getId(), "Should now have an Id!");
        assertEquals(storedUser.getUserName(), testUserName, "Should still have the userName");
        assertEquals(storedUser.getFirstName(), "FName2233", "Should still have the firstName");
        assertEquals(storedUser.getLastName(), "LName2233", "Should still have the lastName");

        userApi.deleteUserByUserName(testUserName, tenant);

        /////////

        // when
        UserCollection collection = userApi.getUsers(tenant);

        // then
        Assert.assertNotNull(collection, "It cannot be empty.");
        Assert.assertNotNull(collection.getUsers(), "It cannot be empty.");
        Assert.assertTrue(collection.getUsers().length > 0, "We are logged in, there must be at least one user (us).");

        // when
        RoleCollection roles = userApi.getRoles();

        // then
        Assert.assertNotNull(roles, "A list of roles should always exist in the cloud.");
    }

    @Test
    public void testCurrentUserMethods() throws Exception {

        // given
        CurrentUser currentuser;

        // when
        currentuser = userApi.getCurrentUser();

        // then
        Assert.assertNotNull(currentuser,
                "Current user must always exist, it is the logged in user who performs this operation, which is appearently you, do you exist?");

    }

    @Test
    public void testGenericUserMethods() throws Exception {

        // given:
        User usertocreate = new User();
        usertocreate.setUserName(testUserName);
        usertocreate.setLastName("LastName007");
        usertocreate.setFirstName("FirstName007");
        usertocreate.setPassword("password1234007");

        // when:
        User testUser = userApi.createUser(usertocreate, tenant);
        usersToDelete.add(usertocreate);

        // then:
        Assert.assertNotNull(testUser, "The user must exist.");
        assertEquals(testUser.getUserName(), testUserName, "User name must match to the one in the cloud");
        assertEquals(testUser.getFirstName(), "FirstName007", "User first name must match to the one in the cloud");
        assertEquals(testUser.getLastName(), "LastName007", "User last name must match to the one in the cloud");

        // now delete that user.
        userApi.deleteUser(testUser, tenant);

        // given:
        // (testing the method that creates a user but does not return it)
        User userForNoReturn = new User();
        userForNoReturn.setLastName("lastName12");
        userForNoReturn.setFirstName("firstName12");
        userForNoReturn.setPassword("password123411");
        userForNoReturn.setUserName(testUserName);

        // when:
        userApi.createUser(userForNoReturn, tenant);
        usersToDelete.add(userForNoReturn);
        User returnedUser = userApi.getUserByName(testUserName, tenant);

        // then:
        assertEquals(returnedUser.getUserName(), userForNoReturn.getUserName(),
                "Username should match to the user name of the object in the cloud.");
        assertEquals(returnedUser.getFirstName(), userForNoReturn.getFirstName(),
                "First name should match to the first name of the user in the cloud.");
        assertEquals(returnedUser.getLastName(), userForNoReturn.getLastName(),
                "Last name should match to the last name of the user in the cloud.");
        Assert.assertNull(returnedUser.getPassword(), "Get operation on user password must return a null.");

        // Now delete that user.
        userApi.deleteUser(userForNoReturn, tenant);

        // when:
        // (testing the method that creates a user by taking user fields as
        // input)
        User user = userApi.createUser(testUserName, tenant, "firstName22", "lastName22", "password22");
        usersToDelete.add(user);
        returnedUser = userApi.getUserByName(testUserName, tenant);

        // then:
        assertEquals(returnedUser.getUserName(), testUserName,
                "Username should match to the user name of the object in the cloud.");
        assertEquals(returnedUser.getLastName(), "lastName22",
                "Last name should match to the user name of the object in the cloud.");
        assertEquals(returnedUser.getFirstName(), "firstName22",
                "First name should match to the user name of the object in the cloud.");

        // now delete that user:
        userApi.deleteUserByUserName(testUserName, tenant);

        // given:
        // (Testing the update methods of the user fields):
        User userToUpdateFields = new User();
        userToUpdateFields.setUserName(testUserName);
        userToUpdateFields.setFirstName("InitialFirstName");
        userToUpdateFields.setLastName("InitialLastName");
        userToUpdateFields.setPassword("InitialPassword1234");

        // when:
        userApi.createUser(userToUpdateFields, tenant);
        usersToDelete.add(userToUpdateFields);
        returnedUser = userApi.getUserByName(testUserName, tenant);

        // then:
        assertEquals(returnedUser.getUserName(), testUserName);

        // now delete that user.
        userApi.deleteUserByUserName(testUserName, tenant);

        // given (testing the updateUser method):
        User userForUpdate = new User();
        userForUpdate.setUserName(testUserName);
        userForUpdate.setFirstName("FirstNameBeforeUpdate");
        userForUpdate.setLastName("LastNameBeforeUpdate");
        userForUpdate.setPassword("verySecretPassword123");
        userForUpdate.setEmail(email);

        // Now create this user in the cloud and update its fields:
        User userInCloud = userApi.createUser(userForUpdate, tenant);
        usersToDelete.add(userForUpdate);
        userInCloud.setFirstName("FirstNameAfterUpdate");
        userInCloud.setLastName("LastNameAfterUpdate");
        userInCloud.setEmail("emailAfterUpdate@something.com");

        // when: (now update the user):
        userApi.updateUser(userInCloud, tenant);

        // now return that user from the cloud:
        returnedUser = userApi.getUserByName(testUserName, tenant);

        // then (now check whether the update worked as expected):
        Assert.assertNotEquals(returnedUser.getFirstName(), "FirstNameBeforeUpdate",
                "FirstName of the user was not successfully updated.");
        Assert.assertNotEquals(returnedUser.getLastName(), "LastNameBeforeUpdate",
                "LastName of the user was not successfully updated.");
        Assert.assertNotEquals(returnedUser.getEmail(), email, "email of the user was not successfully updated.");

        // now delete that user from the cloud:
        userApi.deleteUser(returnedUser, tenant);
    }

    @Test
    public void testGroupMethods() throws Exception {

        // given:
        Group group = new Group();
        group.setName("TestGroup");

        // when:
        Group returnedGroup = userApi.createGroup(group, tenant);
        groupsToDelete.add(returnedGroup);

        // then:
        assertEquals(group.getName(), returnedGroup.getName(),
                "The group that is created in the cloud should have the same name as the group object created locally.");

        // Now delete that group:
        userApi.deleteGroup(returnedGroup, tenant);

        // when:
        GroupCollection groups = userApi.getGroups(tenant);

        // then:
        Assert.assertNotNull(groups, "Groups object cannot be empty.");
        Assert.assertTrue(groups.getGroups().length > 0,
                "There must be at least one group as long as at least one user is in the cloud (that would be the logged in user, which is us)");

        // when (testing to get group by name):
        group = userApi.getGroupByName(tenant, exampleGroup);

        // then:
        Assert.assertNotNull(group, "The group that is retrieved from the cloud should exist.");
        assertEquals(group.getName(), "admins",
                "The group name does not match to the name of the group that was retrieved from the cloud.");

        // when (testing to get group by a group object):
        Group newgroup = userApi.getGroupById(group.getId(), tenant);

        // then:
        assertEquals(group.getId(), newgroup.getId(),
                "The returned group name from the cloud should be the same as the group name of the group object created locally.");

        // given: (Create a user and create two groups. Add this user to these
        // groups and retrieve the groups that this user belongs to)
        User user = userApi.createUser("userToCheckGrp", tenant, "firstName33", "lastName33", password);
        usersToDelete.add(user);
        Group group1 = new Group();
        group1.setName("testGroup00001");
        Group group2 = new Group();
        group2.setName("testGroup00002");

        Group createdGroup1 = userApi.createGroup(group1, tenant);
        groupsToDelete.add(createdGroup1);
        Group createdGroup2 = userApi.createGroup(group2, tenant);
        groupsToDelete.add(createdGroup2);

        // when:
        userApi.addUserToGroup(user, tenant, createdGroup1);
        userApi.addUserToGroup(user, tenant, createdGroup2);

        // then: (now check if this user is added to the groups)
        GroupReferenceCollection groupCol = userApi.getGroupReferencesOfUser(tenant, user);
        GroupReference[] arrayofGroups = groupCol.getGroupReferences();
        GroupReference groupRef1 = arrayofGroups[0];
        GroupReference groupRef2 = arrayofGroups[1];
        Group gettedGroup1 = groupRef1.getGroup();
        Group gettedGroup2 = groupRef2.getGroup();
        assertEquals(gettedGroup1.getId(), createdGroup1.getId(),
                "the group id that the user is added to in the cloud should match to the group that is created locally.");
        assertEquals(gettedGroup2.getId(), createdGroup2.getId(),
                "the group Id that the user is added to in the cloud should match to the group that is created locally.");

        // When: (now test the remove user from a group method)
        userApi.removeUserFromGroup(user, tenant, gettedGroup2);
        // then (now confirm that this group has no users anymore):
        UserReferenceCollection UserCollectionOfGroup2 = userApi.getUserReferencesOfGroup(tenant, gettedGroup2);
        UserReference[] arrayOfRefOfGroup2 = UserCollectionOfGroup2.getUserReferences();

        assertEquals(arrayOfRefOfGroup2.length, 0,
                "After removing the only user in the group, the group should have zero users.");
        // now delete that user and the groups:
        userApi.deleteUserByUserName("userToCheckGrp", tenant);
        userApi.deleteGroup(createdGroup1, tenant);
        userApi.deleteGroup(createdGroup2, tenant);

        // given: (now test updateGroup method):
        Group groupToUpdate = new Group();
        groupToUpdate.setName("InitialGroupName108");

        // Create the group in the cloud:
        groupToUpdate = userApi.createGroup(groupToUpdate, tenant);
        groupsToDelete.add(groupToUpdate);

        // when: (Update the name of the group)
        groupToUpdate.setName("FinalGroupName108");
        userApi.updateGroup(groupToUpdate, tenant);

        // then: (now return this group from the cloud and check if its name is
        // correctly updated)
        groupToUpdate = userApi.getGroupById(groupToUpdate.getId(), tenant);
        assertEquals("FinalGroupName108", groupToUpdate.getName(), "updateGroup method failed.");

        // Now delete that group:
        userApi.deleteGroup(groupToUpdate, tenant);

    }

    @Test
    public void testRoleMethods() throws Exception {
        /*
         * The cloud has four pre-defined roles that can be assigned. RoleName
         * and Roleid are identical. The roles are as follows:
         * 
         * 1. ROLE_TENANT_STATISTICS_READ 2. ROLE_OPTION_MANAGEMENT_ADMIN 3.
         * ROLE_OPTION_MANAGEMENT_READ 4. ROLE_APPLICATION_MANAGEMENT_ADMIN
         * 5.ROLE_APPLICATION_MANAGEMENT_READ (Please keep this comment for
         * future tests)
         */

        // when (testing the getRoles method):

        RoleCollection roles = userApi.getRoles();

        // then:
        Assert.assertNotNull(roles, "Roles object cannot be empty.");
        Assert.assertTrue(roles.getRoles().length > 0, "There must be at least one role.");
        Assert.assertNotEquals(roles.getRoles().length, 0,
                "By default the roles exist therefore the number of pre-defined roles cannot be zero.");

        // when (testing getting a role by its name):

        Role returnedRole = userApi.getRoleByName("ROLE_OPTION_MANAGEMENT_READ");

        // then:
        assertEquals(returnedRole.getName(), "ROLE_OPTION_MANAGEMENT_READ",
                "Returned role name does not match to the requested role name.");

        // when (testing assigning a role to a user):
        User userForRole = userApi.createUser("TestUserForRole27", tenant, "firstName", "lastName", password);
        usersToDelete.add(userForRole);
        userApi.assignRoleToUser(userForRole, returnedRole, tenant);
        RoleReferenceCollection roleRefCol = userApi.getRolesReferencesOfUser(userForRole, tenant);
        RoleReference[] arrayOfRoles2 = roleRefCol.getRoleReferences();

        long numOfFoundRoles = Arrays.stream(arrayOfRoles2)
                .filter(roleReference -> roleReference.getRole().getName().equals(returnedRole.getName())).count();

        // then:
        assertEquals(numOfFoundRoles, 1,
                "The role name should be the same as the role name that is intended to be assigned to the user");

        // when:
        userApi.unassignRoleFromUser(userForRole, returnedRole, tenant);

        // then:
        assertEquals(userApi.getRolesReferencesOfUser(userForRole, tenant).getRoleReferences().length, 2,
                "After unassigning a role from the user, the number of roles the user has should be two because when a user is created it has by default two roles.");

        userApi.deleteUser(userForRole, tenant);

        // Now assign roles to groups:
        // given:
        Group groupForRoles = new Group();
        groupForRoles.setName("GroupForRoles5");
        Group returnedGroup = userApi.createGroup(groupForRoles, tenant);
        groupsToDelete.add(returnedGroup);

        // when:
        userApi.assignRoleToGroup(returnedGroup, returnedRole, tenant);

        // then:
        assertEquals(userApi.getRolesReferencesOfGroup(returnedGroup, tenant).getRoleReferences().length, 1,
                "After assigning a role to a newly created group, the number of roles that this group has should be 1 because a new group has zero roles.");

        // when:
        userApi.unassignRoleFromGroup(returnedGroup, returnedRole, tenant);

        // then:
        assertEquals(userApi.getRolesReferencesOfGroup(returnedGroup, tenant).getRoleReferences().length, 0,
                "After removing the only role that a group has, the remaining number of roles should be zero.");

        // now delete that group:
        userApi.deleteGroup(returnedGroup, tenant);

    }

    @Test
    public void testDevicePermissionsForUsers() throws Exception {

        // given (first create a user where then we can set device permissions):
        User usertocreate = new User();
        usertocreate.setUserName(testUserName);
        usertocreate.setLastName("LastName007");
        usertocreate.setFirstName("FirstName007");
        usertocreate.setPassword("password1234007");
        userApi.createUser(usertocreate, tenant);
        usersToDelete.add(usertocreate);
        User userInCloud = userApi.getUserByName(testUserName, tenant);

        // when (now prepare a map of permissions as below and assign these
        // permissions to the created user):
        // We can assign more than one device id, and more than one permission
        // type.
        Map<String, List<DevicePermission>> devicePermission = new LinkedHashMap<>();

        List<DevicePermission> list1 = new ArrayList<>();
        list1.add(new DevicePermission("ALARM:*:READ"));
        list1.add(new DevicePermission("AUDIT:*:READ"));
        // These are real device ids, however cloud does not check their
        // validity; one can also provide a random string as a device id.
        devicePermission.put("10481", list1);

        List<DevicePermission> list2 = new ArrayList<>();
        list2.add(new DevicePermission("OPERATION:*:READ"));
        list2.add(new DevicePermission("EVENT:*:READ"));
        devicePermission.put("10445", list2);

        userInCloud.setDevicePermissions(devicePermission);
        userApi.updateUser(userInCloud, tenant);

        // when (now let's return this user from the cloud and check if the
        // device permissions were assigned as expected):
        User returned = userApi.getUserByName(testUserName, tenant);

        Map<String, List<DevicePermission>> returnedDevicePermissions = returned.getDevicePermissions();
        assertEquals(devicePermission.size(), returnedDevicePermissions.size());

        assertTrue(returnedDevicePermissions.get("10481").contains("ALARM:*:READ"));
        assertTrue(returnedDevicePermissions.get("10481").contains("AUDIT:*:READ"));
        assertFalse(returnedDevicePermissions.get("10481").contains("OPERATION:*:READ"));
        assertFalse(returnedDevicePermissions.get("10481").contains("EVENT:*:READ"));
        
        assertTrue(returnedDevicePermissions.get("10445").contains("OPERATION:*:READ"));
        assertTrue(returnedDevicePermissions.get("10445").contains("EVENT:*:READ"));
        assertFalse(returnedDevicePermissions.get("10445").contains("ALARM:*:READ"));
        assertFalse(returnedDevicePermissions.get("10445").contains("AUDIT:*:READ"));
        
        // now delete that user:
        userApi.deleteUserByUserName(testUserName, tenant);
    }

    @Test
    public void testDevicePermissionsForGroups() throws Exception {
        
        // given (first create a group in the cloud where then we can set device permissions):
        group.setName(testGroupName);
        userApi.createGroup(group, tenant);
        groupsToDelete.add(group);
        group = userApi.getGroupByName(tenant,testGroupName);

        // when (now prepare a map of permissions as below and assign these
        // permissions to the created group):
        // We can assign more than one device id, and more than one permission
        // type.
        Map<String, List<String>> devicePermission = new LinkedHashMap<>();
        List<String> list1 = new ArrayList<>();

        list1.add("ALARM:*:READ");
        list1.add("AUDIT:*:READ");

        // These are real device ids, however cloud does not check their
        // validity; one can also provide a random string as a device id.
        devicePermission.put("10481", list1);
        List<String> list2 = new ArrayList<>();

        list2.add("OPERATION:*:READ");
        list2.add("EVENT:*:READ");
        devicePermission.put("10445", list2);
        group.setDevicePermissions(devicePermission);
        userApi.updateGroup(group, tenant);

        // when (now let's return this group from the cloud and check if the
        // device permissions were assigned as expected):

        group = userApi.getGroupByName(tenant, testGroupName);

        assertEquals(devicePermission.keySet().size(), group.getDevicePermissions().keySet().size());

        assertTrue((group.getDevicePermissions().get("10481").contains("ALARM:*:READ")));
        assertTrue((group.getDevicePermissions().get("10481").contains("AUDIT:*:READ")));
        assertFalse((group.getDevicePermissions().get("10481").contains("OPERATION:*:READ")));
        assertFalse((group.getDevicePermissions().get("10481").contains("EVENT:*:READ")));
        
        assertTrue((group.getDevicePermissions().get("10445").contains("OPERATION:*:READ")));
        assertTrue((group.getDevicePermissions().get("10445").contains("EVENT:*:READ")));
        assertFalse((group.getDevicePermissions().get("10445").contains("ALARM:*:READ")));
        assertFalse((group.getDevicePermissions().get("10445").contains("AUDIT:*:READ")));
        
    }
    
}