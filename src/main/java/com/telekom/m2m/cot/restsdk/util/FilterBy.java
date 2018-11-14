package com.telekom.m2m.cot.restsdk.util;

public enum FilterBy {
    /**
     * permitted filters from all apis
     */
    BYSOURCE ("source"),
    BYSTATUS ("status"),
    BYAGENTID ("agentId"),
    BYTYPE ("type"),
    BYDATEFROM ("dateFrom"),
    BYDATETO ("dateTo"),
    BYFRAGMENTTYPE ("fragmentType"),
    BYDEVICEID ("deviceId"),
    BYTEXT ("Text"),
    BYLISTOFIDs ("ids"),
    BYUSER ("User"),
    BYAPPLICATION ("application");

    private String filterKey;

    FilterBy(String filterKey) {
        this.filterKey = filterKey;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public static FilterBy getFilterBy(String filter) {

      for(FilterBy b : values()) {
          if(b.getFilterKey().equalsIgnoreCase(filter)) {
              return b;
          }
      }
      throw new IllegalArgumentException(String.format("Couldn't find an enum for requested filter: [%s]", filter));

    }

}
