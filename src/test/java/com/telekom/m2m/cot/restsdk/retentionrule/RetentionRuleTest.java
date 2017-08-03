package com.telekom.m2m.cot.restsdk.retentionrule;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class RetentionRuleTest {

    @Test
    public void testRetentionRuleSerialization() {

        Gson gson = GsonUtils.createGson();

        RetentionRule ruleIn = new RetentionRule();
        ruleIn.setId(12345678L);
        ruleIn.setEditable(true);
        ruleIn.setType("xx_yy_zz");
        ruleIn.setDataType("aa_bb_cc");
        ruleIn.setFragmentType("11_22_33");
        ruleIn.setSource("the source");
        ruleIn.setMaximumAge(365);

        // Just to be sure, bc it's not normal trivial getters/setters:
        assertEquals(12345678L, (long)ruleIn.getId());
        assertTrue(ruleIn.isEditable());
        assertEquals("xx_yy_zz", ruleIn.getType());
        assertEquals("aa_bb_cc", ruleIn.getDataType());
        assertEquals("11_22_33", ruleIn.getFragmentType());
        assertEquals("the source", ruleIn.getSource());
        assertEquals(365, (long)ruleIn.getMaximumAge());

        String json = gson.toJson(ruleIn);

        ExtensibleObject eo = gson.fromJson(json, ExtensibleObject.class);
        RetentionRule ruleOut = new RetentionRule(eo);
        assertEquals(12345678L, (long)ruleOut.getId());
        assertTrue(ruleOut.isEditable());
        assertEquals("xx_yy_zz", ruleOut.getType());
        assertEquals("aa_bb_cc", ruleOut.getDataType());
        assertEquals("11_22_33", ruleOut.getFragmentType());
        assertEquals("the source", ruleOut.getSource());
        assertEquals(365, ruleOut.getMaximumAge());


        ruleIn = new RetentionRule();
        ruleIn.setEditable(false);
        ruleIn.setMaximumAge(7);

        json = gson.toJson(ruleIn);

        eo = gson.fromJson(json, ExtensibleObject.class);
        ruleOut = new RetentionRule(eo);
        assertNull(ruleOut.getId());
        assertFalse(ruleOut.isEditable());
        assertNull(ruleOut.getType());
        assertNull(ruleOut.getDataType());
        assertNull(ruleOut.getFragmentType());
        assertNull(ruleOut.getSource());
        assertEquals(7, (long)ruleOut.getMaximumAge());
    }
}
