package com.telekom.m2m.cot.restsdk.realtime;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

/**
 * The class that defines the CepApi. CEP stands for Complex-Event-Processing.
 * CepApi returns a URL to a collection of modules.
 * 
 * Created by Ozan Arslan on 14.08.2017.
 *
 */
public class CepApi {

    
    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    public CepApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }
    
           
    /**
     * Returns the connector that establishes the communications with the notifications service.
     * @return CepConnector
     */
    public CepConnector getCepConnector() {
        return new CepConnector(cloudOfThingsRestClient);
    }
       
    
}
