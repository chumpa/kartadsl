package io.rsug.kartadsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public sealed interface ICoPipeline permits ReceiverDetermination, Conditions, InterfaceDetermination, ReceiverConnectivity {}

record ReceiverDetermination(Map<String,String> prefixes) implements ICoPipeline {}
record Conditions() implements ICoPipeline {}
record InterfaceDetermination() implements ICoPipeline {}
record ReceiverConnectivity() implements ICoPipeline {}

//class IntegrationFlow {
//    List<ICoPipeline> steps = new ArrayList<>();
//    public IntegrationFlow senderChannel(String name) {
//        steps.add(new SenderConnectivity("GET"));
//        return this;
//    }
//    public void execute() { /* интерпретация steps */ }
//}

class SenderConnectivity {
    public final String communicationChannel;
    public SenderConnectivity(String communicationChannel) {
        this.communicationChannel = Objects.requireNonNull(communicationChannel);
    }
}

