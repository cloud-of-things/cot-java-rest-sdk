package com.telekom.m2m.cot.restsdk.library.sensor;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.library.Fragment;

public class SensorLibraryTest {

    @Test
    public void showSensorAndMeasurementIDAndJSON() {
        Fragment[] ff = getFragments();

        for (Fragment f : ff) {
            System.out.println(f.getId() + " -> " + f.getJson());
        }

    }

    
    @Test
    public void testSensorAndMeasurementMethods() {


        AccelerationMeasurement ac= new AccelerationMeasurement(8.36f, "m/s2");
        assertEquals(ac.getAccelerationUnit(), "m/s2");
        assertEquals(ac.getAccelerationValue(), 8.36f);


        CurrentMeasurement cs= new CurrentMeasurement(13.37f, "A");
        assertEquals(cs.getcurrentUnit(), "A");
        assertEquals(cs.getcurrentValue(), 13.37f);

        DistanceMeasurement dm = new DistanceMeasurement(13.37f, "mm");
        assertEquals(dm.getdistanceUnit(), "mm");
        assertEquals(dm.getdistanceValue(),13.37f);

        HumidityMeasurement hm =  new HumidityMeasurement(13.37f, "%RH");
        assertEquals(hm.gethumidityUnit(), "%RH");
        assertEquals(hm.gethumidityValue(),13.37f);

        LightMeasurement lm = new LightMeasurement(8.36f, "lux");
        assertEquals(lm.getlightUnit(), "lux");
        assertEquals(lm.getlightValue(),8.36f);

        MoistureMeasurement mom= new MoistureMeasurement(13.37f, "%");
        assertEquals(mom.getmoistureUnit(),"%");
        assertEquals(mom.getmoistureValue(),13.37f);


        MotionMeasurement mtm=new MotionMeasurement(true, 13.37f, "m/s");
        assertEquals(mtm.getMotionSpeedUnit(),"m/s");
        assertEquals(mtm.getMotionSpeedValue(),13.37f);
        assertEquals( mtm.getDetectedMotionUnit(),"");
        assertEquals(mtm.getDetectedMotionValue(),true);


        SinglePhaseElectricityMeasurement sim = new SinglePhaseElectricityMeasurement(1.1f, "kWh", 1.2f, "kWh", 1.3f,
                "W", 1.4f, "W");
        
        assertEquals(sim.getIncomingEnergyUnit(), "kWh");
        assertEquals(sim.getIncomingEnergyValue(), 1.1f);
        assertEquals(sim.getIncomingPowerUnit(), "W");
        assertEquals(sim.getIncomingPowerValue(), 1.3f);
        assertEquals(sim.getOutgoingEnergyUnit(), "kWh");
        assertEquals(sim.getOutgoingEnergyValue(), 1.2f);
        assertEquals(sim.getOutgoingPowerUnit(), "W");
        assertEquals(sim.getOutgoingPowerValue(), 1.4f);
    
        
        
        
        ThreePhaseElectricityMeasurement thm= new ThreePhaseElectricityMeasurement(
                1.1f, "kWh", 1.2f, "kWh", 1.3f, "kWh", 1.4f, "kWh", 1.5f, "kWh",
                1.6f, "kWh", 1.7f, "kWh", 1.8f, "kWh",
                
                1.9f, "W", 2.0f, "W", 2.1f, "W", 2.2f, "W", 2.3f, "W",
                2.4f, "W", 2.5f, "W", 2.6f, "W", 
                
                2.7f, "kVArh", 2.8f, "kVArh", 2.9f, "kVArh", 3.0f, "kVArh",
                
                3.1f, "kVAr", 3.2f, "kVAr", 3.3f, "kVAr", 3.4f, "kVAr");
    
    
        assertEquals(thm.getIncomingEnergyUnit(),"kWh");
        assertEquals(thm.getIncomingEnergyValue(),1.1f);
                        
        assertEquals(thm.getOutgoingEnergyUnit(),"kWh");
        assertEquals(thm.getOutgoingEnergyValue(),1.2f);
       
        assertEquals(thm.getIncomingEnergyP1Unit(),"kWh");
        assertEquals(thm.getIncomingEnergyP1Value(),1.3f);
        assertEquals(thm.getIncomingEnergyP2Unit(),"kWh");
        assertEquals(thm.getIncomingEnergyP2Value(),1.4f);
        assertEquals(thm.getIncomingEnergyP3Unit(),"kWh");
        assertEquals(thm.getIncomingEnergyP3Value(),1.5f);
    
        assertEquals(thm.getOutgoingEnergyP1Unit(),"kWh");
        assertEquals(thm.getOutgoingEnergyP1Value(),1.6f);
        assertEquals(thm.getOutgoingEnergyP2Unit(),"kWh");
        assertEquals(thm.getOutgoingEnergyP2Value(),1.7f);
        assertEquals(thm.getOutgoingEnergyP3Unit(),"kWh");
        assertEquals(thm.getOutgoingEnergyP3Value(),1.8f);
    
        assertEquals(thm.getIncomingPowerUnit(),"W");
        assertEquals(thm.getIncomingPowerValue(),1.9f);
                        
        assertEquals(thm.getOutgoingPowerUnit(),"W");
        assertEquals(thm.getOutgoingPowerValue(),2.0f);
       
        assertEquals(thm.getIncomingPowerP1Unit(),"W");
        assertEquals(thm.getIncomingPowerP1Value(),2.1f);
        assertEquals(thm.getIncomingPowerP2Unit(),"W");
        assertEquals(thm.getIncomingPowerP2Value(),2.2f);
        assertEquals(thm.getIncomingPowerP3Unit(),"W");
        assertEquals(thm.getIncomingPowerP3Value(),2.3f);
    
        assertEquals(thm.getOutgoingPowerP1Unit(),"W");
        assertEquals(thm.getOutgoingPowerP1Value(),2.4f);
        assertEquals(thm.getOutgoingPowerP2Unit(),"W");
        assertEquals(thm.getOutgoingPowerP2Value(),2.5f);
        assertEquals(thm.getOutgoingPowerP3Unit(),"W");
        assertEquals(thm.getOutgoingPowerP3Value(),2.6f);
          
        assertEquals(thm.getIncomingReactiveInductiveEnergyUnit(),"kVArh");
        assertEquals(thm.getIncomingReactiveInductiveEnergyValue(),2.7f);
        assertEquals(thm.getOutgoingReactiveInductiveEnergyUnit(),"kVArh");
        assertEquals(thm.getOutgoingReactiveInductiveEnergyValue(),2.8f);
        
        assertEquals(thm.getIncomingReactiveCapacitiveEnergyUnit(),"kVArh");
        assertEquals(thm.getIncomingReactiveCapacitiveEnergyValue(),2.9f);
        assertEquals(thm.getOutgoingReactiveCapacitiveEnergyUnit(),"kVArh");
        assertEquals(thm.getOutgoingReactiveCapacitiveEnergyValue(),3.0f);
            
        assertEquals(thm.getIncomingReactiveInductivePowerUnit(),"kVAr");
        assertEquals(thm.getIncomingReactiveInductivePowerValue(),3.1f);
        assertEquals(thm.getOutgoingReactiveInductivePowerUnit(),"kVAr");
        assertEquals(thm.getOutgoingReactiveInductivePowerValue(),3.2f);
        
        assertEquals(thm.getIncomingReactiveCapacitivePowerUnit(),"kVAr");
        assertEquals(thm.getIncomingReactiveCapacitivePowerValue(),3.3f);
        assertEquals(thm.getOutgoingReactiveCapacitivePowerUnit(),"kVAr");
        assertEquals(thm.getOutgoingReactiveCapacitivePowerValue(),3.4f);
        
        
        TemperatureMeasurement tm = new TemperatureMeasurement(23f, "C");
        assertEquals(tm.getTemperatureValue(), 23f);
        assertEquals(tm.getTemperatureUnit(), "C");
        
       VoltageMeasurement vm = new VoltageMeasurement(18.1f, "V");
       assertEquals(vm.getvoltageUnit(), "V");
       assertEquals(vm.getvoltageValue(), 18.1f);
       
       Position ps =new Position(24f, 25f, 26f, "TELIC", "Time Event");
       assertEquals(ps.getAltitude(), 24f);
       assertEquals(ps.getLongitude(), 25f);
       assertEquals(ps.getLatitude(), 26f);
       assertEquals(ps.getTrackingProtocol(), "TELIC");
       assertEquals(ps.getReportReason(), "Time Event");
       
       Relay rl = new Relay("OPEN");
       assertEquals(rl.getRelayState(), "OPEN");
    
    }
    
