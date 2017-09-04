package com.telekom.m2m.cot.restsdk.users;

import java.util.List;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Currently, barely more than a place holder.
 * The class that holds the the list of groups and users with permissions for a device
 * 
 * @author ozanarslan, created on 04.09.17
 *
 */
public class DevicePermissionOwners extends ExtensibleObject {
    
  
    public DevicePermissionOwners() {
        super();
    }

    public DevicePermissionOwners(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }
    
    
    public void addGroup(Group group){
        
      //  anyObject.put("id", id);

        
    }
    
 public   void addUser(User user){
        
        
    }
 
    public List<User> getUsers(){
        return null;
        
        
    }
    
    public List<Group> getGroups(){
        return null;
        
        
    }
    
    public void setUsers(List<User> users){
        
        
    }
    
    public void setGroups(List<Group> groups){
        
        
    }
}
