package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class ThreePhaseElectricityMeasurement implements Fragment {

    /*
     * Energy Variables:
     */

    // Total active energy summed across phases, incoming:
    private final float aPlusValue;
    private final String aPlusUnit;

    // Total active energy summed across phases, outgoing:
    private final float aMinusValue;
    private final String aMinusUnit;

    // Active energy in for phase 1, incoming
    private final float aPlusOneValue;
    private final String aPlusOneUnit;

    // Active energy in for phase 1, outgoing
    private final float aMinusOneValue;
    private final String aMinusOneUnit;

    // Active energy in for phase 2, incoming
    private final float aPlusTwoValue;
    private final String aPlusTwoUnit;

    // Active energy in for phase 2, outgoing
    private final float aMinusTwoValue;
    private final String aMinusTwoUnit;

    // Active energy in for phase 3, incoming
    private final float aPlusThreeValue;
    private final String aPlusThreeUnit;

    // Active energy in for phase 3, outgoing
    private final float aMinusThreeValue;
    private final String aMinusThreeUnit;

    /*
     * Power Variables:
     */

    // Total active power, incoming:
    private final float pPlusValue;
    private final String pPlusUnit;

    // Total active power, outgoing:
    private final float pMinusValue;
    private final String pMinusUnit;

    // Active power in for phase 1, incoming
    private final float pPlusOneValue;
    private final String pPlusOneUnit;

    // Active power in for phase 1, outgoing
    private final float pMinusOneValue;
    private final String pMinusOneUnit;

    // Active power in for phase 2, incoming
    private final float pPlusTwoValue;
    private final String pPlusTwoUnit;

    // Active power in for phase 2, outgoing
    private final float pMinusTwoValue;
    private final String pMinusTwoUnit;

    // Active power in for phase 3, incoming
    private final float pPlusThreeValue;
    private final String pPlusThreeUnit;

    // Active power in for phase 3, outgoing
    private final float pMinusThreeValue;
    private final String pMinusThreeUnit;

    /*
     * Reactive Inductive Energy Variables:
     */

    // Total reactive inductive energy, incoming:
    private final float riPlusValue;
    private final String riPlusUnit;

    // Total reactive inductive energy, outgoing:
    private final float riMinusValue;
    private final String riMinusUnit;

    /*
     * Reactive Capacitive Energy Variables:
     */

    // Total capacitive inductive energy, incoming:
    private final float rcPlusValue;
    private final String rcPlusUnit;

    // Total capacitive inductive energy, outgoing:
    private final float rcMinusValue;
    private final String rcMinusUnit;

    /*
     * Reactive Inductive Power Variables:
     */

    // Total reactive inductive power, incoming:
    private final float piPlusValue;
    private final String piPlusUnit;

    // Total reactive inductive power, outgoing:
    private final float piMinusValue;
    private final String piMinusUnit;

    /*
     * Reactive Capacitive Power Variables:
     */

    // Total capacitive inductive power, incoming:
    private final float pcPlusValue;
    private final String pcPlusUnit;

    // Total capacitive inductive power, outgoing:
    private final float pcMinusValue;
    private final String pcMinusUnit;


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
            float riPlusValue, String riPlusUnit, float riMinusValue, String riMinusUnit,
            
            //Reactive Capacitive Energy Variables:                                                                                                 
            float rcPlusValue, String rcPlusUnit, float rcMinusValue, String rcMinusUnit,
            
            //Reactive Inductive Power Variables:                                                                                                 
            float piPlusValue, String piPlusUnit, float piMinusValue, String piMinusUnit,
            
            //Reactive Capacitive Power Variables:                                                                                                 
            float pcPlusValue, String pcPlusUnit, float pcMinusValue, String pcMinusUnit) {

        this.aPlusValue = aPlusValue;
        this.aPlusUnit = aPlusUnit;
        this.aMinusValue = aMinusValue;
        this.aMinusUnit = aMinusUnit;
              
        this.aPlusOneValue = aPlusOneValue;
        this.aPlusOneUnit = aPlusOneUnit;          
        this.aPlusTwoValue = aPlusTwoValue;
        this.aPlusTwoUnit = aPlusTwoUnit;           
        this.aPlusThreeValue = aPlusThreeValue;
        this.aPlusThreeUnit = aPlusThreeUnit;  
        
        this.aMinusOneValue = aMinusOneValue;
        this.aMinusOneUnit = aMinusOneUnit;          
        this.aMinusTwoValue = aMinusTwoValue;
        this.aMinusTwoUnit = aMinusTwoUnit;           
        this.aMinusThreeValue = aMinusThreeValue;
        this.aMinusThreeUnit = aMinusThreeUnit;  
              
        this.pPlusValue = pPlusValue;
        this.pPlusUnit = pPlusUnit;
        this.pMinusValue = pMinusValue;
        this.pMinusUnit = pMinusUnit;
        
        this.pPlusOneValue = pPlusOneValue;
        this.pPlusOneUnit = pPlusOneUnit;          
        this.pPlusTwoValue = pPlusTwoValue;
        this.pPlusTwoUnit = pPlusTwoUnit;           
        this.pPlusThreeValue = pPlusThreeValue;
        this.pPlusThreeUnit = pPlusThreeUnit;  
        
        this.pMinusOneValue = pMinusOneValue;
        this.pMinusOneUnit = pMinusOneUnit;          
        this.pMinusTwoValue = pMinusTwoValue;
        this.pMinusTwoUnit = pMinusTwoUnit;           
        this.pMinusThreeValue = pMinusThreeValue;
        this.pMinusThreeUnit = pMinusThreeUnit;  
        
        this.riPlusValue = riPlusValue;
        this.riPlusUnit = riPlusUnit;
        this.riMinusValue = riMinusValue;
        this.riMinusUnit = riMinusUnit;
        
        this.rcPlusValue = rcPlusValue;
        this.rcPlusUnit = rcPlusUnit;
        this.rcMinusValue = rcMinusValue;
        this.rcMinusUnit = rcMinusUnit;    
        
        this.piPlusValue = piPlusValue;
        this.piPlusUnit = piPlusUnit;
        this.piMinusValue = piMinusValue;
        this.piMinusUnit = piMinusUnit;
        
        this.pcPlusValue = pcPlusValue;
        this.pcPlusUnit = pcPlusUnit;
        this.pcMinusValue = pcMinusValue;
        this.pcMinusUnit = pcMinusUnit;  
        
    }

    public float getIncomingEnergyValue() {
        return aPlusValue;
    }


    public String getIncomingEnergyUnit() {
        return aPlusUnit;
    }


    public float getOutgoingEnergyValue() {
        return aMinusValue;
    }


    public String getOutgoingEnergyUnit() {
        return aMinusUnit;
    }


    public float getIncomingEnergyP1Value() {
        return aPlusOneValue;
    }


    public String getIncomingEnergyP1Unit() {
        return aPlusOneUnit;
    }


    public float getOutgoingEnergyP1Value() {
        return aMinusOneValue;
    }


    public String getOutgoingEnergyP1Unit() {
        return aMinusOneUnit;
    }


    public float getIncomingEnergyP2Value() {
        return aPlusTwoValue;
    }


    public String getIncomingEnergyP2Unit() {
        return aPlusTwoUnit;
    }


    public float getOutgoingEnergyP2Value() {
        return aMinusTwoValue;
    }


    public String getOutgoingEnergyP2Unit() {
        return aMinusTwoUnit;
    }


    public float getIncomingEnergyP3Value() {
        return aPlusThreeValue;
    }


    public String getIncomingEnergyP3Unit() {
        return aPlusThreeUnit;
    }


    public float getOutgoingEnergyP3Value() {
        return aMinusThreeValue;
    }


    public String getOutgoingEnergyP3Unit() {
        return aMinusThreeUnit;
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
        return riPlusValue;
    }


    public String getIncomingReactiveInductiveEnergyUnit() {
        return riPlusUnit;
    }


    public float getOutgoingReactiveInductiveEnergyValue() {
        return riMinusValue;
    }


    public String getOutgoingReactiveInductiveEnergyUnit() {
        return riMinusUnit;
    }


    public float getIncomingReactiveCapacitiveEnergyValue() {
        return rcPlusValue;
    }


    public String getIncomingReactiveCapacitiveEnergyUnit() {
        return rcPlusUnit;
    }


    public float getOutgoingReactiveCapacitiveEnergyValue() {
        return rcMinusValue;
    }


    public String getOutgoingReactiveCapacitiveEnergyUnit() {
        return rcMinusUnit;
    }


    public float getIncomingReactiveInductivePowerValue() {
        return piPlusValue;
    }


    public String getIncomingReactiveInductivePowerUnit() {
        return piPlusUnit;
    }


    public float getOutgoingReactiveInductivePowerValue() {
        return piMinusValue;
    }


    public String getOutgoingReactiveInductivePowerUnit() {
        return piMinusUnit;
    }


    public float getIncomingReactiveCapacitivePowerValue() {
        return pcPlusValue;
    }


    public String getIncomingReactiveCapacitivePowerUnit() {
        return pcPlusUnit;
    }


    public float getOutgoingReactiveCapacitivePowerValue() {
        return pcMinusValue;
    }


    public String getOutgoingReactiveCapacitivePowerUnit() {
        return pcMinusUnit;
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
        RIPlus.addProperty("value", riPlusValue);
        RIPlus.addProperty("unit", riPlusUnit);
        JsonObject RIMinus = new JsonObject();
        RIMinus.addProperty("value", riMinusValue);
        RIMinus.addProperty("unit", riMinusUnit);
        
        JsonObject RCPlus = new JsonObject();
        RCPlus.addProperty("value", rcPlusValue);
        RCPlus.addProperty("unit", rcPlusUnit);
        JsonObject RCMinus = new JsonObject();
        RCMinus.addProperty("value", rcMinusValue);
        RCMinus.addProperty("unit", rcMinusUnit);

        JsonObject PIPlus = new JsonObject();
        PIPlus.addProperty("value", piPlusValue);
        PIPlus.addProperty("unit", piPlusUnit);
        JsonObject PIMinus = new JsonObject();
        PIMinus.addProperty("value", piMinusValue);
        PIMinus.addProperty("unit", piMinusUnit);
        

        JsonObject PCPlus = new JsonObject();
        PCPlus.addProperty("value", pcPlusValue);
        PCPlus.addProperty("unit", pcPlusUnit);  
        JsonObject PCMinus = new JsonObject();
        PCMinus.addProperty("value", pcMinusValue);
        PCMinus.addProperty("unit", pcMinusUnit);
        
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