    static Fragment[] getFragments() {
        return new Fragment[] { new AccelerationSensor(), 
                new CurrentSensor(), 
                new DistanceSensor(),
                new HumiditySensor(), 
                new LightSensor(), 
                new MoistureSensor(), 
                new MotionSensor(),
                new SinglePhaseElectricitySensor(), 
                new ThreePhaseElectricitySensor(), 
                new TemperatureSensor(),
                new VoltageSensor(), 
                new AccelerationMeasurement(8.36f, "m/s2"), 
                new CurrentMeasurement(13.37f, "A"),
                new DistanceMeasurement(13.37f, "mm"), 
                new HumidityMeasurement(13.37f, "%RH"),
                new LightMeasurement(8.36f, "lux"),
                new MoistureMeasurement(13.37f, "%"),
                new MotionMeasurement(true, 13.37f, "m/s"),
                new SinglePhaseElectricityMeasurement(1.1f, "kWh", 1.2f, "kWh", 1.3f, "W", 1.4f, "W"),
                new ThreePhaseElectricityMeasurement(1.1f, "kWh", 1.2f, "kWh", 1.3f, "kWh", 1.4f, "kWh", 1.5f, "kWh",
                        1.6f, "kWh", 1.7f, "kWh", 1.8f, "kWh", 1.9f, "W", 2.0f, "W", 2.1f, "W", 2.2f, "W", 2.3f, "W",
                        2.4f, "W", 2.5f, "W", 2.6f, "W", 2.7f, "kVArh", 2.8f, "kVArh", 2.9f, "kVArh", 3.0f, "kVArh",
                        3.1f, "kVAr", 3.2f, "kVAr", 3.3f, "kVAr", 3.4f, "kVAr"),
                new TemperatureMeasurement(23f, "C"), 
                new VoltageMeasurement(18.1f, "V"),
                new Position(24f, 25f, 26f, "TELIC", "Time Event"), 
                new Relay("OPEN") };
    }

}
