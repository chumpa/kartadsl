package io.rsug.kartadsl;

import java.util.Objects;

public class IcoInterfaceDetermination {
    private final IcoReceiverDetermination receiverDetermination;

    public IcoInterfaceDetermination(IcoReceiverDetermination parent, String party, String service) {
        this.receiverDetermination = Objects.requireNonNull(parent);
        //TODO проверка на существование в parent записи с party|service
    }

    /**
     * Без преобразования, без условий
     *
     * @param ifaceNS
     * @param ifaceName
     * @return
     */
    public IcoInterfaceDetermination setReceiverInterface(String ifaceNS, String ifaceName) {
        Objects.requireNonNull(ifaceNS);
        Objects.requireNonNull(ifaceName);
        return this;
    }

    public IcoReceiverDetermination getReceiverDetermination() {
        return Objects.requireNonNull(receiverDetermination);
    }

    public IcoInterfaceDetermination setReceiverChannel(String name) {
        return this;
    }
}
