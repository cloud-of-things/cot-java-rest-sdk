package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.telekom.m2m.cot.restsdk.library.Fragment;
import org.testng.annotations.Test;


public class DeviceManagementLibraryTest {

    @Test
    public void showFragmentsSerialisation() {

        Fragment[] ff = new Fragment[]{
            new IsDevice(),
            new RequiredAvailability(10),
            new Mobile("123456789012345", "23", "0123456789ABCDEF0123"),
            new SupportedOperations("op1", "op2"),
            new Battery(80, "%"),
            new SignalStrength(-30.2f, "dBm", 5.5f, "%"),
            new Hardware("mod", "rev", "SN"),
            new Firmware("name", "vers", "url"),
            new SoftwareList(new SoftwareList.Software("n1", "v1", "u1"),
                             new SoftwareList.Software("n2", "v2", "u2")).
                            addSoftware(new SoftwareList.Software("n3", "v3", "u3")).
                            addSoftware(new SoftwareList.Software("n4", "v4", "u4")),
            new Configuration("myConfig-1\\nmyConfig-2\\n"),
            new CellInfo("gsm", new CellInfo.CellTower("gsm", 49, 10, 20, 30, 2, 20, 12345678L, 1),
                                new CellInfo.CellTower(null, 49, 10, 20, 31, null, null, null, 0)).
                        addCellTower(new CellInfo.CellTower("gsm", 49, 10, 20, 32, 2, 10, 12345678L, 1)).
                        addCellTower(new CellInfo.CellTower(null, 49, 10, 20, 33, null, null, null, null))
        };

        for (Fragment f : ff) {
            System.out.println(f.getJson() + "\n");
        }
    }

}
