package com.telekom.m2m.cot.restsdk.users;

/**
 * Currently only a place holder. A class that represents a reference to a role reference collection.
 * Created by Ozan Arslan on 25.07.2017
 *
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.svenson.JSONProperty;
//import org.svenson.JSONTypeHint;

public class RoleReferenceCollection {

	private List<RoleReference> references;

	public RoleReferenceCollection() {
		references = new ArrayList();
	}

//	@JSONTypeHint(RoleReference.class)
	public List<RoleReference> getReferences() {
		return references;
	}

	public void setReferences(List<RoleReference> references) {
		this.references = references;
	}

//	@JSONProperty(ignore = true)
	public Iterator<RoleReference> iterator() {
		return references.iterator();
	}
}
