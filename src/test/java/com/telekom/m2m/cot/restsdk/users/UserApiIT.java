package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

public class UserApiIT {

    private static final String USERNAME = "testUsername";
    private static final String FIRST_NAME = "testFirstName";
    private static final String LAST_NAME = "testLastName";
    private static final String PASSWORD = "testPassword";
    private static final String GROUP_ADMINS = "admins";
    private static final String GROUP_NAME_1 = "genericGroupName1";
    private static final String GROUP_NAME_2 = "genericGroupName2";
    private static final String EMAIL = "mail@mail77.com";
    private static final String ROLE_NAME = "ROLE_OPTION_MANAGEMENT_READ";
    private static final String ROLES_GROUP_NAME = "testRolesGroup";
    private static final String DEVICE_ID_1 = "10111";
    private static final String DEVICE_ID_2 = "10222";

    private final CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private final UserApi userApi = cloudOfThingsPlatform.getUserApi();

    // This has to be a tenant, for which the account from TestHelper has the necessary permissions!
    // Be careful when using delete functionality in order to avoid a deletion of the "main" user configured in TestHelper.TEST_USERNAME
    private final String tenant = TestHelper.TEST_TENANT;

    private List<Group> groupsToDelete = new ArrayList<>();
    private List<User> usersToDelete = new ArrayList<>();


    @AfterMethod
    public void tearDown() {
        for (User user : usersToDelete) {
            try {
                userApi.deleteUser(user, tenant);
            } catch (CotSdkException ex) {
                // This exception is ok, because then the test method managed to delete its own user (should be the norm):
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
                // This exception is ok, because then the test method managed to delete its own group (should be the norm):
                assertEquals(ex.getHttpStatus(), 404);
            }
        }
        groupsToDelete.clear();
    }

    @Test
    public void testUserCollectionNotEmpty() {
        UserCollection collection = userApi.getUsers(tenant);

        assertNotNull(collection, String.format("User collection of tenant %s is null", tenant));
        assertNotNull(collection.getUsers(), String.format("List of users for tenant %s is null", tenant));
        assertTrue(collection.getUsers().length > 0, "We are logged in, in user collection must be at least one user (us).");
    }

    @Test
    public void testCurrentUserExists() {
        CurrentUser currentuser = userApi.getCurrentUser();

        assertNotNull(currentuser, "Current user must always exist, it is the logged in user who performs this operation.");
    }

    @Test
    public void testCreateUserByUserObject() {
        User returnedUser = createUserInCot();
        usersToDelete.add(returnedUser);

        assertNotNull(returnedUser.getId(), "Created user should have an ID.");
        assertEquals(returnedUser.getUserName(), USERNAME, "Created user and local user object should have same username.");
        assertEquals(returnedUser.getFirstName(), FIRST_NAME, "Created user and local user object should have same first name.");
        assertEquals(returnedUser.getLastName(), LAST_NAME, "Created user and local user object should have same last name.");
        assertEquals(returnedUser.getEmail(), EMAIL, "Created user and local user object should have same email.");
    }

    @Test
    public void testRetrieveCreatedUserFromCot() {
        User returnedUser = createUserInCot();
        usersToDelete.add(returnedUser);
        User userFoundInCot = userApi.getUserByName(USERNAME, tenant);

        assertEquals(userFoundInCot.getUserName(), USERNAME, "User retrieved from CoT and local user object should have same username.");
        assertEquals(userFoundInCot.getFirstName(), FIRST_NAME, "User retrieved from CoT and local user object should have same first name.");
        assertEquals(userFoundInCot.getLastName(), LAST_NAME, "User retrieved from CoT and local user object should have same last name.");
        assertEquals(userFoundInCot.getEmail(), EMAIL, "User retrieved from CoT and local user object should have same email.");
        assertNull(userFoundInCot.getPassword(), "Get operation on user password must return a null.");
    }

    @Test
    public void testCreateUserByArguments() {
        // testing the method that creates a user by taking user fields as input
        User returnedUser = userApi.createUser(USERNAME, tenant, FIRST_NAME, LAST_NAME, PASSWORD);
        usersToDelete.add(returnedUser);
        User userFoundInCot = userApi.getUserByName(USERNAME, tenant);

        assertEquals(userFoundInCot.getUserName(), USERNAME, "Username should match to the username of the object in the cloud.");
        assertEquals(userFoundInCot.getFirstName(), FIRST_NAME, "First name should match to the username of the object in the cloud.");
        assertEquals(userFoundInCot.getLastName(), LAST_NAME, "Last name should match to the username of the object in the cloud.");
    }

