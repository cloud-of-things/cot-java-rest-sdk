package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Class that defines the methods of user. Created by Ozan Arslan on 13.07.2017
 */

public class User extends ExtensibleObject {

	/**
	 * Default construction to create a new user.
	 */
	public User() {
		super();
	}

	/**
	 * Internal constructor to create users from base class.
	 *
	 * @param extensibleObject
	 *            object from base class.
	 */
	public User(ExtensibleObject extensibleObject) {
		super(extensibleObject);
	}

	/**
	 * Constructor with a userName
	 */
	public User(ExtensibleObject extensibleObject, String Username) {
		super();
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
	 * Set the unique identifier of the user. Just used internally.
	 *
	 * @param id
	 *            the new identifier.
	 */
	void setId(String id) {
		anyObject.put("id", id);
	}

	/**
	 * The method to return the username.
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return (String) anyObject.get("userName");
	}

	/**
	 * The method to set userName.
	 * 
	 * @param InputUserName
	 */
	public void setUserName(String InputUserName) {

		anyObject.put("userName", InputUserName);
	}

	/**
	 * The method to retrieve the password of the user.
	 * 
	 * @return password
	 */
	public String getPassword() {
		return (String) anyObject.get("password");
	}

	/**
	 * The method to set the password of the user.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		anyObject.put("password", password);
	}

	/**
	 * The method to retrieve the first name of the user.
	 * 
	 * @return firstName
	 */
	public String getFirstName() {
		return (String) anyObject.get("firstName");
	}

	/**
	 * The method to set the first name of the user.
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		anyObject.put("firstName", firstName);
	}

	/**
	 * The method to retrieve the last name of the user.
	 * 
	 * @return lastName
	 */
	public String getLastName() {

		return (String) anyObject.get("lastName");
	}

	/**
	 * The method to set the last name of the user.
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		anyObject.put("lastName", lastName);
	}

	/**
	 * The method to retrieve the email address of the user.
	 * 
	 * @return email
	 */
	public String getEmail() {
		return (String) anyObject.get("email");
	}

	/**
	 * The method to set the email address of a user.
	 * 
	 * @param email
	 * 
	 */
	public void setEmail(String email) {
		anyObject.put("email", email);
	}

}