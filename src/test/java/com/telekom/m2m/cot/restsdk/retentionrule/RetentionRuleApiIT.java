package com.telekom.m2m.cot.restsdk.retentionrule;


import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.event.EventApi;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class RetentionRuleApiIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private RetentionRuleApi retentionRuleApi = cotPlat.getRetentionRuleApi();


    @Test
    public void testCreateReadUpdateDeleteRule() throws Exception {
        // given
        RetentionRule ruleIn = new RetentionRule();
        String source = "source-" + (Math.random() * Integer.MAX_VALUE);
        ruleIn.setType("*");
        ruleIn.setDataType("EVENT");  // TODO: EVENT, MEASUREMENT are ok, ALARM, AUDIT, OPERATION are not: '"retention/Invalid Data"' HTTP status code:'422'
        ruleIn.setFragmentType("RetentionRuleApiIT");
        ruleIn.setSource(source);
        ruleIn.setMaximumAge(365);

        // when
        RetentionRule r = retentionRuleApi.createRetentionRule(ruleIn);

        // then
        assertNotNull(r.getId(), "Should now have an Id");
        assertTrue(r == ruleIn);

        // when
        RetentionRule ruleOut = retentionRuleApi.getRule(ruleIn.getId());

        // then
        assertEquals(ruleIn.getId(), ruleOut.getId());
        assertEquals(ruleIn.getSource(), ruleOut.getSource());
        assertEquals(ruleIn.getType(), ruleOut.getType());
        assertEquals(ruleIn.getDataType(), ruleOut.getDataType());
        assertEquals(ruleIn.getFragmentType(), ruleOut.getFragmentType());
        assertEquals(ruleIn.getMaximumAge(), ruleOut.getMaximumAge());

        // given
        ruleIn = ruleOut;
        ruleIn.setMaximumAge(31);
        ruleIn.setType("newType");

        // when
        retentionRuleApi.update(ruleIn);

        // then
        ruleOut = retentionRuleApi.getRule(ruleIn.getId());
        assertEquals("newType", ruleOut.getType());
        assertEquals(31, ruleOut.getMaximumAge());
        assertEquals("EVENT", ruleOut.getDataType());
        assertEquals("RetentionRuleApiIT", ruleOut.getFragmentType());

        // when
        retentionRuleApi.deleteRetentionRule(ruleIn);

        // then
        ruleOut = retentionRuleApi.getRule(ruleIn.getId());
        assertNull(ruleOut);
    }


    @Test
    public void testReadRules() {
        // given
        RetentionRule rule = new RetentionRule();
        String source = "source-" + (Math.random() * Integer.MAX_VALUE);
        rule.setType("1");
        rule.setDataType("MEASUREMENT");
        rule.setFragmentType("RetentionRuleApiIT");
        rule.setSource(source);
        rule.setMaximumAge(365);

        long id0 = retentionRuleApi.createRetentionRule(rule).getId();
        long id1 = retentionRuleApi.createRetentionRule(rule).getId();
        long id2 = retentionRuleApi.createRetentionRule(rule).getId();
        rule.setSource("foo");
        long id3 = retentionRuleApi.createRetentionRule(rule).getId(); // This is the one that should be filtered out later. TODO: doesn't work

        // when
        RetentionRuleCollection collection = retentionRuleApi.getRetentionRules();

        // then
        assertTrue(collection.getRetentionRules().length >= 4); // It's >= because we don't know what other data might already be there.

        /*
        // TODO: filter doesn't have any effect for RetentionRules?
        collection = retentionRuleApi.getRetentionRules(Filter.build().bySource(source));
        collection.setPageSize(100);
        RetentionRule[] rules = collection.getRetentionRules();

        assertEquals(rules.length, 3);
        assertEquals((long)rules[0].getId(), id0);
        assertEquals((long)rules[1].getId(), id1);
        assertEquals((long)rules[2].getId(), id2);

        // TODO: deleting collectins of RetentionRules doesn't work either and would be dangerous, without filters.
        retentionRuleApi.deleteRetentionRules(Filter.build().bySource(source));

        collection = retentionRuleApi.getRetentionRules(Filter.build().bySource(source));
        rules = collection.getRetentionRules();
        assertEquals(rules.length, 0);
        */

        // cleanup
        retentionRuleApi.deleteRetentionRule(id0);
        retentionRuleApi.deleteRetentionRule(id1);
        retentionRuleApi.deleteRetentionRule(id2);
        retentionRuleApi.deleteRetentionRule(id3);
    }
}
