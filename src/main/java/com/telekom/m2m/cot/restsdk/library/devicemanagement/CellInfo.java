package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CellInfo implements Fragment {

    private static final Gson gson = GsonUtils.createGson();

    private String radioType;
    private List<CellTower> cellTowers = new ArrayList<>();


    public CellInfo() {
        radioType = null; // CellTowers will have to specify their own radioType now.
    }

    public CellInfo(String radioType, CellTower... cellTowers) {
        this.radioType = radioType;
        for (CellTower cellTower : cellTowers) {
            addCellTower(cellTower);
        }
    }


    public String getRadioType() {
        return radioType;
    }


    public List<CellTower> getCellTowers() {
        // It's ok to give out our CellTower instances because they are immutable.
        return new ArrayList<>(cellTowers);
    }


    public CellInfo addCellTower(CellTower cellTower) {
        // CellTowers don't need a radioType if and only if CellInfo has one. Conflicts are not allowed.
        if (cellTower.radioType == null) {
            if (radioType == null) {
                throw new CotSdkException("CellTower needs to have a radioType because this CellInfo doesn't.");
            }
        } else {
            if ((radioType != null) && (!cellTower.radioType.equals(radioType))) {
                throw new CotSdkException("CellTower.radioType ("+cellTower.radioType+") conflicts with this CellInfo.radioType ("+radioType+")");
            }
        }

        cellTowers.add(cellTower);
        return this;
    }


    public CellInfo removeCellTower(CellTower cellTower) {
        cellTowers.remove(cellTower);
        return this;
    }


    @Override
    public String getId() {
        return "c8y_CellInfo";
    }


    @Override
    public JsonElement getJson() {
        JsonArray array = new JsonArray();
        for (CellInfo.CellTower cellTower : cellTowers) {
            array.add(gson.toJsonTree(cellTower));
        }

        JsonObject object = new JsonObject();
        if (radioType != null) {
            object.addProperty("radioType", radioType);
        }
        object.add("cellTowers", array);
        return object;
    }



    public static class CellTower {
        public final String radioType;
        public final int mobileCountryCode;
        public final int mobileNetworkCode;
        public final int locationAreaCode;
        public final int cellId;
        public final Integer timingAdvance;
        public final Integer signalStrength;
        public final Long primaryScramblingCode;
        public final Integer serving;

        public CellTower(String radioType, int mobileCountryCode, int mobileNetworkCode, int locationAreaCode, int cellId, Integer timingAdvance, Integer signalStrength, Long primaryScramblingCode, Integer serving) {
            this.radioType = radioType;
            this.mobileCountryCode = mobileCountryCode;
            this.mobileNetworkCode = mobileNetworkCode;
            this.locationAreaCode = locationAreaCode;
            this.cellId = cellId;
            this.timingAdvance = timingAdvance;
            this.signalStrength = signalStrength;
            this.primaryScramblingCode = primaryScramblingCode;
            this.serving = serving;
        }
    }

}
