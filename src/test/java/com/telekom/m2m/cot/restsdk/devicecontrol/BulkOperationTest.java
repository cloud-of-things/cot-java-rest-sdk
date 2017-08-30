package com.telekom.m2m.cot.restsdk.devicecontrol;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by Patrick Steinert on 02.01.17.
 */
public class BulkOperationTest {

    @Test
    public void testBulkOperation() {
        // when
        final BulkOperation bulkOperation = new BulkOperation();

        // then
        assertNotNull(bulkOperation);
        assertNotNull(bulkOperation.getAttributes());
        assertEquals(bulkOperation.getAttributes().size(), 0);
    }

}
