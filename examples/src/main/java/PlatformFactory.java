import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.devicecontrol.CotCredentials;

/**
 * Helper class that is used by the examples and contains the boring infrastructure code.
 */
class PlatformFactory {

    /**
     * Reads credentials from environment variables and uses them to createFromEnvironment a Cloud of Things platform object.
     *
     * @return The platform object.
     */
    static CloudOfThingsPlatform createFromEnvironment() {
        return new CloudOfThingsPlatform(
                readFromEnvironment("host"),
                new CotCredentials(
                        readFromEnvironment("tenant"),
                        readFromEnvironment("user"),
                        readFromEnvironment("password")
                )
        );
    }

    /**
     * @param name The name of the environment variable (without prefix, case does not matter).
     * @return The value.
     */
    private static String readFromEnvironment(String name) {
        final String environmentVariableName = "COT_REST_CLIENT_" + name.toUpperCase();
        String value = System.getenv(environmentVariableName);
        if (value == null) {
            throw new RuntimeException("Cloud of Things " + name + " missing. Provide it via environment variable '" + environmentVariableName + "'.");
        }
        return value;
    }
}
