package com.telekom.m2m.cot.restsdk.users;

import java.util.ArrayList;
import java.util.List;

/**
 * Currently only a place holder. A class that represents a reference to a group
 * reference collection. Created by Ozan Arslan on 25.07.2017
 *
 */

public class GroupReferenceCollection {

	private List<GroupReference> references;

	public GroupReferenceCollection() {
		references = new ArrayList();
	}

	public List<GroupReference> getReferences() {
		return references;
	}

	public void setReferences(List<GroupReference> references) {
		this.references = references;
	}

}
