package com.telekom.m2m.cot.restsdk.smartrest;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * A SmartResponseTemplate is used server side to transform a REST response into one line of a SmartREST response.
 *
 * <p>
 * All templates for the given X-Id are matched against the response. Each template, if it does match, can extract
 * data from the JSON, turning it into a CSV line.
 * </p>
 *
 * <p>
 * See SmartRestApiIT for examples.
 * </p>
 */
public class SmartResponseTemplate extends SmartTemplate {

    private String base;
    private String condition;
    private String[] pattern;  // i.e. <VALUE>


    public SmartResponseTemplate() {}

    /**
     * Construct a new SmartResponseTemplate from individual parameters.
     * Parameters should be raw, unescaped.
     *
     * @param msgId the unique (for one X-Id) message-ID of this template
     * @param base the JSONPath base that this template assumes
     * @param condition the JSONPath that needs to match for this template to be evaluated by the server
     * @param pattern the JSONPaths from which information is to be extracted
     */
    public SmartResponseTemplate(String msgId,
                                 String base,
                                 String condition,
                                 String[] pattern) {
        this.msgId = msgId;
        this.base = base;
        this.condition = condition;
        this.pattern = pattern.clone();
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
        return pattern.clone();
    }

    public void setPattern(String[] pattern) {
        this.pattern = pattern.clone();
    }

}
