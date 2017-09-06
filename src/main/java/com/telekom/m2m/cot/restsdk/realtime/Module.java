package com.telekom.m2m.cot.restsdk.realtime;
/**
 * TODO: currently the class is barely more than a placeholder.
 * The class that defines the operations on modules. The class represents a module which can be queried, modified and deployed.
 * 
 * Created by Ozan Arslan on 14.08.2017.
 */
import java.util.Date;
import java.util.List;


public class Module {

    private String id;
    private String name;
    private Date lastModified;
    private String status;
    private String fileRepresentation;
    private List<String> statements;


    public void copyFrom(Module module) {
        this.id = module.getId();
        this.name = module.getName();
        this.lastModified = module.getLastModified();
        this.status = module.getStatus();
        this.fileRepresentation = module.getFileRepresentation();
        this.statements = module.getStatements();
    }


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // TODO: maybe we want to make a copy?
    public List<String> getStatements() {
        return statements;
    }

    // TODO: maybe we want to make a copy?
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
