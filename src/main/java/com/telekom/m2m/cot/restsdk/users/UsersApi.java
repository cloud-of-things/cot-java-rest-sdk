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

	public UserCollection getUsers(String tenant) {

		return new UserCollection(cloudOfThingsRestClient, tenant);
	}

	// TODO:
	public void setUsers(String users) {
	}

	public GroupCollection getGroups(String tanent) {
		return new GroupCollection(cloudOfThingsRestClient, tanent);
	}

	public RoleCollection getRoles() {
		return new RoleCollection(cloudOfThingsRestClient);
	}

	public User getUserByName(String userName, String tanent)

	{

		String result = cloudOfThingsRestClient.getResponse("user/" + tanent + "/users/" + userName, CONTENT_TYPE);

		User user = new User(gson.fromJson(result, ExtensibleObject.class));

		return user;

	}

	public Group getGroupByName(String tenant, String groupName) {
		String CONTENT = "application/vnd.com.nsn.cumulocity.group+json;ver=0.9";
		String result = cloudOfThingsRestClient.getResponse("user/" + tenant + "/groupByName/" + groupName, CONTENT);
		Group group = new Group(gson.fromJson(result, ExtensibleObject.class));
		return group;
	}

	public CurrentUser getCurrentUser() {
		String result = cloudOfThingsRestClient.getResponse("user/currentUser", CONTENT_TYPE);
		CurrentUser currentuser = new CurrentUser(gson.fromJson(result, ExtensibleObject.class));
		return currentuser;
	}

	// TODO: setRoles

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
