package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;


/**
 * Class to retrieve and define sets of users (collection of users.)
 * 
 * Created by Ozan Arslan on 31.07.2017
 */
public class UserCollection {

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.userCollection+json;ver=0.9";
    private static final int  defaultPageSize=5;
    
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;
    
    private final Gson gson = GsonUtils.createGson();

    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = defaultPageSize;
    private final String tenant;


    /**
     * Creates a UserCollection.
     *
     * @param cloudOfThingsRestClient
     *            the necessary REST client to send requests to the CoT.
     */
    UserCollection(CloudOfThingsRestClient cloudOfThingsRestClient, String tenant) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.tenant = tenant;
    }



    /**
     * The method to return the users in a collection as an array
     * 
     * @return the array of users that belong to a collection.
     */
    public User[] getUsers() {
        JsonObject object = getJsonObject(pageCursor, tenant);

        previousAvailable = object.has("prev");

        if (object.has("users")) {
            JsonArray jsonUsers = object.get("users").getAsJsonArray();
            User[] arrayOfUsers = new User[jsonUsers.size()];
            for (int i = 0; i < jsonUsers.size(); i++) {
                JsonElement jsonUser = jsonUsers.get(i).getAsJsonObject();
                User user = new User(gson.fromJson(jsonUser, ExtensibleObject.class));
                arrayOfUsers[i] = user;
            }
            return arrayOfUsers;
        } else {
            return new User[0];
        }
    }


    private JsonObject getJsonObject(int page, String tenant) {
        String response;
        String url = "user/" + tenant + "/users?" + "currentPage=" + page + "&pageSize=" + pageSize;
        response = cloudOfThingsRestClient.getResponse(url);

        return gson.fromJson(response, JsonObject.class);
    }

    /**
     * Moves cursor to the next page.
     */
    public void next() {
        pageCursor += 1;
    }

    /**
     * Moves cursor to the previous page.
     */
    public void previous() {
        pageCursor -= 1;
    }

    /**
     * Checks if the next page has elements. <b>Use with caution, it does a
     * separate HTTP request, so it is considered as slow</b>
     *
     * @return true if next page has events, otherwise false.
     */
    public boolean hasNext() {
        JsonObject object = getJsonObject(pageCursor + 1, tenant);
        if (object.has("users")) {
            JsonArray jsonEvents = object.get("users").getAsJsonArray();
            nextAvailable = jsonEvents.size() > 0;
        }
        return nextAvailable;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if next page has events, otherwise false.
     * @since 0.2.0
     */
    public boolean hasPrevious() {
        return previousAvailable;
    }

    /**
     * Sets the page size for page queries. The queries uses page size as a
     * limit of elements to retrieve. There is a maximum number of elements,
     * currently 2,000 elements. <i>Default is 5</i>
     *
     * @param pageSize
     *            the new page size as positive integer.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(pageSize, 0);
    }

}
