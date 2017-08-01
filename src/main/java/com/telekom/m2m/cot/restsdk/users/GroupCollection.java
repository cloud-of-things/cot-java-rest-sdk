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
 * Class that defines the methods of group collection. Created by Ozan Arslan on
 * 13.07.2017
 */

public class GroupCollection {
	private Filter.FilterBuilder criteria = null;
	private CloudOfThingsRestClient cloudOfThingsRestClient;
	private int pageCursor = 1;

	private Gson gson = GsonUtils.createGson();

	private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.groupCollection+json;ver=0.9";
	private boolean nextAvailable = false;
	private boolean previousAvailable = false;
	private int pageSize = 5;
	private String tanent;

	/**
	 * Creates a GroupCollection. Use {@link GroupApi} to get GroupCollections.
	 *
	 * @param cloudOfThingsRestClient
	 *            the necessary REST client to send requests to the CoT.
	 */
	GroupCollection(CloudOfThingsRestClient cloudOfThingsRestClient, String tanent) {
		this.cloudOfThingsRestClient = cloudOfThingsRestClient;
		this.tanent = tanent;
	}

	/**
	 * Creates a GroupCollection with filters. Use {@link GroupApi} to get
	 * GroupCollections.
	 *
	 * @param filterBuilder
	 *            the build criteria.
	 * @param cloudOfThingsRestClient
	 *            the necessary REST client to send requests to the CoT.
	 */
	GroupCollection(Filter.FilterBuilder filterBuilder, CloudOfThingsRestClient cloudOfThingsRestClient,
			String tanent) {
		this.criteria = filterBuilder;
		this.cloudOfThingsRestClient = cloudOfThingsRestClient;
		this.tanent = tanent;
	}

	public Group[] getGroups() {
		JsonObject object = getJsonObject(pageCursor, tanent);

		previousAvailable = object.has("prev");

		if (object.has("groups")) {
			JsonArray jsonGroups = object.get("groups").getAsJsonArray();
			Group[] arrayOfGroups = new Group[jsonGroups.size()];
			for (int i = 0; i < jsonGroups.size(); i++) {
				JsonElement jsonGroup = jsonGroups.get(i).getAsJsonObject();
				Group group = new Group(gson.fromJson(jsonGroup, ExtensibleObject.class));
				arrayOfGroups[i] = group;
			}
			return arrayOfGroups;
		} else
			return null;
	}

	private JsonObject getJsonObject(int page, String tanent) {
		String response;
		String url = "user/" + tanent + "/groups?" + "currentPage=" + page + "&pageSize=" + pageSize;
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
	 * @return true if next page has groups, otherwise false.
	 * @since 0.2.0
	 */
	public boolean hasNext() {
		JsonObject object = getJsonObject(pageCursor + 1, tanent);
		if (object.has("groups")) {
			JsonArray jsonGroups = object.get("groups").getAsJsonArray();
			nextAvailable = jsonGroups.size() > 0 ? true : false;
		}
		return nextAvailable;
	}

	/**
	 * Checks if there is a previous page.
	 *
	 * @return true if next page has groups, otherwise false.
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
