package com.telekom.m2m.cot.restsdk.smartrest;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.stream.Collectors;


public class SmartResponseTemplate extends SmartTemplate {

    private String base;
    private String condition;
    private String[] pattern;  // i.e. <VALUE>


    public SmartResponseTemplate() {}

    public SmartResponseTemplate(String msgId,
                                 String base,
                                 String condition,
                                 String[] pattern) {
        this.msgId = msgId;
        this.base = base;
        this.condition = condition;
        this.pattern = pattern;
    }

    public SmartResponseTemplate(String csv) {
        // TODO: do we need that? Would it be escaped or unescaped?
        throw new NotImplementedException();
    }


    /**
     * @return this template as a CSV-String, with fields escaped as necessary, ready to be sent to the server.
     */
    public String toString() {
        String patterns = Arrays.stream(pattern).map(this::escape).collect(Collectors.joining(", "));
        return String.join(",", msgId, escape(base), escape(condition), patterns);
    }


    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String[] getPattern() {
        return pattern;
    }

    public void setPattern(String[] pattern) {
        this.pattern = pattern;
    }

}
