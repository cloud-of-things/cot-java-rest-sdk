package com.telekom.m2m.cot.restsdk.realtime;

import java.util.Map;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * The class that defines the CepApi. CEP stands for Complex-Event-Processing.
 * CepApi returns a URL to a collection of modules.
 * 
 * Created by Ozan Arslan on 14.08.2017.
 * TODO: we might want to rename this, to avoid confusion with the CoT-entity "CepApi".
 */
public class CepApi {

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private final Gson gson = GsonUtils.createGson();

    public CepApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Returns the connector that establishes the communications with the
     * notifications service.
     * 
     * @return CepConnector
     */
    public CepConnector getCepConnector() {
        return new CepConnector(cloudOfThingsRestClient);
    }

    // TODO: to be tested with the new userName.
    public Module createModule() {
        String CONTENT = "multipart/form-data";

        String json = "";
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "cep/modules", CONTENT);

        Module module = getModule(id);
        return module;

    }

    // TODO: to be tested with the new userName.
    public Module getModule(String id) {

        String CONTENT = "application/vnd.com.nsn.cumulocity.cepModule+json;ver=...";
        String result = cloudOfThingsRestClient.getResponse(id, "cep/modules", CONTENT);
        return new Module(gson.fromJson(result, ExtensibleObject.class));

    }

    // TODO: to be tested with the new userName.
    public void updateModule(Module module) {

        String CONTENT = "application/vnd.com.nsn.cumulocity.cepModule+json;ver=...";
        Map<String, Object> attributes = module.getAttributes();
        // TODO check which fiels are not allowed to be updated:
        attributes.remove("exampleFieldToBeRemoved");

        ExtensibleObject extensibleObject = new ExtensibleObject();
        extensibleObject.setAttributes(attributes);

        String json = gson.toJson(extensibleObject);
        cloudOfThingsRestClient.doPutRequest(json, "cep/modules/" + module.getId(), CONTENT);

    }

    public void deleteModule(Module module) {

        cloudOfThingsRestClient.delete(module.getId(), "cep/modules");

    }

    // TODO check if we really need a representation of the CepApi such as
    // below:
    /*
     * public CepApiRepresentation getCepApi() {
     * 
     * String result = cloudOfThingsRestClient.getResponse("cep", null); return
     * new CepApiRepresentation(gson.fromJson(result, ExtensibleObject.class));
     * }
     * 
     * public ModuleCollection getModules() { return new
     * ModuleCollection(cloudOfThingsRestClient, "cep/modules", gson, null);
     * 
     * }
     */

}
