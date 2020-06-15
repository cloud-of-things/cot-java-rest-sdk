package com.telekom.m2m.cot.restsdk.util;

import javax.annotation.Nonnull;

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
    BYTEXT ("text"),
    BYLISTOFIDs ("ids"),
    BYUSER ("user"),
    BYAPPLICATION ("application");

    @Nonnull
    private final String filterKey;

    FilterBy(@Nonnull final String filterKey) {
        this.filterKey = filterKey;
    }

    @Nonnull
    public String getFilterKey() {
        return filterKey;
    }

    @Nonnull
    public static FilterBy getFilterBy(@Nonnull final String filterKey) {

      for(final FilterBy filter : values()) {
          if(filter.getFilterKey().equalsIgnoreCase(filterKey)) {
              return filter;
          }
      }
      throw new IllegalArgumentException(String.format("Couldn't find an enum for requested filter: [%s]", filterKey));
    }

}
