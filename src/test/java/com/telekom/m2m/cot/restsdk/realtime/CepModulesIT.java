package com.telekom.m2m.cot.restsdk.realtime;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;


public class CepModulesIT {

    private CepApi cepApi = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT + "/" + TestHelper.TEST_USERNAME,
            TestHelper.TEST_PASSWORD).getCepApi();

    private String moduleId;

    @AfterMethod
    public void tearDown() {
        if (moduleId != null) {
            cepApi.deleteModule(moduleId);
            moduleId = null;
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
    public void testCreateAndReadModule() {
        Module myModule = new Module();
        String name = "testmoduleX" + System.currentTimeMillis();
        myModule.setName(name);

        List<String> statements = new ArrayList<>();
        statements.add("@Name(\"s1\") select * from EventCreated.win:time(1 hour)");
        statements.add("@Name(\"s2\") insert into CreateEvent select * from EventCreated e where getObject(e, \"c8y_LocationUpdate\") is not null output first every 60 events");
        myModule.setStatements(statements);

        cepApi.createModule(myModule);

        moduleId = myModule.getId();
        myModule = cepApi.getModule(moduleId);

        assertEquals(myModule.getName(), name);
        assertEquals(myModule.getId(), moduleId);
        assertEquals(myModule.getStatus(), Module.Status.DEPLOYED);
        assertEquals(myModule.getStatements().size(), 2);
        assertEquals(myModule.getStatements().get(0), "@Name(\"s1\") select * from EventCreated.win:time(1 hour);");
        assertEquals(myModule.getStatements().get(1), "@Name(\"s2\") insert into CreateEvent select * from EventCreated e where getObject(e, \"c8y_LocationUpdate\") is not null output first every 60 events;");
    }

    
    @Test
    public void testUpdateModule() throws InterruptedException {
        //given (first create a module:)
        String name = "testModuleX" + System.currentTimeMillis();
        Module myModule = new Module();
        myModule.setName(name);
        List<String> statements = new ArrayList<>();
        statements.add("@Name(\"s1\")\nselect * from EventCreated.win:time(1 hour)");

        myModule.setStatements(statements);
        cepApi.createModule(myModule);
        moduleId = myModule.getId();

        //when (then retrieve it back from the cloud and update its fields):
        myModule = cepApi.getModule(moduleId);
        Date lastModifiedBefore = myModule.getLastModified();
        Thread.sleep(1000); // We have to wait 1 sec because that's the precision of the lastModified time.

        myModule.setName("newTestModuleX"+ System.currentTimeMillis());
        myModule.setStatus(Module.Status.NOT_DEPLOYED);
        statements.clear();
        statements.add("@Name(\"s2\")\nselect * from EventCreated.win:time(2 hour)");
        myModule.setStatements(statements);
        cepApi.updateModule(myModule);
        Date lastModifiedAfter = myModule.getLastModified();
        assertTrue(lastModifiedAfter.getTime() > lastModifiedBefore.getTime());

        //then (now let's return the module from the cloud and check if its fields are correctly updated):

        myModule = cepApi.getModule(moduleId);
        Date lastModifiedLater = myModule.getLastModified();
        System.out.println("later: "+lastModifiedLater);
        assertTrue(myModule.getName().startsWith("newTestModuleX"));
        assertEquals(myModule.getStatus(), Module.Status.NOT_DEPLOYED);
        assertEquals(myModule.getStatements().size(), 1);
        assertEquals(myModule.getStatements().get(0), "@Name(\"s2\")\nselect * from EventCreated.win:time(2 hour);");

    }
    
}
