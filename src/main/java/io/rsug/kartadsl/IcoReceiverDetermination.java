package io.rsug.kartadsl;

import io.rsug.zatupka.allinone.Condition;

import java.util.LinkedHashMap;
import java.util.Map;

record Receiver(String party, String service) {}

public class IcoReceiverDetermination {
    private final IcoHeader header;
    public final Map<String, String> prefixes = new LinkedHashMap<>();
    public final static int NOT_FOUND_ERROR = 0;
    public final static int NOT_FOUND_IGNORE = 1;
    public final static int NOT_FOUND_TO = 2;

    // по умолчанию новые икохи с
    private int notFoundBehaviour = NOT_FOUND_ERROR;
    private Receiver notFoundReceiver = null;

    public IcoReceiverDetermination(IcoHeader header) {
        this.header = header;
    }

    public IcoReceiverDetermination addReceiver(String party, String service) {
        return this;
    }

    public IcoReceiverDetermination setBehaviourIfNotFoundError() {
        return setBehaviourIfNotFound(NOT_FOUND_ERROR, null, null);
    }

    public IcoReceiverDetermination setBehaviourIfNotFoundIgnore() {
        return setBehaviourIfNotFound(NOT_FOUND_IGNORE, null, null);
    }

    public IcoReceiverDetermination setBehaviourIfNotFound(int x, String party, String service) {
        if (x == NOT_FOUND_ERROR || x == NOT_FOUND_IGNORE) {
            this.notFoundBehaviour = x;
            if (service!=null) {
                // в пиае так можно - установить ресивер но не пользоваться им. Переносим логику.
                this.notFoundReceiver = new Receiver(party, service);
            }
        } else if (x == NOT_FOUND_TO) {
            this.notFoundBehaviour = x;
            //TODO проверить гипотезу, можно ли в икохе ввести только партию без сервиса.
            //Сделать проверку логики здесь.
            if (party == null && service == null) {
                throw new IllegalArgumentException("Party or service must be set");
            }
            this.notFoundReceiver = new Receiver(party, service);
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

}
