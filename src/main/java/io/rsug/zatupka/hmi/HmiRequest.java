package io.rsug.zatupka.hmi;

import java.util.Objects;

public class HmiRequest {
    public final static String typeID = "com.sap.aii.util.hmi.core.msg.HmiRequest";
    public final String ClientId, ClientLanguage, ClientPassword, ClientUser, ControlFlag, HmiSpecVersion, MethodId;
    public final String RequestId, RequiresSession, ServerApplicationId, ServerLogicalSystemName, ServiceId;
    public final ClientLevel ClientLevel;

    public HmiRequest(Instance instance) {
        Hmi hmi = new Hmi(instance);
        if (!typeID.equals(hmi.typeID)) throw new RuntimeException();
        ClientId = hmi.getString("ClientId");
        ClientLanguage = hmi.getString("ClientLanguage");
        ClientPassword = hmi.getString("ClientPassword");
        ClientUser = hmi.getString("ClientUser");
        ControlFlag = hmi.getString("ControlFlag");
        HmiSpecVersion = hmi.getString("HmiSpecVersion");
        MethodId = hmi.getString("MethodId");
        RequestId = hmi.getString("RequestId");
        RequiresSession = hmi.getString("RequiresSession");
        ServerApplicationId = hmi.getString("ServerApplicationId");
        ServerLogicalSystemName = hmi.getString("ServerLogicalSystemName");
        ServiceId = hmi.getString("ServiceId");
        Value v = Objects.requireNonNull(hmi.getValues("ClientLevel").getFirst());
        if (v.isIsnull()) {
            ClientLevel = null;
        } else if (v.getContent() != null && !v.getContent().isEmpty() && v.getContent().getFirst() instanceof Instance) {
            ClientLevel = new ClientLevel((Instance) v.getContent().getFirst());
        } else {
            throw new RuntimeException();
        }
    }
}
