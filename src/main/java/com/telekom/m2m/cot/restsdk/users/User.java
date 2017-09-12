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
   private static final int MAX_USERNAME_SIZE = 1000;
   private static final int EMAIL_SIZE = 254;
   private static final int MAX_PASSWORD_SIZE = 32;
   private static final int MIN_PASSWORD_SIZE = 6;
   
  
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
     * @param id
     *            the new identifier.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * Method to retrieve the URL of a user
     * 
     * @return URL of the user
     */
    public String getSelf(User user, String tenant) {
        return "/user/" + tenant + "/users/" + user.getId();
    }

    public String getUserName() {
        return (String) anyObject.get("userName");
    }

    public void setUserName(String inputUserName) {
        if (inputUserName.length() > MAX_USERNAME_SIZE) {
            throw new CotSdkException("UserName cannot contain more than "+MAX_USERNAME_SIZE+" characters.");
        } else if (!USERNAME_RULE.matcher(inputUserName).matches()) {
            throw new CotSdkException("UserName cannot contain whitespace, slashes nor any of (+$:) characters.");
        } else {
            anyObject.put("userName", inputUserName);
        }
    }

    public String getPassword() {
        return (String) anyObject.get("password");
    }

    public void setPassword(String password) {
        if (password.length() > MAX_PASSWORD_SIZE || password.length() < MIN_PASSWORD_SIZE) {
            throw new CotSdkException("Password must contain at least "+MIN_PASSWORD_SIZE+" and at most "+MAX_PASSWORD_SIZE+" characters.");
        } else {
            anyObject.put("password", password);
        }
    }

    public String getFirstName() {
        return (String) anyObject.get("firstName");
    }

    public void setFirstName(String firstName) {
        anyObject.put("firstName", firstName);
    }

    public String getLastName() {
        return (String) anyObject.get("lastName");
    }

    public void setLastName(String lastName) {
        anyObject.put("lastName", lastName);
    }

    public String getEmail() {
        return (String) anyObject.get("email");
    }

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
    public Map<String, List<String>> getDevicePermissions() {
        return (Map<String, List<String>>)anyObject.get("devicePermissions");
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
    public void setDevicePermissions(Map<String, List<String>> devicePermissions) {
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
