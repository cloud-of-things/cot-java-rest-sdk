package com.telekom.m2m.cot.restsdk.realtime;

import java.util.Arrays;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * The class that defines the CepApi. CEP stands for Complex-Event-Processing.
 * CepApi returns a URL to a collection of modules.
 *
 * Created by Ozan Arslan on 14.08.2017.
 */
public class CepApi {

    public static final String NOTIFICATION_PATH = "cep/realtime";

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    private final Gson gson = GsonUtils.createGson();

    private final static String MODULES_API = "cep/modules";
    private final static String CONTENT_TYPE_MODULES = "application/vnd.com.nsn.cumulocity.cepModule+json";

    public CepApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    /**
     * Returns the connector that establishes the real time communication with the notification service.
     * 
     * @return CepConnector
     */
    public CepConnector getCepConnector() {
        return new CepConnector(cloudOfThingsRestClient, NOTIFICATION_PATH);
    }

    public CepConnector getCepStatementConnector() {
        return new CepConnector(cloudOfThingsRestClient, "cep/notifications");
    }


    /**
     * [Prepare to] load all Modules (json only, no statements) from the server.
     * @return the ModuleCollection
     */
    public ModuleCollection getModules() {
        return new ModuleCollection(cloudOfThingsRestClient, MODULES_API, gson, null);

    }


    /**
     * Create (i.e. persist) a new Module on the server.
     * @param module the new Module. Will receive an ID from the server.
     * @return the same Module instance
     */
    public Module createModule(Module module) {
        String data = "module " + module.getName() + ";" + String.join("\n", module.getStatements());

        String response = cloudOfThingsRestClient.doFormUpload(data, "file", MODULES_API, "text/plain");

        Module responseModule = gson.fromJson(response, Module.class);

        module.copyFrom(responseModule);

        return module;
    }


    /**
     * Get an existing Module by ID.
     * This will cause two separate requests because it needs to get the json as well as the statements.
     * @param id the ID of the Module
     * @return the Module
     */
    public Module getModule(String id) {
        String responseJson = cloudOfThingsRestClient.getResponse(id, MODULES_API, CONTENT_TYPE_MODULES);
        Module module = gson.fromJson(responseJson, Module.class);

        String statementsFile = cloudOfThingsRestClient.getResponse(id, MODULES_API, "text/plain");
        String[] statements = statementsFile.split(";");
        // The first line should be the module name, which itself is not a statement:
        if (statements[0].startsWith("module")) {
            statements = Arrays.copyOfRange(statements, 1, statements.length);
        }
        module.setStatements(Arrays.asList(statements));

        return module;
    }


    /**
     * Update a Module.
     * This will cause two separate requests because it needs to update the json as well as the statements.
     * @param module the Module that shall be updated on the server.
     *               The parameter instance will be updated with the response from the server (esp. lastModified).
     */
    public void updateModule(Module module) {
        String json = "{\"name\" : \"" + module.getName() + "\", \"status\" : \"" + module.getStatus().name() + "\"}";

        // the status can only be updated if it has changed from the default:
        if (module.getStatus().equals(Module.Status.DEPLOYED)) {
            json = "{\"name\" : \"" + module.getName() + "\"}";
        }

        cloudOfThingsRestClient.doPutRequest(json, MODULES_API +"/"+ module.getId(), CONTENT_TYPE_MODULES);

        String data = "module " + module.getName() + ";" + String.join("\n", module.getStatements());
        // The update doesn't need to be multipart/form-data.
        // And: the ID of the module doesn't seem to change. It is updated in place.
        String responseJson = cloudOfThingsRestClient.doPutRequest(data, MODULES_API +"/"+ module.getId(), "text/plain", CONTENT_TYPE_MODULES);
        module.copyFrom(gson.fromJson(responseJson, Module.class));
    }


    /**
     * Delete a Module by its ID.
     * @param id the ID of the Module that should be deleted.
     */
    public void deleteModule(String id) {
        cloudOfThingsRestClient.delete(id, MODULES_API);
    }


}
