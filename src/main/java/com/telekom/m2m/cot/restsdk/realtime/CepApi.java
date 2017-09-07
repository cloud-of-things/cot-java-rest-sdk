package com.telekom.m2m.cot.restsdk.realtime;

import java.util.Arrays;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * The class that defines the CepApi. CEP stands for Complex-Event-Processing.
 * CepApi returns a URL to a collection of modules.
 *
 * Created by Ozan Arslan on 14.08.2017. TODO: we might want to rename this, to
 * avoid confusion with the CoT-entity "CepApi".
 */
public class CepApi {

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    private final Gson gson = GsonUtils.createGson();


    public CepApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    /**
     * Returns the connector that establishes the real time communication with the notification service.
     * 
     * @return CepConnector
     */
    public CepConnector getCepConnector() {
        return new CepConnector(cloudOfThingsRestClient);
    }


    /**
     * [Prepare to] load all Modules (json only, no statements) from the server.
     * @return the ModuleCollection
     */
    public ModuleCollection getModules() {
        return new ModuleCollection(cloudOfThingsRestClient, "cep/modules", gson, null);

    }


    /**
     * Create (i.e. persist) a new Module on the server.
     * @param module the new Module. Will receive an ID from the server.
     * @return the same Module instance
     */
    public Module createModule(Module module) {
        String data = "module " + module.getName() + ";" + String.join("\n", module.getStatements());

        String response = cloudOfThingsRestClient.doFormUpload(data, "file", "cep/modules", "text/plain");

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
        String responseJson = cloudOfThingsRestClient.getResponse(id, "cep/modules", "application/vnd.com.nsn.cumulocity.cepModule+json");
        Module module = gson.fromJson(responseJson, Module.class);

        String statementsFile = cloudOfThingsRestClient.getResponse(id, "cep/modules", "text/plain");
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
     * TODO: maybe we should only update the dirty parts?
     * @param module the Module that shall be updated on the server.
     *               The parameter instance will be updated with the response from the server (esp. lastModified).
     */
    public void updateModule(Module module) {
        String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.cepModule+json";

        String json = "{\"name\" : \"" + module.getName() + "\", \"status\" : \"" + module.getStatus().name() + "\"}";

        // the status can only be updated if it has changed from the default:
        // DEPLOYED. TODO: investigate why this is the case.
        if (module.getStatus().equals(Module.Status.DEPLOYED)) {
            json = "{\"name\" : \"" + module.getName() + "\"}";
        }

        cloudOfThingsRestClient.doPutRequest(json, "cep/modules/" + module.getId(), CONTENT_TYPE);

        String data = "module " + module.getName() + ";" + String.join("\n", module.getStatements());
        // The update doesn't need to be multipart/form-data.
        // And: the ID of the module doesn't seem to change. It is updated in place.
        String responseJson = cloudOfThingsRestClient.doPutRequest(data, "cep/modules/" + module.getId(), "text/plain", CONTENT_TYPE);
        module.copyFrom(gson.fromJson(responseJson, Module.class));
    }


    /**
     * Delete a Module by its ID.
     * @param id the ID of the Module that should be deleted.
     */
    public void deleteModule(String id) {
        // TODO: we should check the return type (success: 204 NO CONTENT)
        cloudOfThingsRestClient.delete(id, "cep/modules");
    }


}
