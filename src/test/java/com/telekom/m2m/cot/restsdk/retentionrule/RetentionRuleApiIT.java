package com.telekom.m2m.cot.restsdk.retentionrule;


import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.event.EventApi;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
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
import static org.testng.Assert.fail;

public class RetentionRuleApiIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private RetentionRuleApi retentionRuleApi = cotPlat.getRetentionRuleApi();


    @Test
    public void testCreateAllDataTypes() {
        RetentionRule ruleIn = new RetentionRule();
        ruleIn.setMaximumAge(365);

        // The server should accept all these rules without giving us any errors.

        ruleIn.setDataType(RetentionRule.DATA_TYPE_EVENT);
        retentionRuleApi.createRetentionRule(ruleIn);
        retentionRuleApi.deleteRetentionRule(ruleIn);

        ruleIn.setDataType(RetentionRule.DATA_TYPE_MEASUREMENT);
        retentionRuleApi.createRetentionRule(ruleIn);
        retentionRuleApi.deleteRetentionRule(ruleIn);

        ruleIn.setDataType(RetentionRule.DATA_TYPE_ALARM);
        retentionRuleApi.createRetentionRule(ruleIn);
        retentionRuleApi.deleteRetentionRule(ruleIn);

        ruleIn.setDataType(RetentionRule.DATA_TYPE_AUDIT);
        retentionRuleApi.createRetentionRule(ruleIn);
        retentionRuleApi.deleteRetentionRule(ruleIn);

        ruleIn.setDataType(RetentionRule.DATA_TYPE_OPERATION);
        retentionRuleApi.createRetentionRule(ruleIn);
        retentionRuleApi.deleteRetentionRule(ruleIn);

        ruleIn.setDataType(RetentionRule.DATA_TYPE_ALL);
        retentionRuleApi.createRetentionRule(ruleIn);
        retentionRuleApi.deleteRetentionRule(ruleIn);
    }


    @Test
    public void testCreateValidation() {
        RetentionRule ruleIn = new RetentionRule();

        // We try to create a RetentionRule for each validation rule. All should fail.

        ruleIn.setDataType(RetentionRule.DATA_TYPE_ALARM); // Missing maxAge
        try {
            retentionRuleApi.createRetentionRule(ruleIn);
            fail();
        } catch(CotSdkException ex) {
            assertEquals(ex.getHttpStatus(), 0); // Because it comes from our local validation, not the server.
        }

        ruleIn.setMaximumAge(365);

        ruleIn.setDataType("INVALID");
        try {
            retentionRuleApi.createRetentionRule(ruleIn);
            fail();
        } catch(CotSdkException ex) {
            assertEquals(ex.getHttpStatus(), 0); // Because it comes from our local validation, not the server.
        }

        ruleIn.setDataType(RetentionRule.DATA_TYPE_ALARM);
        ruleIn.setFragmentType("not_allowed_for_alarm");
        try {
            retentionRuleApi.createRetentionRule(ruleIn);
            fail();
        } catch(CotSdkException ex) {
            assertEquals(ex.getHttpStatus(), 0); // Because it comes from our local validation, not the server.
        }

        ruleIn.setDataType(RetentionRule.DATA_TYPE_AUDIT);
        ruleIn.setFragmentType("not_allowed_for_AUDIT");
        try {
            retentionRuleApi.createRetentionRule(ruleIn);
            fail();
        } catch(CotSdkException ex) {
            assertEquals(ex.getHttpStatus(), 0); // Because it comes from our local validation, not the server.
        }

        ruleIn.setDataType(RetentionRule.DATA_TYPE_OPERATION);
        ruleIn.setFragmentType("*");
        ruleIn.setType("not_allowed_for_operation");
        try {
            retentionRuleApi.createRetentionRule(ruleIn);
            fail();
        } catch(CotSdkException ex) {
            assertEquals(ex.getHttpStatus(), 0); // Because it comes from our local validation, not the server.
        }
    }


    @Test
    public void testUpdateValidation() {
        RetentionRule ruleIn = new RetentionRule();
        ruleIn.setMaximumAge(365);

        // We try one invalid update. No need to duplicate all the cases in testCreateValidation.

        ruleIn.setDataType(RetentionRule.DATA_TYPE_ALARM);

        retentionRuleApi.createRetentionRule(ruleIn);

        ruleIn.setFragmentType("not_allowed_for_alarm");

        try {
            retentionRuleApi.update(ruleIn);
            fail();
        } catch(CotSdkException ex) {
            assertEquals(ex.getHttpStatus(), 0); // Because it comes from our local validation, not the server.
        }

        retentionRuleApi.deleteRetentionRule(ruleIn);
    }


    @Test
    public void testCreateReadUpdateDeleteRule() throws Exception {
        // given
        RetentionRule ruleIn = new RetentionRule();
        String source = "source-" + (Math.random() * Integer.MAX_VALUE);
        ruleIn.setType("RetentionRuleApiIT-type");
        ruleIn.setDataType("EVENT");
        ruleIn.setFragmentType("RetentionRuleApiIT-fragmentType");
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
        assertEquals("RetentionRuleApiIT-fragmentType", ruleOut.getFragmentType());

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


        // cleanup
        retentionRuleApi.deleteRetentionRule(id0);
        retentionRuleApi.deleteRetentionRule(id1);
        retentionRuleApi.deleteRetentionRule(id2);
        retentionRuleApi.deleteRetentionRule(id3);
    }
}
