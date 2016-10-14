package com.telekom.m2m.cot.restsdk.util;

/**
 * Created by Patrick Steinert on 13.10.16.
 */
public class Filter {
    private String source;
    private String type;

    private Filter() {
    }

    public static Criteria filter() {
        return new Criteria();
    }


    public static class Criteria implements IFilter {
        private Filter instance = new Filter();

        public Criteria bySource(String id) {
            instance.source = id;
            return this;
        }

        public String buildFilter() {
            String qs = "";
            if (instance.source != null) {
                qs += "source=" + instance.source;
            }
            if (instance.type != null) {
                qs += "type=" + instance.type;
            }
            return qs;
        }

        public IFilter byType(String type) {
            instance.type = type;
            return this;
        }
    }

}
