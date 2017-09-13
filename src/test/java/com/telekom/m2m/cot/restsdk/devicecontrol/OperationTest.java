package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Patrick Steinert on 20.11.16.
 *
 * @author Patrick Steinert
 */
public class OperationTest {

    private final String operationId = "234";
    private final OperationStatus status = OperationStatus.PENDING;
    private final String deviceId = "dev123";
    private final String deliveryType = "SMS";

    @Test
    public void testBasics() {
        // when
        Operation operation = new Operation();

        // then
        assertNull(operation.getId());
        assertNull(operation.getCreationTime());
        assertNull(operation.getStatus());
        assertNull(operation.getDeliveryType());

        // given
        ExtensibleObject eo = new ExtensibleObject();
        eo.set("status", "PENDING");

        // when
        operation = new Operation(eo);

        // then
        assertEquals(operation.getStatus(), OperationStatus.PENDING);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidStatus() {
        // given
        Operation operation = new Operation();

        // when
        operation.set("status", 1);

        // then
        assertNull(operation.getStatus());

        // when
        operation.set("status", "a");

        // then
        assertNull(operation.getStatus());

    }

    @Test
    public void testOperationSetterAndGetter() {
        // when
        Operation testOperation = createTestOperation();

        // then
        checkAssertions(testOperation);
    }

    @Test
    public void testOperationWrapper() {
        // given
        Operation testOperation = createTestOperation();

        // when
        Operation wrappedOperation = new Operation(testOperation);

        // then
        assertNotNull(wrappedOperation, "Wrapping into the Operation failed!");
        checkAssertions(wrappedOperation);
    }

    @Test
    public void testOperationSerialization() {
        // given
        Operation testOperation = createTestOperation();

        // when
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testOperation);

        // then
        assertNotNull(json, "Serialization of the Operation failed!");
        assertTrue(json.contains(operationId));
        assertTrue(json.contains(status.toString()));
        assertTrue(json.contains(deviceId));
    }

    @Test
    public void testOperationDeserialization() {
        // given
        Operation testOperation = createTestOperation();
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testOperation);

        // when
        ExtensibleObject deserializedOperation = gson.fromJson(json, ExtensibleObject.class);

        // then
        assertNotNull(deserializedOperation, "Deserialization of the Operation failed!");
        Operation operation = new Operation(deserializedOperation);
        checkAssertions(operation);
    }

    private Operation createTestOperation() {
        Operation operation = new Operation();
        operation.setId(operationId);
        operation.setStatus(status);
        operation.setDeviceId(deviceId);
        operation.setDeliveryType(deliveryType);

        return operation;
    }

    private void checkAssertions(Operation testOperation) {
        assertEquals(testOperation.getId(), operationId);
        assertEquals(testOperation.getStatus(), status);
        assertEquals(testOperation.getDeviceId(), deviceId);
        assertEquals(testOperation.getDeliveryType(), deliveryType);
        assertNull(testOperation.getCreationTime());
    }


}
