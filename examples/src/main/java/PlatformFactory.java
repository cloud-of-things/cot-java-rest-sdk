import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.devicecontrol.CotCredentials;

/**
 * Helper class that is used by the examples and contains the boring infrastructure code.
 */
class PlatformFactory {

    /**
     * Reads credentials from environment variables and uses them to create a Cloud of Things platform object.
     *
     * @return The platform object.
     */
    static CloudOfThingsPlatform create() {
        // Ensure that you populate the required environment variables with your credentials.
        String host = System.getenv("COT_REST_CLIENT_HOST");
        if (host == null) {
            throw new RuntimeException("Cloud of Things host missing. Provide it via environment variable 'COT_REST_CLIENT_HOST'.");
        }
        String tenant = System.getenv("COT_REST_CLIENT_TENANT");
        if (tenant == null) {
            throw new RuntimeException("Cloud of Things tenant missing. Provide it via environment variable 'COT_REST_CLIENT_TENANT'.");
        }
        String user = System.getenv("COT_REST_CLIENT_USER");
        if (user == null) {
            throw new RuntimeException("Cloud of Things user missing. Provide it via environment variable 'COT_REST_CLIENT_USER'.");
        }
        String password = System.getenv("COT_REST_CLIENT_PASSWORD");
        if (password == null) {
            throw new RuntimeException("Cloud of Things password missing. Provide it via environment variable 'COT_REST_CLIENT_PASSWORD'.");
        }
        // From the platform we can get the numerous APIs, for example the AlarmApi:
        return new CloudOfThingsPlatform(host, new CotCredentials(tenant, user, password));
    }
}
