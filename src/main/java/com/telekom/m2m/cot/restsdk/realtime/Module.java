package com.telekom.m2m.cot.restsdk.realtime;
/**
 * TODO: currently the class is barely more than a placeholder.
 * The class that defines the operations on modules. The class represents a module which can be queried, modified and deployed.
 * 
 * Created by Ozan Arslan on 14.08.2017.
 */
import java.util.ArrayList;
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

    /**
     * Return a copy of the statements in this module.
     * Statements cannot be changed or added via this list. Use {@link #setStatements(List)} or
     * {@link #addStatement(String)} instead.
      * @return a copy of the statements in this module. List is empty if there are no statements.
     */
    public List<String> getStatements() {
        if (statements != null) {
            return new ArrayList<>(statements);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Set the statements in this module to the statements in a given list. The List itself will not become the
     * statement list of this module. Only the statements will be copied. Existing statements will be discarded.
     * @param statements list of statements. Terminating ';' is optional.
     */
    public void setStatements(List<String> statements) {
        this.statements = new ArrayList<>(statements.size());
        for (String statement : statements) {
            addStatement(statement);
        }
    }

    /**
     * Add another statemnt to the current list of statements.
     * @param statement the new statement. Terminating ';' is optional.
     */
    public void addStatement(String statement) {
        if (statement.endsWith(";")) {
            statements.add(statement.trim());
        } else {
            statements.add(statement.trim()+";");
        }
    }

    public String getFileRepresentation() {
        return fileRepresentation;
    }

    public void setFileRepresentation(String fileRepresentation) {
        this.fileRepresentation = fileRepresentation;
    }

}
