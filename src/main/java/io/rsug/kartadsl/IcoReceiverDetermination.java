package io.rsug.kartadsl;

import io.rsug.zatupka.allinone.*;

import java.util.*;

public class IcoReceiverDetermination {
    private final IcoHeader icoHeader;
    public final Map<String, String> prefixes = new LinkedHashMap<>();
    public final static int NOT_FOUND_ERROR = 0;
    public final static int NOT_FOUND_IGNORE = 1;
    public final static int NOT_FOUND_TO = 2;

    // по умолчанию икохи с 0
    private int notFoundBehaviour = NOT_FOUND_ERROR;
    private Receiver notFoundReceiver = null;

    public IcoReceiverDetermination(IcoHeader header) {
        this.icoHeader = header;
    }

    public Receiver getNotFoundReceiver() {
        return notFoundReceiver;
    }

    public IcoReceiverDetermination addPrefix(String prefix, String url) {
        prefixes.put(prefix, url);
        return this;
    }

    public IcoReceiverDetermination addReceiver(String party, String service) {
        return this;
    }

    public IcoReceiverDetermination setNoReceiverBehaviour(AllInOne allInOne) {
        Objects.requireNonNull(allInOne);
        int code = NOT_FOUND_ERROR;
        if (allInOne.getNoReceiverBehaviour() != null) {
            //AllInOne@version=100 часто нет NoReceiverBehaviour
            code = allInOne.getNoReceiverBehaviour().getIfNoReceiverFound().intValue();
            String receiverId = allInOne.getNoReceiverBehaviour().getDefaultReceiverId();
            if (receiverId != null) {
                List<ReceiverConfiguration> receiverConfigurations = allInOne.getReceiverConfigurations().getReceiverConfiguration();
                Optional<ReceiverConfiguration> receiverConfiguration = Objects.requireNonNull(receiverConfigurations).stream().filter(t -> receiverId.equals(t.getReceiverId())).findFirst();
                if (receiverConfiguration.isPresent()) {
                    this.notFoundReceiver = receiverConfiguration.get().getReceiver();
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        return this;
    }

    public IcoReceiverDetermination setBehaviourIfNotFoundError() {
        return setBehaviourIfNotFound(NOT_FOUND_ERROR, null, null);
    }

    public IcoReceiverDetermination setBehaviourIfNotFoundIgnore() {
        return setBehaviourIfNotFound(NOT_FOUND_IGNORE, null, null);
    }

    public IcoReceiverDetermination setBehaviourIfNotFound(int x, String party, String service) {
        if (service != null) {
            this.notFoundReceiver = new Receiver();
            this.notFoundReceiver.setPartyExtractor(IcoHeader.trdextractortKO(Objects.requireNonNull(party)));
            this.notFoundReceiver.setServiceExtractor(IcoHeader.trdextractortKO(Objects.requireNonNull(service)));
        }
        if (x == NOT_FOUND_ERROR || x == NOT_FOUND_IGNORE) {
            this.notFoundBehaviour = x;
        } else if (x == NOT_FOUND_TO) {
            this.notFoundBehaviour = x;
            //TODO проверить гипотезу, можно ли в икохе ввести только партию без сервиса.
            if (notFoundReceiver == null) {
                throw new IllegalArgumentException("Party or service must be set");
            }
        } else {
            throw new IllegalArgumentException("Invalid not found behaviour constant: " + x);
        }
        return this;
    }

    public IcoInterfaceDetermination addInterfaceDetermination(String party, String service) {
        return new IcoInterfaceDetermination(this, party, service);
    }

    public IcoInterfaceDetermination addInterfaceDetermination(String party, String service, String namespace, String operationMapping) {
        return new IcoInterfaceDetermination(this, party, service);
    }

    public int getNotFoundBehaviour() {
        return notFoundBehaviour;
    }
}