    @Test
    public void testUpdateUser() {
        User createdUser = createUserInCot();
        usersToDelete.add(createdUser);
        createdUser.setFirstName("FirstNameAfterUpdate");
        createdUser.setLastName("LastNameAfterUpdate");
        createdUser.setEmail("emailAfterUpdate@something.com");

        userApi.updateUser(createdUser, tenant);

        User userFoundByUsername = userApi.getUserByName(USERNAME, tenant);

        Assert.assertEquals(userFoundByUsername.getFirstName(), "FirstNameAfterUpdate", "FirstName of the user was not successfully updated.");
        Assert.assertEquals(userFoundByUsername.getLastName(), "LastNameAfterUpdate", "LastName of the user was not successfully updated.");
        Assert.assertEquals(userFoundByUsername.getEmail(), "emailAfterUpdate@something.com", "Email of the user was not successfully updated.");
    }

    @Test
    public void testReturnedCreatedGroup() {
        Group group = new Group();
        group.setName(GROUP_NAME_1);

        Group returnedGroup = userApi.createGroup(group, tenant);
        groupsToDelete.add(returnedGroup);

        assertEquals(returnedGroup.getName(), GROUP_NAME_1, "The group that is created in the cloud should have the same name as the group object created locally.");
    }

    @Test
    public void testCreatedGroupRetrievedFromCot() {
        Group group = new Group();
        group.setName(GROUP_NAME_1);

        Group returnedGroup = userApi.createGroup(group, tenant);
        groupsToDelete.add(returnedGroup);
        Group groupFoundByName = userApi.getGroupByName(tenant, GROUP_ADMINS);

        assertNotNull(groupFoundByName, "The group that is retrieved from the cloud should exist.");
        assertEquals(groupFoundByName.getName(), GROUP_ADMINS, "The group name does not match to the name of the group that was retrieved from the cloud.");
    }

    @Test
    public void testCreateGroupAndGetGroupById() {
        Group group = new Group();
        group.setName(GROUP_NAME_1);

        Group returnedGroup = userApi.createGroup(group, tenant);
        groupsToDelete.add(returnedGroup);

        GroupCollection groupCollection = userApi.getGroups(tenant);
        Group groupRetrievedFromCot = userApi.getGroupById(returnedGroup.getId(), tenant);

        assertNotNull(groupCollection, "Group collection object cannot be empty.");
        assertTrue(groupCollection.getGroups().length > 0, "There must be at least one group in group collection as at least one user is present in the cloud (that would be the logged in user, which is us)");
        assertEquals(returnedGroup.getId(), groupRetrievedFromCot.getId(), "The returned group name from the cloud should be the same as the group name of the group object created locally.");
    }

    @Test
    public void testGetGroupReferencesOfUser() {
        User returnedUser = createUserInCot();
        usersToDelete.add(returnedUser);
        Group group1 = new Group();
        group1.setName(GROUP_NAME_1);
        Group group2 = new Group();
        group2.setName(GROUP_NAME_2);

        Group returnedGroup1 = userApi.createGroup(group1, tenant);
        groupsToDelete.add(returnedGroup1);
        Group returnedGroup2 = userApi.createGroup(group2, tenant);
        groupsToDelete.add(returnedGroup2);

        userApi.addUserToGroup(returnedUser, tenant, returnedGroup1);
        userApi.addUserToGroup(returnedUser, tenant, returnedGroup2);

        GroupReferenceCollection groupReferenceCollection = userApi.getGroupReferencesOfUser(tenant, returnedUser);
        GroupReference[] groupReferenceArray = groupReferenceCollection.getGroupReferences();
        GroupReference groupReference1 = groupReferenceArray[0];
        GroupReference groupReference2 = groupReferenceArray[1];
        Group groupRetrievedFromCot1 = groupReference1.getGroup();
        Group groupRetrievedFromCot2 = groupReference2.getGroup();

        // Order of the group IDs that are returned from the CLoud of Things does not seem to be guaranteed.
        // We check that all expected groups are referenced, regardless of the order.
        assertEqualsNoOrder(
            new Long[]{groupRetrievedFromCot1.getId(), groupRetrievedFromCot2.getId()},
            new Long[]{returnedGroup1.getId(),returnedGroup2.getId()},
            "The group IDs that the user is added to in the cloud should match to the group IDs that are created locally."
        );
    }

