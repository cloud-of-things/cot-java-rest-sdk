import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.devicecontrol.CotCredentials;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import java.util.Map;

/**
 * The DeviceCreator lets You create, print and delete devices (which are just ManagedObjects in the CoT).
 */
public class DeviceCreator {

    private static InventoryApi inventoryApi;

    private static void printHelpAndExit() {
        System.out.println("Create a new example device: -c/--create <name>");
        System.out.println("Read and print a device: -r/--read <id>");
        System.out.println("Delete a device: -d/--delete <id>");
        System.exit(0);
    }

    public static void main(String[] args) {
        // From the platform we can get the numerous APIs, for example the InventoryApi:
        CloudOfThingsPlatform platform = new CloudOfThingsPlatform(
            Environment.read("host"),
            new CotCredentials(
                Environment.read("tenant"),
                Environment.read("user"),
                Environment.read("password")
            )
        );
        inventoryApi = platform.getInventoryApi();

        if (args.length == 2) {
            switch (args[0]) {
                case "-c":
                case "--create":
                    createExampleDeviceByName(args[1]);
                    break;
                case "-r":
                case "--read":
                    readAndPrintDeviceById(args[1]);
                    break;
                case "-d":
                case "--delete":
                    inventoryApi.delete(args[1]);
                    break;
                default:
                    printHelpAndExit();
            }
        } else {
            printHelpAndExit();
        }

    }

    private static void readAndPrintDeviceById(String id) {
        ManagedObject device = inventoryApi.get(id);

        if (device == null) {
            System.out.println("A device with id '" + id + "' could not be found.");
        } else {
            StringBuilder builder = new StringBuilder();
            // Not all attributes of a ManagedObject are Strings. The most common other type is
            // the ManagedObjectReferenceCollection, for references to other ManagedObjects.
            for (Map.Entry<String, Object> attribute : device.getAttributes().entrySet()) {
                builder.append("  ").append(attribute.getKey()).append(" = ").append(attribute.getValue()).append("\n");
            }

            String json = GsonUtils.createGson(true).toJson(device);

            // Not all attributes are serialized to JSON:
            System.out.println("The device as ManagedObject:\n" + builder.toString() + "\n" +
                               "The device as JSON:\n" + json);
        }
    }

    private static void createExampleDeviceByName(String name) {
        ManagedObject myDevice = new ManagedObject();

        // A device can have an arbitrary name:
        myDevice.setName(name);

        // This marks the ManagedObject as a device. Without it, it could be anything:
        myDevice.set("c8y_IsDevice", new JsonObject());

        // Some example measurements, that this device is supporting:
        JsonArray measurements = new JsonArray();
        measurements.add("c8y_LightMeasurement");
        measurements.add("c8y_MotionMeasurement");
        myDevice.set("c8y_SupportedMeasurements", measurements);

        inventoryApi.create(myDevice);

        // The ID has been assigned by the server:
        String id = myDevice.getId();

        System.out.println("The new device '" + name + "' has the ID " + id);
    }

}
