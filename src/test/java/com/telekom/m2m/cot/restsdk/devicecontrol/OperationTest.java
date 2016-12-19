package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Patrick Steinert on 20.11.16.
 *
 * @author Patrick Steinert
 */
public class OperationTest {


    @Test
    public void testBasics() {

        Operation operation = new Operation();
        Assert.assertNull(operation.getId());
        Assert.assertNull(operation.getCreationTime());

        ExtensibleObject eo = new ExtensibleObject();
        eo.set("status", "PENDING");

        operation = new Operation(eo);
        Assert.assertEquals(operation.getStatus(), OperationStatus.PENDING);

    }


}
