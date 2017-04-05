import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

/**
 * Created by Patrick Steinert on 12.03.17.
 */
public class ReadInventory {

    public static void main(String[] args) {
        CloudOfThingsPlatform platform = new CloudOfThingsPlatform("https://management.server.domain.com", "management", "admin", "test1234");

        ManagedObject mo = new ManagedObject();
        mo.setName("mymo");
        mo.set("c8y_IsDevice", new Object());
        ManagedObject mo2 = platform.getInventoryApi().create(mo);
        System.out.println(mo2.getId());


//        EventCollection events = platform.getEventApi().getEvents();
//        Event[] ev = events.getEvents();
//        for(Event e : ev) {
//            System.out.println(e.getId());
//        }
    }
}
