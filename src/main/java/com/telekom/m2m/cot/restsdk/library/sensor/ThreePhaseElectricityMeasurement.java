package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class ThreePhaseElectricityMeasurement implements Fragment {

    /*
     * Energy Variables:
     */

    // Total active energy summed across phases, incoming:
    private float aPlusValue;
    private String aPlusUnit;

    // Total active energy summed across phases, outgoing:
    private float aMinusValue;
    private String aMinusUnit;

    // Active energy in for phase 1, incoming
    private float aPlusOneValue;
    private String aPlusOneUnit;

    // Active energy in for phase 1, outgoing
    private float aMinusOneValue;
    private String aMinusOneUnit;

    // Active energy in for phase 2, incoming
    private float aPlusTwoValue;
    private String aPlusTwoUnit;

    // Active energy in for phase 2, outgoing
    private float aMinusTwoValue;
    private String aMinusTwoUnit;

    // Active energy in for phase 3, incoming
    private float aPlusThreeValue;
    private String aPlusThreeUnit;

    // Active energy in for phase 3, outgoing
    private float aMinusThreeValue;
    private String aMinusThreeUnit;

    /*
     * Power Variables:
     */
    // Total active power, incoming:
    private float pPlusValue;
    private String pPlusUnit;

    // Total active power, outgoing:
    private float pMinusValue;
    private String pMinusUnit;

    // Active power in for phase 1, incoming
    private float pPlusOneValue;
    private String pPlusOneUnit;

    // Active power in for phase 1, outgoing
    private float pMinusOneValue;
    private String pMinusOneUnit;

    // Active power in for phase 2, incoming
    private float pPlusTwoValue;
    private String pPlusTwoUnit;

    // Active power in for phase 2, outgoing
    private float pMinusTwoValue;
    private String pMinusTwoUnit;

    // Active power in for phase 3, incoming
    private float pPlusThreeValue;
    private String pPlusThreeUnit;

    // Active power in for phase 3, outgoing
    private float pMinusThreeValue;
    private String pMinusThreeUnit;

    /*
     * Reactive Inductive Energy Variables:
     */

    // Total reactive inductive energy, incoming:
    private float RIPlusValue;
    private String RIPlusUnit;

    // Total reactive inductive energy, outgoing:
    private float RIMinusValue;
    private String RIMinusUnit;

    /*
     * Reactive Capacitive Energy Variables:
     */

    // Total capacitive inductive energy, incoming:
    private float RCPlusValue;
    private String RCPlusUnit;

    // Total capacitive inductive energy, outgoing:
    private float RCMinusValue;
    private String RCMinusUnit;

    /*
     * Reactive Inductive Power Variables:
     */

    // Total reactive inductive power, incoming:
    private float PIPlusValue;
    private String PIPlusUnit;

    // Total reactive inductive power, outgoing:
    private float PIMinusValue;
    private String PIMinusUnit;

    /*
     * Reactive Capacitive Power Variables:
     */

    // Total capacitive inductive power, incoming:
    private float PCPlusValue;
    private String PCPlusUnit;

    // Total capacitive inductive power, outgoing:
    private float PCMinusValue;
    private String PCMinusUnit;

    public ThreePhaseElectricityMeasurement(
            //Energy Variables:
            float aPlusValue, String aPlusUnit, float aMinusValue, String aMinusUnit,
            float aPlusOneValue, String aPlusOneUnit,
            float aPlusTwoValue, String aPlusTwoUnit,         
            float aPlusThreeValue, String aPlusThreeUnit, 
            float aMinusOneValue, String aMinusOneUnit,           
            float aMinusTwoValue, String aMinusTwoUnit,
            float aMinusThreeValue, String aMinusThreeUnit,
                        
            //Power Variables:
            float pPlusValue, String pPlusUnit, float pMinusValue, String pMinusUnit,
            float pPlusOneValue, String pPlusOneUnit,
            float pPlusTwoValue, String pPlusTwoUnit,
            float pPlusThreeValue, String pPlusThreeUnit,
            float pMinusOneValue, String pMinusOneUnit,
            float pMinusTwoValue, String pMinusTwoUnit,
            float pMinusThreeValue, String pMinusThreeUnit,
            
            //Reactive Inductive Energy Variables:                                                                                                 
            float RIPlusValue, String RIPlusUnit, float RIMinusValue, String RIMinusUnit,
            
            //Reactive Capacitive Energy Variables:                                                                                                 
            float RCPlusValue, String RCPlusUnit, float RCMinusValue, String RCMinusUnit,
            
            //Reactive Inductive Power Variables:                                                                                                 
            float PIPlusValue, String PIPlusUnit, float PIMinusValue, String PIMinusUnit,
            
            //Reactive Capacitive Power Variables:                                                                                                 
            float PCPlusValue, String PCPlusUnit, float PCMinusValue, String PCMinusUnit) {

        this.aPlusValue = aPlusValue;
        this.aPlusUnit = aPlusUnit;
        this.pMinusValue = pMinusValue;
        this.pMinusUnit = pMinusUnit;
        
        this.aPlusOneValue = aPlusValue;
        this.aPlusOneUnit = aPlusUnit;          
        this.aPlusTwoValue = aPlusTwoValue;
        this.aPlusTwoUnit = aPlusTwoUnit;           
        this.aPlusThreeValue = aPlusThreeValue;
        this.aPlusThreeUnit = aPlusThreeUnit;  
        
        this.pPlusValue = pPlusValue;
        this.pPlusUnit = pPlusUnit;
        this.pMinusValue = pMinusValue;
        this.pMinusUnit = pMinusUnit;
        
        this.pPlusOneValue = pPlusValue;
        this.pPlusOneUnit = pPlusUnit;          
        this.pPlusTwoValue = pPlusTwoValue;
        this.pPlusTwoUnit = pPlusTwoUnit;           
        this.pPlusThreeValue = pPlusThreeValue;
        this.pPlusThreeUnit = pPlusThreeUnit;  
        
        this.RIPlusValue = RIPlusValue;
        this.RIPlusUnit = RIPlusUnit;
        this.RIMinusValue = RIMinusValue;
        this.RIMinusUnit = RIMinusUnit;
        
        this.RCPlusValue = RCPlusValue;
        this.RCPlusUnit = RCPlusUnit;
        this.RCMinusValue = RCMinusValue;
        this.RCMinusUnit = RCMinusUnit;    
        
        this.PIPlusValue = PIPlusValue;
        this.PIPlusUnit = PIPlusUnit;
        this.PIMinusValue = PIMinusValue;
        this.PIMinusUnit = PIMinusUnit;
        
        this.PCPlusValue = PCPlusValue;
        this.PCPlusUnit = PCPlusUnit;
        this.PCMinusValue = PCMinusValue;
        this.PCMinusUnit = PCMinusUnit;  
        
    }

    public float getIncomingEnergyValue() {
        return aPlusValue;
    }


    public String getIncomingEnergyUnit() {
        return aPlusUnit;
    }



    public float getOutgoingEnergyValue() {
        return pMinusValue;
    }



    public String getOutgoingEnergyUnit() {
        return pMinusUnit;
    }


    public float getIncomingEnergyP1Value() {
        return aPlusOneValue;
    }



    public String getIncomingEnergyP1Unit() {
        return aPlusOneUnit;
    }



    public float getOutgoingEnergyP1Value() {
        return pMinusOneValue;
    }



    public String getOutgoingEnergyP1Unit() {
        return pMinusOneUnit;
    }


    public float getIncomingEnergyP2Value() {
        return aPlusTwoValue;
    }


    public String getIncomingEnergyP2Unit() {
        return aPlusTwoUnit;
    }


    public float getOutgoingEnergyP2Value() {
        return pMinusTwoValue;
    }


    public String getOutgoingEnergyP2Unit() {
        return pMinusTwoUnit;
    }


    public float getIncomingEnergyP3Value() {
        return aPlusThreeValue;
    }


    public String getIncomingEnergyP3Unit() {
        return aPlusThreeUnit;
    }


    public float getOutgoingEnergyP3Value() {
        return pMinusThreeValue;
    }


    public String getOutgoingEnergyP3Unit() {
        return pMinusThreeUnit;
    }


    public float getIncomingPowerValue() {
        return pPlusValue;
    }


    public String getIncomingPowerUnit() {
        return pPlusUnit;
    }


    public float getOutgoingPowerValue() {
        return pMinusValue;
    }


    public String getOutgoingPowerUnit() {
        return pMinusUnit;
    }


    public float getIncomingPowerP1Value() {
        return pPlusOneValue;
    }


    public String getIncomingPowerP1Unit() {
        return pPlusOneUnit;
    }


    public float getOutgoingPowerP1Value() {
        return pMinusOneValue;
    }


    public String getOutgoingPowerP1Unit() {
        return pMinusOneUnit;
    }


    public float getIncomingPowerP2Value() {
        return pPlusTwoValue;
    }


    public String getIncomingPowerP2Unit() {
        return pPlusTwoUnit;
    }


    public float getOutgoingPowerP2Value() {
        return pMinusTwoValue;
    }


    public String getOutgoingPowerP2Unit() {
        return pMinusTwoUnit;
    }

    public float getIncomingPowerP3Value() {
        return pPlusThreeValue;
    }


    public String getIncomingPowerP3Unit() {
        return pPlusThreeUnit;
    }


    public float getOutgoingPowerP3Value() {
        return pMinusThreeValue;
    }


    public String getOutgoingPowerP3Unit() {
        return pMinusThreeUnit;
    }


    public float getIncomingReactiveInductiveEnergyValue() {
        return RIPlusValue;
    }


    public String getIncomingReactiveInductiveEnergyUnit() {
        return RIPlusUnit;
    }


    public float getOutgoingReactiveInductiveEnergyValue() {
        return RIMinusValue;
    }


    public String getOutgoingReactiveInductiveEnergyUnit() {
        return RIMinusUnit;
    }


    public float getIncomingReactiveCapacitiveEnergyValue() {
        return RCPlusValue;
    }


    public String getIncomingReactiveCapacitiveEnergyUnit() {
        return RCPlusUnit;
    }


    public float getOutgoingReactiveCapacitiveEnergyValue() {
        return RCMinusValue;
    }


    public String getOutgoingReactiveCapacitiveEnergyUnit() {
        return RCMinusUnit;
    }


    public float getIncomingReactiveInductivePowerValue() {
        return PIPlusValue;
    }


    public String getIncomingReactiveInductivePowerUnit() {
        return PIPlusUnit;
    }


    public float getOutgoingReactiveInductivePowerValue() {
        return PIMinusValue;
    }


    public String getOutgoingReactiveInductivePowerUnit() {
        return PIMinusUnit;
    }


    public float getIncomingReactiveCapacitivePowerValue() {
        return PCPlusValue;
    }


    public String getIncomingReactiveCapacitivePowerUnit() {
        return PCPlusUnit;
    }


    public float getOutgoingReactiveCapacitivePowerValue() {
        return PCMinusValue;
    }


    public String getOutgoingReactiveCapacitivePowerUnit() {
        return PCMinusUnit;
    }
   
    
    @Override
    public String getId() {
        return "c8y_ThreePhaseElectricityMeasurement";
    }
    
    
    @Override
    public JsonElement getJson() {
        JsonObject aPlus = new JsonObject();
        aPlus.addProperty("value", aPlusValue);
        aPlus.addProperty("unit", aPlusUnit);
        JsonObject aMinus = new JsonObject();
        aMinus.addProperty("value", aMinusValue);
        aMinus.addProperty("unit", aMinusUnit);

        
        JsonObject aPlusOne = new JsonObject();
        aPlusOne.addProperty("value", aPlusOneValue);
        aPlusOne.addProperty("unit", aPlusOneUnit);
        JsonObject aMinusOne = new JsonObject();
        aMinusOne.addProperty("value", aMinusOneValue);
        aMinusOne.addProperty("unit", aMinusOneUnit);
        
        JsonObject aPlusTwo = new JsonObject();
        aPlusTwo.addProperty("value", aPlusTwoValue);
        aPlusTwo.addProperty("unit", aPlusTwoUnit);
        JsonObject aMinusTwo = new JsonObject();
        aMinusTwo.addProperty("value", aMinusTwoValue);
        aMinusTwo.addProperty("unit", aMinusTwoUnit);       

        JsonObject aPlusThree = new JsonObject();
        aPlusThree.addProperty("value", aPlusThreeValue);
        aPlusThree.addProperty("unit", aPlusThreeUnit);
        JsonObject aMinusThree = new JsonObject();
        aMinusThree.addProperty("value", aMinusThreeValue);
        aMinusThree.addProperty("unit", aMinusThreeUnit);   
        
        
        JsonObject pPlus = new JsonObject();
        pPlus.addProperty("value", pPlusValue);
        pPlus.addProperty("unit", pPlusUnit);
        JsonObject pMinus = new JsonObject();
        pMinus.addProperty("value", pMinusValue);
        pMinus.addProperty("unit", pMinusUnit);

        
        JsonObject pPlusOne = new JsonObject();
        pPlusOne.addProperty("value", pPlusOneValue);
        pPlusOne.addProperty("unit", pPlusOneUnit);
        JsonObject pMinusOne = new JsonObject();
        pMinusOne.addProperty("value", pMinusOneValue);
        pMinusOne.addProperty("unit", pMinusOneUnit);
        
        JsonObject pPlusTwo = new JsonObject();
        pPlusTwo.addProperty("value", pPlusTwoValue);
        pPlusTwo.addProperty("unit", pPlusTwoUnit);
        JsonObject pMinusTwo = new JsonObject();
        pMinusTwo.addProperty("value", pMinusTwoValue);
        pMinusTwo.addProperty("unit", pMinusTwoUnit);       

        JsonObject pPlusThree = new JsonObject();
        pPlusThree.addProperty("value", pPlusThreeValue);
        pPlusThree.addProperty("unit", pPlusThreeUnit);
        JsonObject pMinusThree = new JsonObject();
        pMinusThree.addProperty("value", pMinusThreeValue);
        pMinusThree.addProperty("unit", pMinusThreeUnit); 
        
        JsonObject RIPlus = new JsonObject();
        RIPlus.addProperty("value", RIPlusValue);
        RIPlus.addProperty("unit", RIPlusUnit);
        JsonObject RIMinus = new JsonObject();
        RIMinus.addProperty("value", RIMinusValue);
        RIMinus.addProperty("unit", RIMinusUnit);
        
        JsonObject RCPlus = new JsonObject();
        RCPlus.addProperty("value", RCPlusValue);
        RCPlus.addProperty("unit", RCPlusUnit);
        JsonObject RCMinus = new JsonObject();
        RCMinus.addProperty("value", RCMinusValue);
        RCMinus.addProperty("unit", RCMinusUnit);

        JsonObject PIPlus = new JsonObject();
        PIPlus.addProperty("value", PIPlusValue);
        PIPlus.addProperty("unit", PIPlusUnit);
        JsonObject PIMinus = new JsonObject();
        PIMinus.addProperty("value", PIMinusValue);
        PIMinus.addProperty("unit", PIMinusUnit);
        

        JsonObject PCPlus = new JsonObject();
        PCPlus.addProperty("value", PCPlusValue);
        PCPlus.addProperty("unit", PCPlusUnit);  
        JsonObject PCMinus = new JsonObject();
        PCMinus.addProperty("value", PCMinusValue);
        PCMinus.addProperty("unit", PCMinusUnit);
        
        JsonObject energyObject = new JsonObject();
        energyObject.add("A+", aPlus);
        energyObject.add("A-", aMinus);
        energyObject.add("A+:1", aPlusOne);
        energyObject.add("A-:1", aMinusOne);
        energyObject.add("A+:2", aPlusTwo);
        energyObject.add("A-:2", aMinusTwo);
        energyObject.add("A+:3", aPlusThree);
        energyObject.add("A-:3", aMinusThree);
        
        
        energyObject.add("P+", pPlus);
        energyObject.add("P-", pMinus);
        energyObject.add("P+:1", pPlusOne);
        energyObject.add("P-:1", pMinusOne);
        energyObject.add("P+:2", pPlusTwo);
        energyObject.add("P-:2", pMinusTwo);
        energyObject.add("P+:3", pPlusThree);
        energyObject.add("P-:3", pMinusThree);
        
        energyObject.add("Ri+", RIPlus);
        energyObject.add("Ri-", RIMinus);
        energyObject.add("Rc+", RCPlus);
        energyObject.add("Rc-", RCMinus);
        
        energyObject.add("Qi+", PIPlus);
        energyObject.add("Qi-", PIMinus);
        energyObject.add("Qc+", PCPlus);
        energyObject.add("Qc-", PCMinus);
        
        return energyObject;
    }
    
}
