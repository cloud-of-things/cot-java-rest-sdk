import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.realtime.CepApi;
import com.telekom.m2m.cot.restsdk.realtime.CepConnector;
import com.telekom.m2m.cot.restsdk.realtime.Notification;
import com.telekom.m2m.cot.restsdk.realtime.SubscriptionListener;

/**
 * The AlarmWatcher connects to the cloud and listens for real time notifications about alarms from one or more devices.
 * All command line arguments will be used as device IDs. If no IDs are given then all alarms will be watched.
 * IDs, for which no device exists, might cause weird errors from the server.
 * To cause alarms, which will show up here, you can use the example AlarmTrigger.
 */
public class AlarmWatcher {


    public static void main(String[] args) throws InterruptedException {
        // From the platform we can get the numerous APIs, for example the CepApi (ComplexEventProcessing):
        CloudOfThingsPlatform platform = PlatformFactory.createFromEnvironment();
        CepApi cepApi = platform.getCepApi();

        // With the CepApi you can get the connector, which will receive and distribute the notifications:
        CepConnector connector = cepApi.getCepConnector();

        // The connector will run in the background and will pass notifications to SubscriptionListeners:
        ExampleListener listener = new ExampleListener();
        connector.addListener(listener);

        // We only receive notifications on channels to which we have subscribed, so we subscribe to the alarm
        // channels of all our devices, or to all alarms, if no device IDs were given:
        if (args.length == 0) {
            System.out.println("Subscribing to all alarms (/alarms/*).");
            connector.subscribe("/alarms/*");
        } else {
            for (String device : args) {
                System.out.println("Subscribing to alarms from source " + device + ".");
                connector.subscribe("/alarms/" + device);
            }
        }

        // Connect, starting background listener, and then wait.
        connector.connect();
        while (true) {
            Thread.sleep(10);
        }
    }


    private static class ExampleListener implements SubscriptionListener {

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        @Override
        public void onNotification(String channel, Notification notification) {
            System.out.println("New notification on channel " + channel + ":\n");
            System.out.println(gson.toJson(notification.getData()));
        }

        @Override
        public void onError(String channel, Throwable error) {
            System.out.println("There was an error on channel " + channel + ": " + error);
        }
    }

}
