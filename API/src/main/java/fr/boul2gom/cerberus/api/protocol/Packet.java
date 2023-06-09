package fr.boul2gom.cerberus.api.protocol;

import java.util.UUID;

public class Packet {

    private final UUID uuid = UUID.randomUUID();

    public UUID getId() {
        return this.uuid;
    }
}
