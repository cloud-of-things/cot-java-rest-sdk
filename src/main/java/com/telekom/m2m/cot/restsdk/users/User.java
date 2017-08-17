package com.telekom.m2m.cot.restsdk.users;

import java.util.regex.Pattern;

import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * Class that defines the methods of user. Created by Ozan Arslan on 13.07.2017
 */
public class User extends ExtensibleObject {
    
   private static final Pattern AllowedChars = Pattern.compile("[a-zA-Z0-9\\._@\\-]{3,}"); 
   private static final int MAX_SIZE=1000;
   private static final int EMAIL_SIZE=254;
  
    public User() {}

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
     * @param user
     * @return URL of the user
     */
    public String getSelf(User user, String tenant) {
        String self = "/user/" + tenant + "/users/" + user.getId();
        return self;
    }

    public String getUserName() {
        return (String) anyObject.get("userName");
    }

    public void setUserName(String InputUserName) {
        if(InputUserName.length()>MAX_SIZE){
        throw new CotSdkException("userName cannot contain more than 1000 characters.");
        }else if(!AllowedChars.matcher(InputUserName).matches()){
            throw new CotSdkException("User name cannot contain whitespace, slashes nor any of (+$:) characters.");
        }else anyObject.put("userName", InputUserName);
    }

    public String getPassword() {
        return (String) anyObject.get("password");
    }

    public void setPassword(String password) {
        if(password.length()>1000){
        throw new CotSdkException("Password cannot contain more than 1000 characters.");
        }else if(!AllowedChars.matcher(password).matches()){
            throw new CotSdkException("Password cannot contain whitespace, slashes nor any of (+$:) characters.");
        }else anyObject.put("password", password);
    }

    public String getFirstName() {
        return (String) anyObject.get("firstName");
    }

    public void setFirstName(String firstName) {
        if(firstName.length()>1000){
        throw new CotSdkException("firstName cannot contain more than 1000 characters.");
        }else if(!AllowedChars.matcher(firstName).matches()){
            throw new CotSdkException("firstName cannot contain whitespace, slashes nor any of (+$:) characters.");
        }else anyObject.put("firstName", firstName);
    }

    public String getLastName() {
        return (String) anyObject.get("lastName");
    }

    public void setLastName(String lastName) {
        if(lastName.length()>1000){
        throw new CotSdkException("lastName cannot contain more than 1000 characters.");
        }else if(!AllowedChars.matcher(lastName).matches()){
            throw new CotSdkException("lastName cannot contain whitespace, slashes nor any of (+$:) characters.");
        }else anyObject.put("lastName", lastName);
    }

    public String getEmail() {
        return (String) anyObject.get("email");
    }

    public void setEmail(String email) {
        if(email.length()>EMAIL_SIZE){
        throw new CotSdkException("Email address cannot contain more than 254 characters.");
        }else if(!AllowedChars.matcher(email).matches()){
            throw new CotSdkException("Email address cannot contain whitespace, slashes nor any of (+$:) characters.");
        }else anyObject.put("email", email);
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
