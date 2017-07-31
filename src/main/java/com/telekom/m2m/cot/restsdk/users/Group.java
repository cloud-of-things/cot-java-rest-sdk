package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

public class Group extends ExtensibleObject {

	/**
	 * Default construction to create a new group.
	 */
	public Group() {
		super();
	}

	/**
	 * Internal constructor to create groups from base class.
	 *
	 * @param extensibleObject
	 *            object from base class.
	 */
	public Group(ExtensibleObject extensibleObject) {
		super(extensibleObject);
	}

	/**
	 * Get the unique identifier of the user.
	 *
	 * @return String the unique identifier of the user or null if not
	 *         available.
	 */
	public String getId() {
		return (String) anyObject.get("id");
	}

	/**
	 * Set the unique identifier of the group. Just used internally.
	 *
	 * @param id
	 *            the new identifier.
	 */
	void setId(String id) {
		anyObject.put("id", id);
	}

	/**
	 * The method to return the group name.
	 * 
	 * @return name
	 */
	public String getName() {
		return (String) anyObject.get("name");
	}

	/**
	 * The method to set group name.
	 * 
	 * @param name
	 */
	public void setName(String name) {

		anyObject.put("name", name);
	}

	/**
	 * Return the group description
	 * 
	 * @return description
	 */
	public String getDescription() {
		return (String) anyObject.get("description");
	}

	/**
	 * Set the description of the group
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		anyObject.put("description", description);
	}

	/**
	 * Return the users of the group
	 * 
	 * @return
	 */
	public UserReferenceCollection getUsers() {
		return (UserReferenceCollection) anyObject.get("users");
	}

	/**
	 * 
	 * Set the users of the group
	 * 
	 * @param users
	 */
	public void setUsers(UserReferenceCollection users) {
		anyObject.put("users", users);
	}

	/**
	 * 
	 * Return the roles of the group
	 * 
	 * @return roles
	 */
	public RoleReferenceCollection getRoles() {
		return (RoleReferenceCollection) anyObject.get("roles");
	}

	/**
	 * Set the roles for the group
	 * 
	 * @param roles
	 */
	public void setRoles(RoleReferenceCollection roles) {
		anyObject.put("roles", roles);
	}

}
