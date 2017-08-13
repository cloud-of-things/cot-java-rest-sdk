package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Use the UserApi to work with users. Created by Ozan Arslan on 13.07.2017
 *
 */
public class UserApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.user+json; charset=UTF-8; ver=0.9";

    private final Gson gson = GsonUtils.createGson();

    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient
     *            the configured rest client.
     */
    public UserApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Method to return the collection of users in a given tenant.
     * 
     * @param tenant
     * @return an instance of UserCollection
     */
    public UserCollection getUsers(String tenant) {
        return new UserCollection(cloudOfThingsRestClient, tenant);
    }

    /**
     * Method to retrieve collection of groups in a given tenant.
     * 
     * @param tenant
     * @return an instance of GroupCollection
     */
    public GroupCollection getGroups(String tenant) {
        return new GroupCollection(cloudOfThingsRestClient, "user/" + tenant + "/groups/", gson, null);
    }

    /**
     * The method to create a group in the cloud.
     * 
     * @param group
     * @param tenant
     * @return the group that is created.
     */
    public Group createGroup(Group group, String tenant) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.group+json; charset=UTF-8; ver=0.9";
        String json = "{\"name\":\"" + group.getName() + "\"}";
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/groups", CONTENT);
        group.setId(id);

        return group;
    }

    /**
     * The method to delete a group from the cloud.
     * 
     * @param group
     * @param tenant
     */
    public void deleteGroup(Group group, String tenant) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete(groupId, "user/" + tenant + "/groups");
    }

    /**
     * Method to retrieve a user by username, in a given tenant.
     * 
     * @param userName
     * @param tenant
     * @return an instance of a user.
     */
    public User getUserByName(String userName, String tenant) {
        String result = cloudOfThingsRestClient.getResponse("user/" + tenant + "/users/" + userName, CONTENT_TYPE);
        User user = new User(gson.fromJson(result, ExtensibleObject.class));
        return user;
    }

    /**
     * Method to retrieve a group by its name.
     * 
     * @param tenant
     * @param groupName
     * @return an instance of a Group.
     */
    public Group getGroupByName(String tenant, String groupName) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.group+json;ver=0.9";
        String result = cloudOfThingsRestClient.getResponse("user/" + tenant + "/groupByName/" + groupName, CONTENT);
        Group group = new Group(gson.fromJson(result, ExtensibleObject.class));
        return group;
    }

    /**
     * The method to retrieve a specific group from the cloud.
     * 
     * @param group
     * @param tenant
     * @return the group that is requested.
     */
    public Group getGroup(Group group, String tenant) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.group+json;ver=0.9";
        String groupId = Long.toString(group.getId());
        String result = cloudOfThingsRestClient.getResponse(groupId, "user/" + tenant + "/groups", CONTENT);
        Group returnedgroup = new Group(gson.fromJson(result, ExtensibleObject.class));
        return returnedgroup;
    }

    // Operations on Current User:

    /**
     * Method to retrieve the currently logged in user.
     * 
     * @return an instance of the currently logged in user.
     */
    public CurrentUser getCurrentUser() {
        String result = cloudOfThingsRestClient.getResponse("user/currentUser", CONTENT_TYPE);
        CurrentUser currentuser = new CurrentUser(gson.fromJson(result, ExtensibleObject.class));
        return currentuser;
    }

    /**
     * The method to retrieve the first name of the user.
     * 
     * @return first name of the current user.
     */
    public String getCurrentUserFirstName() {
        String firstname = getCurrentUser().getFirstName();
        return firstname;
    }

    /**
     * The method to retrieve the last name of the user.
     * 
     * @return first name of the current user.
     */
    public String getCurrentUserLastName() {
        String lastname = getCurrentUser().getLastName();
        return lastname;
    }

    // The below method should be allowed, currentuser is you, you can get
    // request your own
    // password.
    /**
     * @return the password of the current user.
     */
    public String getCurrentUserPassword() {
        String password = getCurrentUser().getPassword();
        return password;
    }

    /**
     * The method to update the first name of the current user.
     * 
     * @param firstName
     */
    public void updateCurrentUserFirstName(String firstName) {
        User user = new User();
        user.setFirstName(firstName);
        String json = gson.toJson(user);
        cloudOfThingsRestClient.doPutRequest(json, "user/currentUser", CONTENT_TYPE);
    }

    /**
     * The method to update the last name of the current user.
     * 
     * @param lastName
     */
    public void updateCurrentUserLastName(String lastName) {
        User user = new User();
        user.setLastName(lastName);
        String json = gson.toJson(user);
        cloudOfThingsRestClient.doPutRequest(json, "user/currentUser", CONTENT_TYPE);
    }

    /**
     * The method to update the password of the current user.
     * 
     * @param password
     */
    public void updateCurrentUserPassword(String password) {
        User user = new User();
        user.setPassword(password);
        String json = gson.toJson(user);
        cloudOfThingsRestClient.doPutRequest(json, "user/currentUser", CONTENT_TYPE);
    }

    // Operations on a generic User:
    /**
     * A method to create a user in the cloud from a pre-defined user object and
     * return it.
     * 
     * @param user
     * @param tenant
     * @return an instance of a User
     */
    public User createUser(User user, String tenant) {
        String json = gson.toJson(user);
        // post request:
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/users", CONTENT_TYPE);
        user.setId(id);
        return user;
    }

    /**
     * A method to create a user in the cloud from a pre-defined user object.
     * 
     * @param user
     * @param tenant
     */
    public void createUserNoReturn(User user, String tenant) {
        String json = gson.toJson(user);
        // post request:
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/users", CONTENT_TYPE);
        user.setId(id);
    }

    /**
     * A method to create a user in the cloud and return it by providing a
     * userName, password, firstName, lastName and a tenant.
     * 
     * @param userName
     *            of the user.
     * @param tenant
     *            of the tenant where the user resides.
     * @param firstName
     *            of the user.
     * @param lastName
     *            of the user.
     * @param password
     *            of the user.
     * @return an instance of the user.
     */
    public User createUser(String userName, String tenant, String firstName, String lastName, String password) {
        User user = new User();
        user.setUserName(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        String json = gson.toJson(user);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/users", CONTENT_TYPE);
        user.setId(id);
        return user;
    }

    /**
     * A method to create a user in the cloud and return it by providing a
     * userName, password, firstName, lastName and a tenant.
     * 
     * @param userName
     *            of the user.
     * @param tenant
     *            of the tenant where the user resides.
     * @param firstName
     *            of the user.
     * @param lastName
     *            of the user.
     * @param password
     *            of the user.
     */
    public void createUserNoReturn(String userName, String tenant, String firstName, String lastName, String password) {
        User user = new User();
        user.setUserName(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        String json = gson.toJson(user);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/users", CONTENT_TYPE);
        user.setId(id);
    }

    /**
     * The method to delete a specific user from the cloud.
     * 
     * @param user
     * @param tenant
     */
    public void deleteUser(User user, String tenant) {
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/users");
    }

    /**
     * The method to delete a specific user from the cloud by providing the
     * username.
     * 
     * @param userName
     * @param tenant
     */
    public void deleteUserByUserName(String userName, String tenant) {
        User user = getUserByName(userName, tenant);
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/users");
    }

    /**
     * The method to update the first name of a specific user.
     * 
     * @param user
     * @param tenant
     * @param firstName
     */
    public void updateUserFirstName(User user, String tenant, String firstName) {
        String json = "{\"firstName\":\"" + firstName + "\"}";
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT_TYPE);
    }

    /**
     * The method to update the last name of a specific user.
     * 
     * @param user
     * @param tenant
     * @param lastName
     */
    public void updateUserLastName(User user, String tenant, String lastName) {
        String json = "{\"lastName\":\"" + lastName + "\"}";
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT_TYPE);
    }

    /**
     * The method to update the password of a specific user.
     * 
     * @param user
     * @param tenant
     * @param password
     */
    public void updateUserPassword(User user, String tenant, String password) {
        String json = "{\"password\":\"" + password + "\"}";
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT_TYPE);
    }

    // Methods related to groups:

    /**
     * The method to add a specific user to a group.
     * 
     * @param user
     * @param tenant
     * @param group
     */
    public void addUserToGroup(User user, String tenant, Group group) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.userReference+json;ver=0.9";
        String json = "{\"user\":{\"self\": \"" + user.getSelf(user, tenant) + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/groups/" + group.getId() + "/users", CONTENT);
    }

    /**
     * The method to add the current user to a group.
     * 
     * @param user
     * @param tenant
     * @param group
     */
    public void addCurrentUserToGroup(CurrentUser user, String tenant, Group group) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.userReference+json;ver=0.9";
        String json = "{\"user\":{\"self\": \"" + user.getSelf(user, tenant) + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/groups/" + group.getId() + "/users", CONTENT);
    }

    /**
     * The method to remove a user from a group.
     * 
     * @param user
     * @param tenant
     * @param group
     */
    public void removeUserFromGroup(User user, String tenant, Group group) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/groups/" + groupId + "/users");
    }

    /**
     * The method to remove the current user from a group.
     * 
     * @param user
     * @param tenant
     * @param group
     */
    public void removeCurrentUserFromGroup(CurrentUser user, String tenant, Group group) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete("user/" + tenant + "/groups/" + groupId + "/users/" + user.getUserName() + "/");
    }

    /**
     * The method to get the user references of the group
     * 
     * @param tenant
     * @param group
     * @return the set of references (URLs) of users in the group
     */
    public UserReferenceCollection getUserReferencesOfGroup(String tenant, Group group) {
        String groupId = Long.toString(group.getId());
        String URL = "user/" + tenant + "/groups/" + groupId + "/users";
        return new UserReferenceCollection(cloudOfThingsRestClient, URL, gson, null);
    }

    public GroupReferenceCollection getGroupReferencesOfUser(String tenant, User user) {
        String URL = "user/" + tenant + "/users/" + user.getUserName() + "/groups";
        return new GroupReferenceCollection(cloudOfThingsRestClient, URL, gson, null);
    }

    /// Methods related to roles:

    /**
     * Method to retrieve a collection of roles.
     * 
     * @return an instance of RoleCollection
     */
    public RoleCollection getRoles() {
        return new RoleCollection(cloudOfThingsRestClient, "user/roles", gson, null);
    }

    // TODO: it seems that the following two methods are not allowed by
    // the cloud.
    // Going to keep them for now just in case we might be wrong.
    /**
     * The method to create a role in the cloud.
     * 
     * @param role
     * @return the created role
     */
    public Role createRole(Role role) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.role+json; charset=UTF-8; ver=0.9";
        String json = "{\"name\":\"" + role.getName() + "\"}";
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/roles", CONTENT);
        role.setId(id);
        return role;
    }

    /**
     * The method to delete a role.
     * 
     * @param role
     * @param tenant
     */
    public void deleteRole(Role role, String tenant) {
        cloudOfThingsRestClient.delete(role.getId(), "user/roles/");
    }

    /**
     * The method to retrieve a role by providing its name
     * 
     * @param name
     * @return the requested role
     */
    public Role getRoleByName(String name) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.role+json;ver=0.9";
        String result = cloudOfThingsRestClient.getResponse(name, "user/roles", CONTENT);
        Role returnedrole = new Role(gson.fromJson(result, ExtensibleObject.class));
        return returnedrole;
    }

    /**
     * The method to assign a role to a user.
     * 
     * @param user
     * @param role
     * @param tenant
     */
    public void assignRoleToUser(User user, Role role, String tenant) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.roleReference+json; charset=UTF-8; ver=0.9";
        String json = "{\"role\":{\"self\": \"user/roles/" + role.getId() + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/users/" + user.getId() + "/roles", CONTENT);
    }

    /**
     * The method to unassign a user from a role.
     * 
     * @param user
     * @param role
     * @param tenant
     */
    public void unassignRoleFromUser(User user, Role role, String tenant) {
        cloudOfThingsRestClient.delete(role.getName(), "user/" + tenant + "/users/" + user.getUserName() + "/roles");
    }

    /**
     * The method to retrieve a collection of references (URLs) of roles that a
     * user is assigned to.
     * 
     * @param user
     * @param tenant
     * @return a collection of role references of a user.
     */
    public RoleReferenceCollection getRolesReferencesOfUser(User user, String tenant) {
        String URL = "user/" + tenant + "/users/" + user.getUserName() + "/roles";
        return new RoleReferenceCollection(cloudOfThingsRestClient, URL, gson, null);
    }

    /**
     * The method to assign a role to a group of users.
     * 
     * @param group
     * @param role
     * @param tenant
     */
    public void assignRoleToGroup(Group group, Role role, String tenant) {
        String groupId = Long.toString(group.getId());
        String CONTENT = "application/vnd.com.nsn.cumulocity.roleReference+json; charset=UTF-8; ver=0.9";
        String json = "{\"role\":{\"self\": \"user/roles/" + role.getId() + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/groups/" + groupId + "/roles", CONTENT);
    }

    /**
     * The groups can have more than one role. This method returns the
     * collection of references of roles that the group is assigned to.
     * 
     * @param group
     * @param tenant
     * @return collection of roles that the group is assigned to.
     */
    public RoleReferenceCollection getRolesReferencesOfGroup(Group group, String tenant) {
        String groupId = Long.toString(group.getId());
        String URL = "user/" + tenant + "/groups/" + groupId + "/roles";
        return new RoleReferenceCollection(cloudOfThingsRestClient, URL, gson, null);
    }

    /**
     * The method to unassign a group from a role.
     * 
     * @param group
     * @param role
     * @param tenant
     */
    public void unassignRoleFromGroup(Group group, Role role, String tenant) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete(role.getName(), "user/" + tenant + "/groups/" + groupId + "/roles");
    }

    /**
     * The method to update the fields of a user in the cloud.
     * 
     * @param user
     * @param tenant
     */
    public void updateUser(User user, String tenant) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.user+json; charset=UTF-8; ver=0.9";
        String json = gson.toJson(user);
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT);
    }

    /**
     * The method to update the fields of a group in the cloud.
     * 
     * @param user
     * @param tenant
     */
    public void updateGroup(Group group, String tenant) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.group+json;ver=0.9";
        String json = gson.toJson(group);
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + group.getId(), CONTENT);
    }
}
