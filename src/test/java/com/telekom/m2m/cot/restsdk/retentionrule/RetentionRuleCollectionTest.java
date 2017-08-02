package com.telekom.m2m.cot.restsdk.retentionrule;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.testng.Assert.assertEquals;


public class RetentionRuleCollectionTest {

    private RetentionRuleCollection retentionRuleCollection;

    private CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);

    private String collectionJson = "{\n" +
            "\"statistics\": {\n" +
            "    \"currentPage\": 1,\n" +
            "    \"pageSize\": 5,\n" +
            "    \"totalPages\": 1\n" +
            "},\n" +
            "\"retentionRules\": [\n" +
            "    {\n" +
            "        \"dataType\": \"EVENT\",\n" +
            "        \"fragmentType\": \"fragmentType1\",\n" +
            "        \"id\": 1,\n" +
            "        \"maximumAge\": 12,\n" +
            "        \"self\": \"<<URL of retentionRule>>\",\n" +
            "        \"source\": \"source\",\n" +
            "        \"type\": \"type1\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"dataType\": \"ALARM\",\n" +
            "        \"fragmentType\": \"fragmentType2\",\n" +
            "        \"id\": 2,\n" +
            "        \"maximumAge\": 12,\n" +
            "        \"self\": \"<<URL of retentionRule>>\",\n" +
            "        \"source\": \"source\",\n" +
            "        \"type\": \"type2\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"dataType\": \"*\",\n" +
            "        \"fragmentType\": \"*\",\n" +
            "        \"id\": 5,\n" +
            "        \"maximumAge\": 12,\n" +
            "        \"self\": \"<<URL of retentionRule>>\",\n" +
            "        \"source\": \"*\",\n" +
            "        \"type\": \"*\"\n" +
            "    }\n" +
            "],\n" +
            "\"self\": \"<<URL of current page>>\"\n" +
            "}";


    @BeforeMethod
    public void setup() {
        retentionRuleCollection = new RetentionRuleCollection(
                rc,
                "retention/retentions",
                GsonUtils.createGson(),
                (Filter.FilterBuilder)null);
        retentionRuleCollection.setPageSize(5); // Not important, but the test should not rely on default values.

        Mockito.when(rc.getResponse(eq("retention/retentions?currentPage=1&pageSize=5"), any(String.class))).thenReturn(collectionJson);
    }


    @Test
    public void testGetRetentionRules() {
        RetentionRule[] rules = retentionRuleCollection.getRetentionRules();

        // Did we get enough rules?
        assertEquals(3, rules.length);
        // All of them (i.e. no duplicates or something like that), in the right order order?
        assertEquals("EVENT", rules[0].getDataType());
        assertEquals("fragmentType2", rules[1].getFragmentType());
        assertEquals("*", rules[2].getType());
    }

}
