package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Class to retrieve and define sets of roles.
 * 
 * Created by Ozan Arslan on 31.07.2017
 *
 */

public class RoleCollection {

	private Filter.FilterBuilder criteria = null;
	private CloudOfThingsRestClient cloudOfThingsRestClient;
	private int pageCursor = 1;

	private Gson gson = GsonUtils.createGson();

	private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.roleCollection+json;ver=0.9";
	private boolean nextAvailable = false;
	private boolean previousAvailable = false;
	private int pageSize = 5;
	private String tanent;

	/**
	 * Creates a RoleCollection.
	 *
	 * @param cloudOfThingsRestClient
	 *            the necessary REST client to send requests to the CoT.
	 */
	RoleCollection(CloudOfThingsRestClient cloudOfThingsRestClient) {
		this.cloudOfThingsRestClient = cloudOfThingsRestClient;
	}

	/**
	 * Creates a RoleCollection with filters.
	 *
	 * @param filterBuilder
	 *            the build criteria.
	 * @param cloudOfThingsRestClient
	 *            the necessary REST client to send requests to the CoT.
	 */
	RoleCollection(Filter.FilterBuilder filterBuilder, CloudOfThingsRestClient cloudOfThingsRestClient) {
		this.criteria = filterBuilder;
		this.cloudOfThingsRestClient = cloudOfThingsRestClient;
	}

	/**
	 * A method to return the array of roles.
	 * 
	 * @return arrayOfRoles
	 */
	public Role[] getRoles() {
		JsonObject object = getJsonObject(pageCursor);

		previousAvailable = object.has("prev");

		if (object.has("roles")) {
			JsonArray jsonRoles = object.get("roles").getAsJsonArray();
			Role[] arrayOfRoles = new Role[jsonRoles.size()];
			for (int i = 0; i < jsonRoles.size(); i++) {
				JsonElement jsonRole = jsonRoles.get(i).getAsJsonObject();
				Role role = new Role(gson.fromJson(jsonRole, ExtensibleObject.class));
				arrayOfRoles[i] = role;
			}
			return arrayOfRoles;
		} else
			return null;
	}

	private JsonObject getJsonObject(int page) {
		String response;
		String url = "user/roles?" + "currentPage=" + page + "&pageSize=" + pageSize;
		if (criteria != null) {
			url += "&" + criteria.buildFilter();
		}
		response = cloudOfThingsRestClient.getResponse(url, CONTENT_TYPE);

		return gson.fromJson(response, JsonObject.class);
	}

	/**
	 * Moves cursor to the next page.
	 *
	 * @since 0.2.0
	 */
	public void next() {
		pageCursor += 1;
	}

	/**
	 * Moves cursor to the previous page.
	 *
	 * @since 0.2.0
	 */
	public void previous() {
		pageCursor -= 1;
	}

	/**
	 * Checks if the next page has elements. <b>Use with caution, it does a
	 * seperate HTTP request, so it is considered as slow</b>
	 *
	 * @return true if next page has roles, otherwise false.
	 * @since 0.2.0
	 */
	public boolean hasNext() {
		JsonObject object = getJsonObject(pageCursor + 1);
		if (object.has("roles")) {
			JsonArray jsonRoles = object.get("roles").getAsJsonArray();
			nextAvailable = jsonRoles.size() > 0 ? true : false;
		}
		return nextAvailable;
	}

	/**
	 * Checks if there is a previous page.
	 *
	 * @return true if next page has roles, otherwise false.
	 * @since 0.2.0
	 */
	public boolean hasPrevious() {
		return previousAvailable;
	}

	/**
	 * Sets the page size for page queries. The queries uses page size as a
	 * limit of elements to retrieve. There is a maximum number of elements,
	 * currently 2,000 elements. <i>Default is 5</i>
	 *
	 * @param pageSize
	 *            the new page size as positive integer.
	 */
	public void setPageSize(int pageSize) {
		if (pageSize > 0) {
			this.pageSize = pageSize;
		} else {
			this.pageSize = 0;
		}
	}

}