    @Test
    public void testDeleteGroup() {
        User returnedUser = createUserInCot();
        usersToDelete.add(returnedUser);
        Group group = new Group();
        group.setName(GROUP_NAME_1);

        Group returnedGroup1 = userApi.createGroup(group, tenant);
        groupsToDelete.add(returnedGroup1);
        userApi.addUserToGroup(returnedUser, tenant, returnedGroup1);

        GroupReferenceCollection groupReferenceCollectionBeforeDeletingGroup = userApi.getGroupReferencesOfUser(tenant, returnedUser);
        GroupReference[] groupReferenceArrayBeforeDeletingGroup = groupReferenceCollectionBeforeDeletingGroup.getGroupReferences();
        GroupReference groupReferenceBeforeDeletingGroup = groupReferenceArrayBeforeDeletingGroup[0];
        Group groupRetrievedFromCot = groupReferenceBeforeDeletingGroup.getGroup();
        assertEquals(groupReferenceArrayBeforeDeletingGroup.length, 1, "Group reference collection must contain one group created previously.");

        userApi.deleteGroup(groupRetrievedFromCot, tenant);
        GroupReferenceCollection groupReferenceCollectionAfterDeletingGroup = userApi.getGroupReferencesOfUser(tenant, returnedUser);
        GroupReference[] groupReferenceArrayAfterDeletingGroup = groupReferenceCollectionAfterDeletingGroup.getGroupReferences();
        assertEquals(groupReferenceArrayAfterDeletingGroup.length, 0, "After deleting a group there should be no group present.");
    }

    @Test
    public void testRemoveUserFromGroup() {
        User returnedUser = createUserInCot();
        usersToDelete.add(returnedUser);
        Group group = new Group();
        group.setName(GROUP_NAME_1);
        Group returnedGroup = userApi.createGroup(group, tenant);
        groupsToDelete.add(returnedGroup);
        userApi.addUserToGroup(returnedUser, tenant, returnedGroup);

        GroupReferenceCollection groupReferenceCollection = userApi.getGroupReferencesOfUser(tenant, returnedUser);
        GroupReference[] groupReferenceArray = groupReferenceCollection.getGroupReferences();
        GroupReference groupReference = groupReferenceArray[0];
        Group groupRetrievedFromCot = groupReference.getGroup();
        UserReferenceCollection userReferenceCollectionBeforeDeletingUser = userApi.getUserReferencesOfGroup(tenant, groupRetrievedFromCot);
        UserReference[] userReferencesArrayBeforeDeletingUser = userReferenceCollectionBeforeDeletingUser.getUserReferences();

        assertEquals(userReferencesArrayBeforeDeletingUser.length, 1, "There should be one user assigned to the group.");

        userApi.removeUserFromGroup(returnedUser, tenant, groupRetrievedFromCot);
        UserReferenceCollection userReferenceCollectionAfterDeletingUser = userApi.getUserReferencesOfGroup(tenant, groupRetrievedFromCot);
        UserReference[] userReferencesArrayAfterDeletingUser = userReferenceCollectionAfterDeletingUser.getUserReferences();

        assertEquals(userReferencesArrayAfterDeletingUser.length, 0, "After removing the only user in the group, the group should have zero users.");
    }

    @Test
    public void testUpdateGroup() {
        Group groupToUpdate = new Group();
        groupToUpdate.setName(GROUP_NAME_1);
        groupToUpdate = userApi.createGroup(groupToUpdate, tenant);
        groupsToDelete.add(groupToUpdate);

        groupToUpdate.setName(GROUP_NAME_2);
        userApi.updateGroup(groupToUpdate, tenant);
        Group groupRetrievedFromCot = userApi.getGroupById(groupToUpdate.getId(), tenant);

        assertEquals(groupRetrievedFromCot.getName(), GROUP_NAME_2, "Group name update has failed.");
    }

