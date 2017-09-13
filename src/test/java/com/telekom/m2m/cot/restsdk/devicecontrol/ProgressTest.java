package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Andreas Dyck on 31.08.17.
 *
 * @author Andreas Dyck
 */
public class ProgressTest {

    private final int numberOfDevices = 14;
    private final int numberOfExecutingDevices = 2;
    private final int numberOfFailedDevices = 3;
    private final int numberOfPendingDevices = 4;
    private final int numberOfSuccessfulDevices = 5;

    @Test
    public void testProgress() {
        // when
        Progress progress = new Progress();

        // then
        assertNotNull(progress);
        assertEquals(progress.getAttributes().size(), 0);
        assertNull(progress.getNumberOfDevices());
        assertNull(progress.getNumberOfExecutingDevices());
        assertNull(progress.getNumberOfFailedDevices());
        assertNull(progress.getNumberOfPendingDevices());
        assertNull(progress.getNumberOfSuccessfulDevices());
    }

    @Test
    public void testProgressSetterAndGetter() {
        // when
        Progress testProgress = createTestProgress();

        // then
        checkAssertions(testProgress);
    }

    @Test
    public void testProgressWrapper() {
        // given
        Progress testProgress = createTestProgress();

        // when
        Progress wrappedProgress = new Progress(testProgress);

        // then
        assertNotNull(wrappedProgress, "Wrapping into the Progress failed!");
        checkAssertions(wrappedProgress);
    }

    @Test
    public void testProgressSerialization() {
        // given
        Progress testProgress = createTestProgress();

        // when
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testProgress);

        // then
        assertNotNull(json, "Serialization of the Progress failed!");
        assertTrue(json.contains(String.valueOf(numberOfDevices)));
        assertTrue(json.contains(String.valueOf(numberOfExecutingDevices)));
        assertTrue(json.contains(String.valueOf(numberOfFailedDevices)));
        assertTrue(json.contains(String.valueOf(numberOfPendingDevices)));
        assertTrue(json.contains(String.valueOf(numberOfSuccessfulDevices)));
    }

    @Test
    public void testProgressDeserialization() {
        // given
        Progress testProgress = createTestProgress();
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testProgress);

        // when
        ExtensibleObject deserializedProgress = gson.fromJson(json, ExtensibleObject.class);

        // then
        assertNotNull(deserializedProgress, "Deserialization of the Progress failed!");
        Progress progress = new Progress(deserializedProgress);
        checkAssertions(progress);
    }

    private Progress createTestProgress() {
        Progress progress = new Progress();
        progress.setNumberOfDevices(numberOfDevices);
        progress.setNumberOfExecutingDevices(numberOfExecutingDevices);
        progress.setNumberOfFailedDevices(numberOfFailedDevices);
        progress.setNumberOfPendingDevices(numberOfPendingDevices);
        progress.setNumberOfSuccessfulDevices(numberOfSuccessfulDevices);

        return progress;
    }

    private void checkAssertions(Progress testProgress) {
        assertEquals(testProgress.getNumberOfDevices().intValue(), numberOfDevices);
        assertEquals(testProgress.getNumberOfExecutingDevices().intValue(), numberOfExecutingDevices);
        assertEquals(testProgress.getNumberOfFailedDevices().intValue(), numberOfFailedDevices);
        assertEquals(testProgress.getNumberOfPendingDevices().intValue(), numberOfPendingDevices);
        assertEquals(testProgress.getNumberOfSuccessfulDevices().intValue(), numberOfSuccessfulDevices);
    }

}
