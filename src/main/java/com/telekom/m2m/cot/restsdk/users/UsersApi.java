package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

public class UsersApi {
	private final CloudOfThingsRestClient cloudOfThingsRestClient;
	private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.user+json;ver=0.9";
	private final Gson gson = GsonUtils.createGson();

	public UsersApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
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
		return new GroupCollection(
				cloudOfThingsRestClient,
				"user/" + tenant + "/groups/",
				gson,
				null);
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
	public User getUserByName(String userName, String tenant)

	{

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
	 * Method to return the currently logged in user.
	 * 
	 * @return an instance of the currently logged in user.
	 */
	public CurrentUser getCurrentUser() {
		String result = cloudOfThingsRestClient.getResponse("user/currentUser", CONTENT_TYPE);
		CurrentUser currentuser = new CurrentUser(gson.fromJson(result, ExtensibleObject.class));
		return currentuser;
	}

	// TODO: setRoles

	/**
	 * A method to create a user WORK IN PROGRESS
	 * 
	 * @param user
	 * @param tenantId
	 * @return an instance of a User
	 */
	public User createUser(User user, String tenantId) {
		String json = gson.toJson(user);
		// post requrest:
		String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "user/" + tenantId + "/users/", CONTENT_TYPE);
		user.setId(id);

		return user;
	}

	/*
	 * public User getUser(String URL, String userName, String tanentId)
	 * 
	 * {
	 * 
	 * String result; result = cloudOfThingsRestClient.getResponse("user/" +
	 * tanentId + "/users/" + userName, CONTENT_TYPE); User user =
	 * gson.fromJson(result, User.class);
	 * 
	 * return user;
	 * 
	 * }
	 */

}
