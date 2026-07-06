package io.rsug.kartadsl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MWPOICo {
    boolean activated;
    boolean sync = false;
    private final MWPODir owner;
    private final String senderAddress, senderIFQN, receiverAddressOptional;
    private String senderCC = null;
    private Map<String, String> receiverIfqnToChannel = new HashMap<>();

    MWPOICo(MWPODir dir, String senderAddress, String senderIFQN, String receiverAddressOptional) {
        activated = false;
        owner = Objects.requireNonNull(dir);
        this.senderAddress = MWPO.requireAddress(senderAddress);
        this.senderIFQN = MWPO.requireIFQN(senderIFQN);
        if (receiverAddressOptional == null) {
            this.receiverAddressOptional = null;
        } else {
            this.receiverAddressOptional = MWPO.requireAddress(receiverAddressOptional);
        }
    }

    MWPOICo setSync() {
        return setSync(true);
    }

    MWPOICo setSync(boolean b) {
        sync = b;
        return this;
    }

    public MWPOICo assignCC(String channelAddress) {
        checkAlreadyActivated();
        MWPO.requireChannel(channelAddress);
        if (!owner.communicationChannels.contains(channelAddress)) {
            throw new IllegalStateException("No such communication channel exists: " + channelAddress);
        }
        this.senderCC = channelAddress;
        return this;
    }

    public MWPOICo addReceiverExplicit(String receiverAddress) {
        checkAlreadyActivated();
        MWPO.requireAddress(receiverAddress);
        if (!owner.communicationComponents.contains(receiverAddress)) {
            System.out.println("WARNING: receiver component doesn't exist: " + receiverAddress);
        }
        return this;
    }

    public MWPOICo addReceiverByXPath(String receiverAddress, List<String> xpaths) {
        checkAlreadyActivated();
        return this;
    }

    public MWPOICo addReceiverByDC() {
        checkAlreadyActivated();
        return this;
    }

    public MWPOICo setOM1_1(String receiverAddress, String operationMappingQN, String receiverIFQN) {
        checkAlreadyActivated();
        MWPO.requireAddress(receiverAddress);
        if (operationMappingQN != null) MWPO.requireIFQN(operationMappingQN);
        MWPO.requireIFQN(receiverIFQN);
        return this;
    }

    public MWPOICo assignCC(String receiverIFQN, String channelAddress) {
        checkAlreadyActivated();
        MWPO.requireIFQN(receiverIFQN);
        MWPO.requireChannel(channelAddress);
        if (!owner.communicationChannels.contains(channelAddress)) {
            throw new IllegalStateException("No such communication channel exists: " + channelAddress);
        }
        String component = MWPO.extractComponentFromCC(channelAddress);
        if (!owner.communicationComponents.contains(component)) {
            throw new IllegalStateException("Unexpected behaviour (unknown component for known channel)");
        }
        receiverIfqnToChannel.put(receiverIFQN, channelAddress);
        return this;
    }

    public void activate() {
        checkAlreadyActivated();
        if (senderCC == null) {
            throw new IllegalStateException("Sender CC not set");
        }
        if (receiverIfqnToChannel.isEmpty()) {
            throw new IllegalStateException("Receiver CC not set");
        }
        activated = true;
    }

    private void checkAlreadyActivated() {
        if (activated) throw new IllegalStateException("Already activated");
    }

}

