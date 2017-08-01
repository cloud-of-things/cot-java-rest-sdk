package com.telekom.m2m.cot.restsdk.users;

import java.util.List;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * 
 * Currently a place holder. Created by Ozan Arslan on 27.07.2017
 *
 */

public class UserReferenceCollection {

	private Filter.FilterBuilder criteria = null;
	private CloudOfThingsRestClient cloudOfThingsRestClient;
	private int pageCursor = 1;

	private Gson gson = GsonUtils.createGson();

	private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.eventCollection+json;charset=UTF-8;ver=0.9";
	private boolean nextAvailable = false;
	private boolean previousAvailable = false;
	private int pageSize = 5;

	private List<UserReference> references;

	public List<UserReference> getReferences() {
		return references;
	}

	public void setReferences(List<UserReference> references) {
		this.references = references;
	}

}
