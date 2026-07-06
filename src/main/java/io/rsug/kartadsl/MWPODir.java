package io.rsug.kartadsl;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MWPODir {
    final List<MWPO> adapterFrameworks = new LinkedList<>();
    final MWSLD sld;
    final List<String> parties = new LinkedList<>();
    final List<String> communicationComponents = new LinkedList<>();
    final List<String> communicationChannels = new LinkedList<>();

    MWPODir(MWPO po, MWSLD sld) {
        this.sld = Objects.requireNonNull(sld);
        adapterFrameworks.add(Objects.requireNonNull(po));
    }

    public void addAdapterFramework(MWPO af) {
        adapterFrameworks.add(Objects.requireNonNull(af));
    }

    public void addParty(String party) {
        MWPO.patternMatches(MWPO.PATTERN_COMPONENT, Objects.requireNonNull(party));
        if (!parties.contains(party)) {
            parties.add(party);
        }
    }

    public void assignBS(String party, String businessSystemName) {
        String s = MWPO.makeAddress(party, businessSystemName);
        if (!communicationComponents.contains(s)) {
            if (party!=null && !parties.contains(party)) {
                throw new IllegalArgumentException("No such party exists: " + party);
            }
            if (!sld.businessSystems.contains(businessSystemName)) {
                throw new IllegalStateException("No such business system in SLD: " + businessSystemName);
            }
            communicationComponents.add(s);
        }
    }

    public void createComponent(String party, String businessSystemName) {
        String s = MWPO.makeAddress(party, businessSystemName);
        if (party!=null && !parties.contains(party)) {
            throw new IllegalArgumentException("No such party exists: " + party);
        }
        if (!communicationComponents.contains(s)) {
            communicationComponents.add(s);
        }
    }

    public void createCC(String componentAddress, String channelName) {
        String s = MWPO.makeAddressCC(componentAddress, channelName);
        if (communicationChannels.contains(s)) return;
        communicationChannels.add(s);
        if (!communicationComponents.contains(componentAddress)) {
            throw new IllegalArgumentException("Component " + componentAddress + " doesn't exist");
        }
    }

    public MWPOICo createICo(String senderAddress, String senderIFQN, String receiverAddress) {
        return new MWPOICo(this, senderAddress, senderIFQN, receiverAddress);
    }

    public String toCamelDSL() {
        // Можно строить Camel Java DSL строго по одному ICo, но если канал-сендер привязан к нескольким ICo,
        // то нужен метод на уровне PODir (канал во from один, далее Router по именам интерфейсов и заголовков)
        throw new IllegalArgumentException("Not implemented yet");
    }

}
