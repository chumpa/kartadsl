package io.rsug.zatupka.hmi;

public class HmiMethodOutput {
    public final static String typeID = "com.sap.aii.utilxi.hmi.api.HmiMethodOutput";
    public final String ContentType, Return;

    public HmiMethodOutput(Instance instance) {
        Hmi hmi = new Hmi(instance);
        if (!typeID.equals(hmi.typeID)) throw new RuntimeException();
        ContentType = hmi.getString("ContentType");
        Return = hmi.getString("Return");
    }
}
