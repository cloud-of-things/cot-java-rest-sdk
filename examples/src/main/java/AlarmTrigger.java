import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

import java.util.Arrays;
import java.util.Date;

/**
 * The AlarmTrigger sends an Alarm to the Cloud of Things.
 */
public class AlarmTrigger {

    private static void printHelpAndExit() {
        System.out.println("Arguments need to be: <deviceId> <alarmType> <alarmSeverity> <alarmText>");
        System.out.println("The deviceId must be valid. Otherwise you get status 422, 'alarm/Unprocessable Entity'");
        System.out.println("The alarmType is something like com_mycompany_something_specialalarm.");
        System.out.println("The alarmSeverity is WARNING|MINOR|MAJOR|CRITICAL.");
        System.out.println("The alarmText is any user (or device) supplied description. Spaces are allowed.");
        System.out.println("Note that if you createFromEnvironment multiple alarms with the same type and severity they will " +
                           "not be seen as individual alarms, but as one, with an increasing count.");
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            printHelpAndExit();
        }
        CloudOfThingsPlatform platform = PlatformFactory.createFromEnvironment();
        AlarmApi alarmApi = platform.getAlarmApi();

        Alarm alarm = new Alarm();
        ManagedObject source = new ManagedObject(); // This would be the device, that sends the alarm.
        source.setId(args[0]);
        alarm.setSource(source);
        alarm.setType(args[1]);
        alarm.setSeverity(args[2]);
        alarm.setText(String.join(" ", Arrays.copyOfRange(args, 3, args.length)));
        alarm.setTime(new Date());
        alarm.setStatus(Alarm.STATE_ACTIVE);

        alarmApi.create(alarm);

        System.out.println("The alarm has been created with id " + alarm.getId());
    }
}
