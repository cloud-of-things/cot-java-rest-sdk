package com.telekom.m2m.cot.restsdk.users;

import java.util.Map;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter.FilterBuilder;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Use the UserApi to work with users. Created by Ozan Arslan on 13.07.2017
 */
public class UserApi {

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE_USER = "application/vnd.com.nsn.cumulocity.user+json; charset=UTF-8; ver=0.9";
    private static final String CONTENT_TYPE_GROUP = "application/vnd.com.nsn.cumulocity.group+json; charset=UTF-8; ver=0.9";
    private static final String CONTENT_TYPE_USER_REF = "application/vnd.com.nsn.cumulocity.userReference+json; charset=UTF-8; ver=0.9";
    private static final String CONTENT_TYPE_ROLE = "application/vnd.com.nsn.cumulocity.role+json; charset=UTF-8; ver=0.9";
    private static final String CONTENT_TYPE_ROLE_REF = "application/vnd.com.nsn.cumulocity.roleReference+json; charset=UTF-8; ver=0.9";

    private final Gson gson = GsonUtils.createGson();

    public UserApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public UserCollection getUsers(String tenant) {
        return new UserCollection(cloudOfThingsRestClient, tenant);
    }

    
    public GroupCollection getGroups(String tenant) {
        return new GroupCollection(cloudOfThingsRestClient, "user/" + tenant + "/groups/", gson, null);
    }

    public Group createGroup(Group group, String tenant) {
        String json = "{\"name\":\"" + group.getName() + "\"}";
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/groups", CONTENT_TYPE_GROUP);
        group.setId(id);

        return group;
    }

