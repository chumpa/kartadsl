package io.rsug.zatupka.hmi;

public class ClientLevel {
    public final static String typeID = "com.sap.aii.util.applcomp.ApplCompLevel";
    public final String Release, SupportPackage;

    public ClientLevel(Instance instance) {
        Hmi hmi = new Hmi(instance);
        if (!typeID.equals(hmi.typeID)) throw new RuntimeException();
        Release = hmi.getString("Release");
        SupportPackage = hmi.getString("SupportPackage");
    }
}
