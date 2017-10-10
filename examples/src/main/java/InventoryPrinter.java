import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.devicecontrol.CotCredentials;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectCollection;
import com.telekom.m2m.cot.restsdk.util.Filter;


/**
 * The InventoryPrinter queries and prints the Cloud-of-Things inventory for ManagedObjects.
 * If a type is given as the first (and only) command line parameter then only objects of that type will be listed.
 * It might be ran for example like this, from example/target/classes:
 *
 * java -cp .:../../../target/java-rest-client-with-dependencies-0.7.0-SNAPSHOT.jar InventoryPrinter c8y_DeviceGroup
 */
public class InventoryPrinter {

    public static void main(String[] args) {
        String type = "<anyType>";
        if (args.length > 0) {
            type = args[0];
        }

        // Ensure that you populate the required environment variables with your credentials.
        String url = System.getenv("COT_REST_CLIENT_HOST");
        String tenant = System.getenv("COT_REST_CLIENT_TENANT");
        String user = System.getenv("COT_REST_CLIENT_USER");
        String password = System.getenv("COT_REST_CLIENT_PASSWORD");

        // From the platform we can get the numerous APIs, for example the InventoryApi:
        CloudOfThingsPlatform platform = new CloudOfThingsPlatform(url, new CotCredentials(tenant, user, password));
        InventoryApi inventoryApi = platform.getInventoryApi();

        // Get all objects, or objects filtered by type, 32 per page (each page is
        // requested individually, default is JsonArrayPagination.DEFAULT_PAGE_SIZE):
        ManagedObjectCollection managedObjectCollection;
        if (type.equals("<anyType>")) {
            managedObjectCollection = inventoryApi.getManagedObjects(32);
        } else {
            managedObjectCollection = inventoryApi.getManagedObjects(Filter.build().byType(type), 32);
        }

        int count = 0;
        System.out.println("ID: \ttype \tname");

        while (true) {
            // Retrieve the current page of objects:
            ManagedObject[] managedObjects = managedObjectCollection.getManagedObjects();

            for (ManagedObject managedObject : managedObjects) {
                System.out.println(managedObject.getId() + ": \t" + managedObject.getType() + " \t" + managedObject.getName());
                    count += 1;
            }

            // Has this been the last page?
            if (!managedObjectCollection.hasNext()) {
                break;
            }

            // Advance the page cursor to the next page:
            managedObjectCollection.next();
        }

        System.out.println("Found " + count + " ManagedObjects with type '" + type + "' in the Inventory.");
    }

}
