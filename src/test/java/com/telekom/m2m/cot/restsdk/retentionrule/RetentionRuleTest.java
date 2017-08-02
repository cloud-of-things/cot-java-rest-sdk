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
    public void testEventSerialization() {

        Gson gson = GsonUtils.createGson();

        RetentionRule rIn = new RetentionRule();
        rIn.setId(12345678L);
        rIn.setEditable(true);
        rIn.setType("xx_yy_zz");
        rIn.setDataType("aa_bb_cc");
        rIn.setFragmentType("11_22_33");
        rIn.setSource("the source");
        rIn.setMaximumAge(365);

        // Just to be sure, bc it's not normal trivial getters/setters:
        assertEquals(12345678L, (long)rIn.getId());
        assertTrue(rIn.isEditable());
        assertEquals("xx_yy_zz", rIn.getType());
        assertEquals("aa_bb_cc", rIn.getDataType());
        assertEquals("11_22_33", rIn.getFragmentType());
        assertEquals("the source", rIn.getSource());
        assertEquals(365, (long)rIn.getMaximumAge());

        String json = gson.toJson(rIn);

        ExtensibleObject o = gson.fromJson(json, ExtensibleObject.class);
        RetentionRule rOut = new RetentionRule(o);
        assertEquals(12345678L, (long)rOut.getId());
        assertTrue(rOut.isEditable());
        assertEquals("xx_yy_zz", rOut.getType());
        assertEquals("aa_bb_cc", rOut.getDataType());
        assertEquals("11_22_33", rOut.getFragmentType());
        assertEquals("the source", rOut.getSource());
        assertEquals(365, (long)rOut.getMaximumAge());


        rIn = new RetentionRule();
        rIn.setEditable(false);
        rIn.setMaximumAge(7);

        json = gson.toJson(rIn);

        o = gson.fromJson(json, ExtensibleObject.class);
        rOut = new RetentionRule(o);
        assertNull(rOut.getId());
        assertFalse(rOut.isEditable());
        assertNull(rOut.getType());
        assertNull(rOut.getDataType());
        assertNull(rOut.getFragmentType());
        assertNull(rOut.getSource());
        assertEquals(7, (long)rOut.getMaximumAge());
    }
}
