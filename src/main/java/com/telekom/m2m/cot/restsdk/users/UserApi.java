package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * @author ozanarslan
 *
 */
public class UserApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.user+json; charset=UTF-8; ver=0.9";

    private final Gson gson = GsonUtils.createGson();

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

    public Group createGroup(Group group, String tenant) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.group+json; charset=UTF-8; ver=0.9";
        String json = "{\"name\":\"" + group.getName() + "\"}";

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenant + "/groups", CONTENT);
        group.setId(id);

        return group;
    }

    public void deleteGroup(Group group, String tenant) {
        String groupId = Long.toString(group.getId());
        cloudOfThingsRestClient.delete(groupId, "user/" + tenant + "/groups");

    }

    /**
     * Method to retrieve a collection of roles.
     * 
     * @return an instance of RoleCollection
     */
    public RoleCollection getRoles() {
        return new RoleCollection(cloudOfThingsRestClient);
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

    public String getCurrentUserFirstName() {
        String firstname = getCurrentUser().getFirstName();
        return firstname;
    }

    public String getCurrentUserLastName() {
        String lastname = getCurrentUser().getLastName();
        return lastname;
    }

    // This method is allowed, currentuser is you, you can get request your own
    // password.
    public String getCurrentUserPassword() {
        String password = getCurrentUser().getPassword();
        return password;
    }

    public void updateCurrentUserFirstName(String firstName) {
        User user = new User();
        user.setFirstName(firstName);
        String json = gson.toJson(user);
        cloudOfThingsRestClient.doPutRequest(json, "user/currentUser", CONTENT_TYPE);
    }

    public void updateCurrentUserLastName(String lastName) {
        User user = new User();
        user.setLastName(lastName);
        String json = gson.toJson(user);
        cloudOfThingsRestClient.doPutRequest(json, "user/currentUser", CONTENT_TYPE);
    }

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

    public void deleteUser(User user, String tenant) {
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/users");
    }

    public void deleteUserByUserName(String userName, String tenant) {
        User user = getUserByName(userName, tenant);
        cloudOfThingsRestClient.delete(user.getId(), "user/" + tenant + "/users");
    }

    public void updateUserFirstName(User user, String tenant, String firstName) {
        String json = "{\"firstName\":\"" + firstName + "\"}";
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT_TYPE);
    }

    public void updateUserLastName(User user, String tenant, String lastName) {
        String json = "{\"lastName\":\"" + lastName + "\"}";
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT_TYPE);
    }

    public void updateUserPassword(User user, String tenant, String password) {
        String json = "{\"password\":\"" + password + "\"}";
        cloudOfThingsRestClient.doPutRequest(json, "user/" + tenant + "/users/" + user.getId(), CONTENT_TYPE);
    }

    public void addUserToGroup(User user, String tenant, Group group) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.userReference+json;ver=0.9";
        String json = "{\"user\":{\"self\": \"" + user.getSelf(user, tenant) + "\" }}";
        System.out.println(json);
        cloudOfThingsRestClient.doPostRequest(json, "user/" + tenant + "/groups/" + group.getId() + "/users", CONTENT);
    }

    public void removeUserFromGroup(User user, String tenant, Group group) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.userReference+json;ver=0.9";
        String groupId = Long.toString(group.getId());

        cloudOfThingsRestClient.delete("user/" + tenant + "/groups/" + groupId + "/users/" + user.getUserName());

    }

    public UserReferenceCollection getUserReferencesOfGroup(String tenant, Group group) {
        String groupId = Long.toString(group.getId());
        String URL = "user/" + tenant + "/groups/" + groupId + "/users";
        return new UserReferenceCollection(cloudOfThingsRestClient, URL, gson, null);

    }

    public GroupReferenceCollection getGroupReferencesOfUser(String tenant, User user) {
        // GET /user/<<tenant>>/users/<<userName>>/groups

        String URL = "user/" + tenant + "/users/" + user.getUserName() + "/groups";
        return new GroupReferenceCollection(cloudOfThingsRestClient, URL, gson, null);

    }

}
