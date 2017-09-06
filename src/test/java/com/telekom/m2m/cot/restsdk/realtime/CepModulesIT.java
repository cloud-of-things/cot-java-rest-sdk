package com.telekom.m2m.cot.restsdk.realtime;


import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class CepModulesIT {

    private CepApi cepApi = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT + "/" + TestHelper.TEST_USERNAME,
            TestHelper.TEST_PASSWORD).getCepApi();

    private String moduleId;

    @AfterMethod
    public void tearDown() {
        if (moduleId != null) {
            cepApi.deleteModule(moduleId);
        }

    }

    @Test
    public void testGetModules() {
        ModuleCollection moduleCollection = cepApi.getModules();
        moduleCollection.setPageSize(1000);
        Module[] modules = moduleCollection.getModules();
        for (Module module : modules) {
            System.out.println("module: " + module.getName() + " (" + module.getId() + ")");
        }
    }

    @Test
    public void testCreateModule() {
        String name = "robmoduleX" + System.currentTimeMillis(); // TODO: validate (e.g. no '-' allowed?)
        Module myModule = new Module();
        myModule.setName(name);
        List<String> statements = new ArrayList<>();
        statements.add("@Name(\"s1\")\nselect * from EventCreated.win:time(1 hour)\n\n");
        //TODO: adding a second line/statement doesn't work like this?
        /* +
                       "@Name(\"s2\")\ninsert into CreatedEvent select * from EventCreated e where getObject(e, \"c8y_LocationUpdate\") is not null output first every 60 events");
                       */
        myModule.setStatements(statements);
        cepApi.createModule(myModule);

        moduleId = myModule.getId();
        myModule = cepApi.getModule(moduleId);

        assertEquals(myModule.getName(), name);
        assertEquals(myModule.getId(), moduleId);
        assertEquals(myModule.getStatus(), "DEPLOYED");
    }

}
