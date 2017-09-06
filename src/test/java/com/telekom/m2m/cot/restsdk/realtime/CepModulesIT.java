package com.telekom.m2m.cot.restsdk.realtime;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
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
        Module myModule = new Module();
        String name = "testmoduleX" + System.currentTimeMillis(); // TODO: validate (e.g. no '-' allowed?)
        myModule.setName(name);

        List<String> statements = new ArrayList<>();
        statements.add("@Name(\"s1\") select * from EventCreated.win:time(1 hour)");
        statements.add("@Name(\"s2\") insert into CreatedEvent select * from EventCreated e where getObject(e, \"c8y_LocationUpdate\") is not null output first every 60 events");
        myModule.setStatements(statements);

        cepApi.createModule(myModule);

        moduleId = myModule.getId();
        myModule = cepApi.getModule(moduleId);

        assertEquals(myModule.getName(), name);
        assertEquals(myModule.getId(), moduleId);
        assertEquals(myModule.getStatus(), "DEPLOYED");
        assertEquals(myModule.getStatements().size(), 2);
        assertEquals(myModule.getStatements().get(0), "@Name(\"s1\") select * from EventCreated.win:time(1 hour);");
        assertEquals(myModule.getStatements().get(1), "@Name(\"s2\") insert into CreatedEvent select * from EventCreated e where getObject(e, \"c8y_LocationUpdate\") is not null output first every 60 events;");
    }

    
    
    @Test
    public void testUpdateModule() {
        
        //given (first create a module:)
        String name = "testModuleX" + System.currentTimeMillis(); // TODO: validate (e.g. no '-' allowed?)
        Module myModule = new Module();
        myModule.setName(name);
        List<String> statements = new ArrayList<>();
        statements.add("@Name(\"s1\")\nselect * from EventCreated.win:time(1 hour)\n\n");

        myModule.setStatements(statements);
        cepApi.createModule(myModule);

        moduleId = myModule.getId();
        
        //when (then  retrieve it back from the clould and update its fields):
        myModule = cepApi.getModule(moduleId);
        myModule.setName("newTestModuleX"+ System.currentTimeMillis());
        myModule.setStatus("NOT_DEPLOYED");
        cepApi.updateModule(myModule);
        
        //then (now let's return the module from the cloud and check if its fields are correctly updated):

        myModule= cepApi.getModule(moduleId);
        
        assertTrue(myModule.getName().contains("newTestM"));
        assertEquals(myModule.getStatus(), "NOT_DEPLOYED");
        
    }
    
}