    /*
     * The cloud has four pre-defined roles that can be assigned. RoleName and Role ID are identical. The roles are as follows:
     *
     * 1. ROLE_TENANT_STATISTICS_READ
     * 2. ROLE_OPTION_MANAGEMENT_ADMIN
     * 3. ROLE_OPTION_MANAGEMENT_READ
     * 4. ROLE_APPLICATION_MANAGEMENT_ADMIN
     * 5. ROLE_APPLICATION_MANAGEMENT_READ (Please keep this comment for future tests)
     */

    @Test
    public void testRolesNotNull() {
        RoleCollection roles = userApi.getRoles();

        assertNotNull(roles, "A list of roles should always exist in the cloud.");
        assertTrue(roles.getRoles().length > 0, "There must be at least one role.");
    }

    @Test
    public void testGetRoleByName() {
        Role returnedRole = userApi.getRoleByName(ROLE_NAME);

        assertEquals(returnedRole.getName(), ROLE_NAME, "Returned role name does not match to the requested role name.");
    }

    @Test
    public void testAssignRoleToUser() {
        User userForRole = createUserInCot();
        usersToDelete.add(userForRole);
        RoleReferenceCollection roleReferenceCollection = userApi.getRolesReferencesOfUser(userForRole, tenant);
        RoleReference[] roleReferencesArray = roleReferenceCollection.getRoleReferences();

        assertEquals(roleReferencesArray.length, 2, "The number of roles of a user should be two because when a user is created it has by default two roles.");

        Role roleInCot = userApi.getRoleByName(ROLE_NAME);
        userApi.assignRoleToUser(userForRole, roleInCot, tenant);
        RoleReferenceCollection roleReferenceCollectionAfterAssigningRole = userApi.getRolesReferencesOfUser(userForRole, tenant);
        RoleReference[] roleReferencesArrayAfterAssigningRole = roleReferenceCollectionAfterAssigningRole.getRoleReferences();
        long rolesNumberWithTestRoleName = Arrays.stream(roleReferencesArrayAfterAssigningRole).filter(roleReference -> roleReference.getRole().getName().equals(roleInCot.getName())).count();

        assertEquals(roleReferencesArrayAfterAssigningRole.length, 3, "The number of roles of a user should be three, because of two initial roles and additional role assigned previously.");
        assertEquals(rolesNumberWithTestRoleName, 1, "The role name should be the same as the role name that is intended to be assigned to the user");
    }

    @Test
    public void testUnassignRoleFromUser() {
        User userForRole = createUserInCot();
        usersToDelete.add(userForRole);
        Role roleInCot = userApi.getRoleByName(ROLE_NAME);
        userApi.assignRoleToUser(userForRole, roleInCot, tenant);
        RoleReferenceCollection roleReferenceCollection = userApi.getRolesReferencesOfUser(userForRole, tenant);
        RoleReference[] roleReferencesArray = roleReferenceCollection.getRoleReferences();

        assertEquals(roleReferencesArray.length, 3, "The number of roles of a user should be three, because of two initial roles and additional role assigned previously.");

        userApi.unassignRoleFromUser(userForRole, roleInCot, tenant);

        assertEquals(userApi.getRolesReferencesOfUser(userForRole, tenant).getRoleReferences().length, 2,
                "After unassigning a role from the user, the number of roles the user has should be two because when a user is created it has by default two roles.");
    }

    @Test
    public void testAssignAndUnassignRoleToGroup() {
        Group groupForRoles = new Group();
        groupForRoles.setName(ROLES_GROUP_NAME);
        Group returnedGroup = userApi.createGroup(groupForRoles, tenant);
        groupsToDelete.add(returnedGroup);
        Role returnedRole = userApi.getRoleByName(ROLE_NAME);

        userApi.assignRoleToGroup(returnedGroup, returnedRole, tenant);

        assertEquals(userApi.getRolesReferencesOfGroup(returnedGroup, tenant).getRoleReferences().length, 1,
                "After assigning a role to a newly created group, the number of roles that this group has should be 1 because a new group has zero roles.");

        userApi.unassignRoleFromGroup(returnedGroup, returnedRole, tenant);

        assertEquals(userApi.getRolesReferencesOfGroup(returnedGroup, tenant).getRoleReferences().length, 0,
                "After removing the only role that a group has, the remaining number of roles should be zero.");
    }

