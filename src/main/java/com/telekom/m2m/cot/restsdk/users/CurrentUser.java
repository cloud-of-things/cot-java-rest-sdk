package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Class that defines the methods of current user. Current user is the user that
 * is logged in. Created by Ozan Arslan on 13.07.2017
 */
public class CurrentUser extends ExtensibleObject {


    public CurrentUser() {
        super();
    }

    public CurrentUser(ExtensibleObject extensibleObject) {
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
     * Set the unique identifier of the user. Just used internally.
     *
     * @param id a String with the new identifier.
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
     * @param userName a String with the name of a user.
     */
    public void setUserName(String userName) {

        anyObject.put("userName", userName);
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
     * @param password is a String with a password of the user.
     */
    public void setPassword(String password) {
        anyObject.put("password", password);
    }

    /**
     * The method to retrieve the first name of the user.
     * 
     * @return firstName a String with the first name of the user.
     */
    public String getFirstName() {
        return (String) anyObject.get("firstName");
    }

    /**
     * The method to set the first name of the user.
     * 
     * @param firstName is a String with the first name of the user.
     */
    public void setFirstName(String firstName) {
        anyObject.put("firstName", firstName);
    }

    /**
     * The method to retrieve the last name of the user.
     * 
     * @return lastName a String with the last name of the user.
     */
    public String getLastName() {
        return (String) anyObject.get("lastName");
    }

    /**
     * The method to set the last name of the user.
     * 
     * @param lastName is a String with the last name of the user.
     */
    public void setLastName(String lastName) {
        anyObject.put("lastName", lastName);
    }

    /**
     * The method to retrieve the email address of the user.
     * 
     * @return email a String with the email address of the user.
     */
    public String getEmail() {
        return (String) anyObject.get("email");
    }

    /**
     * The method to set the email address of a user.
     * 
     * @param email is a String with the email address of the user.
     * 
     */
    public void setEmail(String email) {
        anyObject.put("email", email);
    }

    /**
     * The method to return the URL of the current user.
     * 
     * @return the URL of the current user as a String.
     */
    public String getSelf(CurrentUser user, String tenant) {

        return  "/user/" + tenant + "/users/" + user.getId();
    }

}
