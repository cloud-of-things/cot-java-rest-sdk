package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Class that defines the methods of a role. Created by Ozan Arslan on
 * 13.07.2017
 */

public class Role extends ExtensibleObject {

	/**
	 * Default construction to create a new user.
	 */
	public Role() {
		super();
	}

	/**
	 * Internal constructor to create roles from base class.
	 *
	 * @param extensibleObject
	 *            object from base class.
	 */
	public Role(ExtensibleObject extensibleObject) {
		super(extensibleObject);
	}

	/**
	 * Get the unique identifier of the role.
	 *
	 * @return String the unique identifier of the user or null if not
	 *         available.
	 */
	public String getId() {
		return (String) anyObject.get("id");
	}

	/**
	 * Set the unique identifier of the user. Just used internally.
	 *
	 * @param id
	 *            the new identifier.
	 */
	void setId(String id) {
		anyObject.put("id", id);
	}

	/**
	 * Give a name to the role
	 * 
	 * @param name
	 */
	public void setName(String name) {
		anyObject.put("name", name);
	}

	/**
	 * Return the name of the role
	 * 
	 * @return name
	 */
	public String getName() {
		return (String) anyObject.get("name");
	}

}
