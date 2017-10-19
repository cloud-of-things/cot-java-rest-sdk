package com.telekom.m2m.cot.restsdk.users;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * Class that defines the methods of user. Created by Ozan Arslan on 13.07.2017
 */
public class User extends ExtensibleObject {
    
   private static final Pattern USERNAME_RULE = Pattern.compile("[^\\s\\\\+\\$/:]+"); 
   public static final int MAX_USERNAME_SIZE = 1000;
   public static final int EMAIL_SIZE = 254;
   public static final int MAX_PASSWORD_SIZE = 32;
   public static final int MIN_PASSWORD_SIZE = 6;
   
  
    public User() {
        super();
    }

    public User(ExtensibleObject extensibleObject) {
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
     * @param id the new identifier.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * Method to retrieve the URL of a user
     *
     * @param user user object with id
     * @param tenant String with tenant name
     *
     * @return URL of the user
     */
    public String getSelf(User user, String tenant) {
        return "/user/" + tenant + "/users/" + user.getId();
    }

    /**
     * Returns the userName.
     *
     * @return the userName as String.
     */
    public String getUserName() {
        return (String) anyObject.get("userName");
    }

    /**
     * Set the userName.
     *
     * There is some validation:
     * <ul>
     *  <li>of maximum length MAX_USERNAME_SIZE</li>
     *  <li>absence of whitespace, slashes nor any of (+$:) characters</li>
     * </ul>
     *
     * @param inputUserName the userName.
     */
    public void setUserName(String inputUserName) {
        if (inputUserName.length() > MAX_USERNAME_SIZE) {
            throw new CotSdkException("UserName cannot contain more than "+MAX_USERNAME_SIZE+" characters.");
        } else if (!USERNAME_RULE.matcher(inputUserName).matches()) {
            throw new CotSdkException("UserName cannot contain whitespace, slashes nor any of (+$:) characters.");
        } else {
            anyObject.put("userName", inputUserName);
        }
    }

    /**
     * Returns the password of the user.
     *
     * @return the password as String.
     */
    public String getPassword() {
        return (String) anyObject.get("password");
    }

    /**
     * Set the password.
     *
     * There is some validation:
     * <ul>
     *  <li>of minumum length MIN_PASSWORD_SIZE</li>
     *  <li>of maximum length MAX_PASSWORD_SIZE</li>
     *  <li>absence of whitespace, slashes nor any of (+$:) characters</li>
     * </ul>
     * @param password as String.
     */
    public void setPassword(String password) {
        if (password.length() > MAX_PASSWORD_SIZE || password.length() < MIN_PASSWORD_SIZE) {
            throw new CotSdkException("Password must contain at least "+MIN_PASSWORD_SIZE+" and at most "+MAX_PASSWORD_SIZE+" characters.");
        } else {
            anyObject.put("password", password);
        }
    }

    /**
     * Returns the firstName.
     *
     * @return firstName as String.
     */
    public String getFirstName() {
        return (String) anyObject.get("firstName");
    }

    /**
     * Sets the first name.
     *
     * @param firstName as String
     */
    public void setFirstName(String firstName) {
        anyObject.put("firstName", firstName);
    }

    /**
     * Returns the lastName.
     *
     * @return lastName as String.
     */
    public String getLastName() {
        return (String) anyObject.get("lastName");
    }

    /**
     * Sets the lastName.
     *
     * @param lastName as String
     */
    public void setLastName(String lastName) {
        anyObject.put("lastName", lastName);
    }

    /**
     * Returns the email.
     *
     * @return email as String.
     */
    public String getEmail() {
        return (String) anyObject.get("email");
    }

    /**
     * Set the email.
     *
     * There is some validation:
     * <ul>
     *  <li>of maximum length EMAIL_SIZE</li>
     * </ul>
     *
     * @param email as String.
     */
    public void setEmail(String email) {
        if (email.length() > EMAIL_SIZE) {
            throw new CotSdkException("Email address cannot contain more than "+EMAIL_SIZE+" characters.");
        } else {
            anyObject.put("email", email);
        }
    }
    
    /**
     * The method to retrieve the assigned device permissions of a user. The map
     * contains a series of keys of device ids, and the values are a list of
     * permissions of different type.
     * 
     * @return a map of device permissions for this user or null if the user has no permissions
     * TODO: make a copy instead?
     */
    public Map<String, List<DevicePermission>> getDevicePermissions() {
        return (Map<String, List<DevicePermission>>)anyObject.get("devicePermissions");
    }


    /**
     * The method to set device permissions to a user. It allows to set more
     * than one type of device permissions for more than one device at once by
     * employing a map of device ids and a list of permissions.
     * Will overwrite all existing permissions in this User instance.
     * 
     * @param devicePermissions the new permissions for this user.
     * TODO: make a copy instead?
     */
    public void setDevicePermissions(Map<String, List<DevicePermission>> devicePermissions) {
        anyObject.put("devicePermissions", devicePermissions);
    }


    /**
     * A method to get the URL of the user's groups
     * 
     * @return the URL of the groups
     */
    public String getGroupsOfUser() {
        return (String) anyObject.get("groups");
    }

}
