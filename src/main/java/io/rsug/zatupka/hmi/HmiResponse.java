package io.rsug.zatupka.hmi;

import java.util.Objects;

public class HmiResponse {
    public final static String typeID = "com.sap.aii.utilxi.hmi.core.msg.HmiResponse";
    public final String ClientId, ControlFlag, HmiSpecVersion, RequestId;
    public final Object MethodFault, CoreException;
    public final HmiMethodOutput MethodOutput;

    public HmiResponse(Instance instance) {
        Hmi hmi = new Hmi(instance);
        if (!typeID.equals(hmi.typeID)) throw new RuntimeException();
        ClientId = hmi.getString("ClientId");
        ControlFlag = hmi.getString("ControlFlag");
        HmiSpecVersion = hmi.getString("HmiSpecVersion");
        RequestId = hmi.getString("RequestId");
        Instance instMethodOutput = hmi.getInstanceOrNull("MethodOutput");
        MethodOutput = instMethodOutput == null ? null : new HmiMethodOutput(instMethodOutput);
        Instance instMethodFault = hmi.getInstanceOrNull("MethodFault");
        Instance instCoreException = hmi.getInstanceOrNull("CoreException");
        MethodFault = null;
        CoreException = null;
    }
}