    public void deleteGroup(Group group, String tenant) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete(groupId, "user/" + tenant + "/groups");
    }

    public User getUserByName(String userName, String tenant) {
        String result = cloudOfThingsRestClient.getResponse("user/" + tenant + "/users/" + userName);
        return new User(gson.fromJson(result, ExtensibleObject.class));
    }

    public Group getGroupByName(String tenant, String groupName) {
        String result = cloudOfThingsRestClient.getResponse("user/" + tenant + "/groupByName/" + groupName);
        return new Group(gson.fromJson(result, ExtensibleObject.class));
    }

    public Group getGroupById(Long id, String tenant) {
        String groupId = Long.toString(id);
        String result = cloudOfThingsRestClient.getResponse(groupId, "user/" + tenant + "/groups", CONTENT_TYPE_GROUP);
        return new Group(gson.fromJson(result, ExtensibleObject.class));
    }

    // Operations on Current User:

    /**
     * Method to retrieve the currently logged in user.
     * 
     * @return an instance of the currently logged in user.
     */
    public CurrentUser getCurrentUser() {
        String result = cloudOfThingsRestClient.getResponse("user/currentUser");
        return new CurrentUser(gson.fromJson(result, ExtensibleObject.class));
    }

    // Operations on a generic User:
    /**
     * A method to create a user in the cloud from a pre-defined user object and
     * return it.
     * 
     * @param user the user to create/persist in the cloud
     * @param tenant a String with tenant name
     * @return the same input user object
     */
    public User createUser(User user, String tenant) {
        String json = gson.toJson(user);
        // post request:
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/users", CONTENT_TYPE_USER);
        user.setId(id);
        return user;
    }


    /**
     * A method to create a user in the cloud and return it by providing a
     * userName, password, firstName, lastName and a tenant.
     * 
     * @param userName of the user.
     * @param tenant of the tenant where the user resides.
     * @param firstName of the user.
     * @param lastName of the user.
     * @param password of the user.
     * @return an instance of the user.
     */
    public User createUser(String userName, String tenant, String firstName, String lastName, String password) {
        User user = new User();
        user.setUserName(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        String json = gson.toJson(user);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/users", CONTENT_TYPE_USER);
        user.setId(id);
        return user;
    }

    /**
     * The method to delete a specific user from the cloud.
     * 
     * @param user a user object of the user which should be deleted.
     * @param tenant a String with the name of the tenant.
     */
    public void deleteUser(User user, String tenant) {
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/users");
    }

    /**
     * The method to delete a specific user from the cloud by providing the
     * username.
     * 
     * @param userName a name of the user which should be deleted.
     * @param tenant a String with the name of the tenant.
     */
    public void deleteUserByUserName(String userName, String tenant) {
        User user = getUserByName(userName, tenant);
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/users");
    }

    // Methods related to groups:

    /**
     * The method to add a specific user to a group.
     * 
     * @param user a user object of the user which should be added to the group.
     * @param tenant a String with the name of the tenant.
     * @param group a group object the user used to be added to.
     */
    public void addUserToGroup(User user, String tenant, Group group) {
        String json = "{\"user\":{\"self\": \"" + user.getSelf(user, tenant) + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/groups/" + group.getId() + "/users", CONTENT_TYPE_USER_REF);
    }

    public void addCurrentUserToGroup(CurrentUser user, String tenant, Group group) {
        String json = "{\"user\":{\"self\": \"" + user.getSelf(user, tenant) + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/groups/" + group.getId() + "/users", CONTENT_TYPE_USER_REF);
    }

    public void removeUserFromGroup(User user, String tenant, Group group) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/groups/" + groupId + "/users");
    }

    public void removeCurrentUserFromGroup(CurrentUser user, String tenant, Group group) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete("user/" + tenant + "/groups/" + groupId + "/users/" + user.getUserName() + "/");
    }

    public UserReferenceCollection getUserReferencesOfGroup(String tenant, Group group) {
        String groupId = Long.toString(group.getId());
        String url = "user/" + tenant + "/groups/" + groupId + "/users";
        return new UserReferenceCollection(cloudOfThingsRestClient, url, gson, null);
    }

    public GroupReferenceCollection getGroupReferencesOfUser(String tenant, User user) {
        String url = "user/" + tenant + "/users/" + user.getUserName() + "/groups";
        return new GroupReferenceCollection(cloudOfThingsRestClient, url, gson, null);
    }

    /// Methods related to roles:

    /**
     * Method to retrieve all the available, pre-defined roles in the cloud.
     * 
     * @return an instance of RoleCollection
     */
    public RoleCollection getRoles() {
        return new RoleCollection(cloudOfThingsRestClient, "user/roles", gson, null);
    }

    public Role getRoleByName(String name) {
        String result = cloudOfThingsRestClient.getResponse(name, "user/roles", CONTENT_TYPE_ROLE);
        Role returnedrole = new Role(gson.fromJson(result, ExtensibleObject.class));
        return returnedrole;
    }

    public void assignRoleToUser(User user, Role role, String tenant) {
        String json = "{\"role\":{\"self\": \"user/roles/" + role.getId() + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/users/" + user.getId() + "/roles", CONTENT_TYPE_ROLE_REF);
    }

    public void unassignRoleFromUser(User user, Role role, String tenant) {
        cloudOfThingsRestClient.delete(role.getName(), "user/" + tenant + "/users/" + user.getUserName() + "/roles");
    }

    /**
     * The method to retrieve a collection of references (URLs) of roles that a
     * user is assigned to.
     * 
     * @param user a user object of the user whose roles references will be retrieved.
     * @param tenant a String with the name of the tenant.
     * @return a collection of role references of a user.
     */
    public RoleReferenceCollection getRolesReferencesOfUser(User user, String tenant) {
        String url = "user/" + tenant + "/users/" + user.getUserName() + "/roles";
        return new RoleReferenceCollection(cloudOfThingsRestClient, url, gson, null);
    }

    public void assignRoleToGroup(Group group, Role role, String tenant) {
        String groupId = Long.toString(group.getId());
        String json = "{\"role\":{\"self\": \"user/roles/" + role.getId() + "\" }}";
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/groups/" + groupId + "/roles", CONTENT_TYPE_ROLE_REF);
    }

    /**
     * The groups can have more than one role. This method returns the
     * collection of references of roles that the group is assigned to.
     * 
     * @param group the group object of the group whose roles references will be retrieved.
     * @param tenant a String with the name of the tenant.
     * @return collection of roles that the group is assigned to.
     */
    public RoleReferenceCollection getRolesReferencesOfGroup(Group group, String tenant) {
        String groupId = Long.toString(group.getId());
        String url = "user/" + tenant + "/groups/" + groupId + "/roles";
        return new RoleReferenceCollection(cloudOfThingsRestClient, url, gson, null);
    }

    public void unassignRoleFromGroup(Group group, Role role, String tenant) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete(role.getName(), "user/" + tenant + "/groups/" + groupId + "/roles");
    }

    /**
     * The method to update the fields of a user in the cloud.
     * 
     * @param user the user object with the new values to be stored.
     *             Id, username, password cannot be updated and will be ignored.
     * @param tenant a String with the name of the tenant.
     */
    public void updateUser(User user, String tenant) {
        Map<String, Object> attributes = user.getAttributes();
        attributes.remove("id");
        attributes.remove("userName");
        attributes.remove("password"); // Cannot be changed in this way for regular users!

        ExtensibleObject extensibleObject = new ExtensibleObject();
        extensibleObject.setAttributes(attributes);

        String json = gson.toJson(extensibleObject);
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT_TYPE_USER);
    }

    public void updateGroup(Group group, String tenant) {
        Map<String, Object> attributes = group.getAttributes();
        attributes.remove("self");
        attributes.remove("roles");
        attributes.remove("id");

        ExtensibleObject extensibleObject = new ExtensibleObject();
        extensibleObject.setAttributes(attributes);

        String json = gson.toJson(extensibleObject);
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/groups/" + groupId, CONTENT_TYPE_GROUP);
    }
}
