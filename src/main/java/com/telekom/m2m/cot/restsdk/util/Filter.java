package com.telekom.m2m.cot.restsdk.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Patrick Steinert on 13.10.16.
 */
public class Filter {

    private HashMap<String, String> arguments = new HashMap<String, String>();

    private Filter() {
    }

    public static Criteria filter() {
        return new Criteria();
    }


    public static class Criteria implements IFilter {
        private Filter instance = new Filter();

        /**
         * Adds a filter for source id.
         *
         * @param id ID of the source ({@link com.telekom.m2m.cot.restsdk.inventory.ManagedObject}) to filter for.
         * @return an approprieate filter Object.
         */
        public Criteria bySource(String id) {
//            instance.source = id;
            instance.arguments.put("source", id);
            return this;
        }

        public String buildFilter() {
            String qs = "";
            //boolean ampNeeded = false;
//            if (instance.source != null) {
//                qs += "source=" + instance.source;
//                //ampNeeded = true;
//            }
//            if (instance.type != null) {
//
//                qs += "type=" + instance.type;
//            }
//            if (instance.type != null) {
//                qs += "type=" + instance.type;
//            }
//            if (instance.type != null) {
//                qs += "type=" + instance.type;
//            }
            Set<Map.Entry<String, String>> set = instance.arguments.entrySet();

            for (Map.Entry<String, String> entry : set) {
                qs += entry.getKey() + "=" + entry.getValue() + "&";
            }
            return qs.substring(0, qs.length() - 1);
        }

        /**
         * Adds a filter for type.
         *
         * @param type type to filter for.
         * @return an approprieate filter Object.
         */
        public Criteria byType(String type) {
            //instance.type = type;
            instance.arguments.put("type", type);
            return this;
        }

        /**
         * Adds a filter for a time range.
         *
         * @param from start of the date range (more in the history).
         * @param to   end of the date range (more in the future).
         * @return
         */
        public Criteria byDate(Date from, Date to) {
            //instance.dateFrom = from;
            //instance.dateTo = to;
            instance.arguments.put("dateFrom", CotUtils.convertDateToTimestring(from));
            instance.arguments.put("dateTo", CotUtils.convertDateToTimestring(to));
            return this;
        }

        public Criteria byFragmentType(String fragmentType) {
            instance.arguments.put("fragmentType", fragmentType);
            return this;
        }
    }

}
