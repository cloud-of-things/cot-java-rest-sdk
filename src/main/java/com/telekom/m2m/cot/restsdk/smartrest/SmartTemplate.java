package com.telekom.m2m.cot.restsdk.smartrest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SmartTemplate {

    private static final Pattern NEED_QUOTES = Pattern.compile("[\",\\t\\n]|^\\s+|\\s+$");


    protected String msgId;


    /**
     * If a value contains double-quotes ("), commas (,), leading or trailing whitespace,
     * line-breaks or tab stops, it must be surrounded by quotes (").
     * Contained double-quotes (") must be escaped by prepending another double-quote ("").
     *
     * Also, null is represented as "".
     *
     * @param value the String that might need escaping
     *
     * @return the escaped String
     */
    public String escape(String value) {
        if (value == null) {
            return "";
        }

        value = value.replace("\"", "\"\"");
        if (NEED_QUOTES.matcher(value).find()) {
            value = "\"" + value + "\"";
        }

        return value;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

}
