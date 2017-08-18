package com.telekom.m2m.cot.restsdk.realtime;
/**
 * TODO: currently the class is barely more than a placeholder.
 * The class that defines the operations on modules. The class represents a module which can be querried, modified and deployed.
 * 
 * Created by Ozan Arslan on 14.08.2017.
 *
 */

import java.util.Date;
import java.util.List;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

public class Module extends ExtensibleObject {

    /**
     * Default construction to create a new module.
     */
    public Module() {
        super();
    }

    /**
     * Internal constructor to create module from base class.
     *
     * @param extensibleObject
     *            object from base class.
     */
    public Module(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    private String id;
    private String name;
    private Date lastModified;
    private String status;
    private String fileRepresentation;
    private List<String> statements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModifiedDateTime() {
        return lastModified;
    }

    public void setLastModifiedDateTime(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getStatements() {
        return statements;
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public String getFileRepresentation() {
        return fileRepresentation;
    }

    public void setFileRepresentation(String fileRepresentation) {
        this.fileRepresentation = fileRepresentation;
    }
}