    @Test
    public void testAssignDevicePermissionsToUser() {
        User returnedUser = createUserInCot();
        usersToDelete.add(returnedUser);
        //User userInCloud = userApi.getUserByName(TEST_USERNAME, tenant);

        Map<String, List<DevicePermission>> devicePermissionsMap = prepareDevicePermissions();
        returnedUser.setDevicePermissions(devicePermissionsMap);
        userApi.updateUser(returnedUser, tenant);

        User userInCot = userApi.getUserByName(USERNAME, tenant);

        Map<String, List<DevicePermission>> returnedDevicePermissions = userInCot.getDevicePermissions();
        assertEquals(devicePermissionsMap.size(), returnedDevicePermissions.size());

        assertTrue(returnedDevicePermissions.get(DEVICE_ID_1).toString().contains("ALARM:*:READ"));
        assertTrue(returnedDevicePermissions.get(DEVICE_ID_1).toString().contains("AUDIT:*:READ"));
        assertFalse(returnedDevicePermissions.get(DEVICE_ID_1).toString().contains("OPERATION:*:READ"));
        assertFalse(returnedDevicePermissions.get(DEVICE_ID_1).toString().contains("EVENT:*:READ"));

        assertTrue(returnedDevicePermissions.get(DEVICE_ID_2).toString().contains("OPERATION:*:READ"));
        assertTrue(returnedDevicePermissions.get(DEVICE_ID_2).toString().contains("EVENT:*:READ"));
        assertFalse(returnedDevicePermissions.get(DEVICE_ID_2).toString().contains("ALARM:*:READ"));
        assertFalse(returnedDevicePermissions.get(DEVICE_ID_2).toString().contains("AUDIT:*:READ"));
    }

    @Test
    public void testAssignDevicePermissionsForGroups() {
        Group group = new Group();
        group.setName(GROUP_NAME_1);
        userApi.createGroup(group, tenant);
        groupsToDelete.add(group);
        group = userApi.getGroupByName(tenant, GROUP_NAME_1);
        Map<String, List<DevicePermission>> devicePermissionsMap = prepareDevicePermissions();
        group.setDevicePermissions(devicePermissionsMap);
        userApi.updateGroup(group, tenant);
        Group groupRetrievedFromCot = userApi.getGroupByName(tenant, GROUP_NAME_1);

        assertEquals(devicePermissionsMap.keySet().size(), group.getDevicePermissions().keySet().size());

        assertTrue((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_1).toString().contains("ALARM:*:READ")));
        assertTrue((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_1).toString().contains("AUDIT:*:READ")));
        assertFalse((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_1).toString().contains("OPERATION:*:READ")));
        assertFalse((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_1).toString().contains("EVENT:*:READ")));

        assertTrue((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_2).toString().contains("OPERATION:*:READ")));
        assertTrue((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_2).toString().contains("EVENT:*:READ")));
        assertFalse((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_2).toString().contains("ALARM:*:READ")));
        assertFalse((groupRetrievedFromCot.getDevicePermissions().get(DEVICE_ID_2).toString().contains("AUDIT:*:READ")));
    }

    private User createUserInCot() {
        User userToCreate = new User();
        userToCreate.setUserName(USERNAME);
        userToCreate.setFirstName(FIRST_NAME);
        userToCreate.setLastName(LAST_NAME);
        userToCreate.setPassword(PASSWORD);
        userToCreate.setEmail(EMAIL);

        return userApi.createUser(userToCreate, tenant);
    }

    private Map<String, List<DevicePermission>> prepareDevicePermissions() {
        List<DevicePermission> devicePermissionsList1 = new ArrayList<>();
        devicePermissionsList1.add(new DevicePermission(DevicePermission.Api.ALARM, null, DevicePermission.Permission.READ));
        devicePermissionsList1.add(new DevicePermission("AUDIT:*:READ"));
        List<DevicePermission> devicePermissionsList2 = new ArrayList<>();
        devicePermissionsList2.add(new DevicePermission(DevicePermission.Api.OPERATION, "*", DevicePermission.Permission.READ));
        devicePermissionsList2.add(new DevicePermission("EVENT:*:READ"));

        // These are real device ids, however cloud does not check their validity; one can also provide a random string as a device id.
        Map<String, List<DevicePermission>> devicePermissionsMap = new LinkedHashMap<>();
        devicePermissionsMap.put(DEVICE_ID_1, devicePermissionsList1);
        devicePermissionsMap.put(DEVICE_ID_2, devicePermissionsList2);

        return devicePermissionsMap;
    }
}