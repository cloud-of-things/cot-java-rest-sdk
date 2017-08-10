package com.telekom.m2m.cot.restsdk.smartrest;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class SmartRequestTemplate extends SmartTemplate {

    private String accept;
    private String contentType;
    private String method;
    private String placeholder;
    private String resourceUri;
    private String templateString;
    private String[] values; // i.e. <PARAMS>


    public SmartRequestTemplate() {}

    public SmartRequestTemplate(String msgId,
                                String method,
                                String resourceUri,
                                String accept,
                                String contentType,
                                String placeholder,
                                String[] values,
                                String templateString) {
        this.msgId = msgId;
        this.accept = accept;
        this.contentType = contentType;
        this.method = method;
        this.placeholder = placeholder;
        this.resourceUri = resourceUri;
        this.templateString = templateString;
        this.values = values.clone();
    }

    public SmartRequestTemplate(String csv) {
        // TODO: do we need that? Would it be escaped or unescaped?
        throw new NotImplementedException();
    }


    /**
     * @return this template as a CSV-String, with fields escaped as necessary, ready to be sent to the server.
     */
    public String toString() {
        return String.join(",", msgId, method, resourceUri, escape(contentType), escape(accept), escape(placeholder), (values == null) ? "" : String.join(" ", values), escape(templateString));
    }


    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
    }

    public String[] getValues() {
        return values.clone();
    }

    public void setValues(String[] values) {
        this.values = values.clone();
    }

}
