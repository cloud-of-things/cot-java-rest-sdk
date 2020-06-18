package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class SinglePhaseElectricityMeasurement implements Fragment {

    // Total active energy, incoming:
    private final float aPlusValue;
    private final String aPlusUnit;

    // Total active energy, outgoing:
    private final float aMinusValue;
    private final String aMinusUnit;

    // Total active power, incoming:
    private final float pPlusValue;
    private final String pPlusUnit;

    // Total active power, outgoing:
    private final float pMinusValue;
    private final String pMinusUnit;

    public SinglePhaseElectricityMeasurement(float aPlusValue, String aPlusUnit, float aMinusValue, String aMinusUnit,
            float pPlusValue, String pPlusUnit, float pMinusValue, String pMinusUnit) {

        this.aPlusValue = aPlusValue;
        this.aPlusUnit = aPlusUnit;
        this.aMinusValue = aMinusValue;
        this.aMinusUnit = aMinusUnit;
        this.pPlusValue = pPlusValue;
        this.pPlusUnit = pPlusUnit;
        this.pMinusValue = pMinusValue;
        this.pMinusUnit = pMinusUnit;
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

    @Override
    public String getId() {
        return "c8y_SinglePhaseElectricityMeasurement";
    }

    @Override
    public JsonElement getJson() {
        JsonObject aPlus = new JsonObject();
        aPlus.addProperty("value", aPlusValue);
        aPlus.addProperty("unit", aPlusUnit);
        JsonObject aMinus = new JsonObject();
        aMinus.addProperty("value", aMinusValue);
        aMinus.addProperty("unit", aMinusUnit);

        JsonObject pPlus = new JsonObject();
        pPlus.addProperty("value", pPlusValue);
        pPlus.addProperty("unit", pPlusUnit);
        JsonObject pMinus = new JsonObject();
        pMinus.addProperty("value", pMinusValue);
        pMinus.addProperty("unit", pMinusUnit);

        JsonObject energyObject = new JsonObject();
        energyObject.add("A+", aPlus);
        energyObject.add("A-", aMinus);
        energyObject.add("P+", pPlus);
        energyObject.add("P-", pMinus);

        return energyObject;
    }

}
