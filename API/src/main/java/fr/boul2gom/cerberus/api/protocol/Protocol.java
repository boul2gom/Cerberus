package fr.boul2gom.cerberus.api.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.boul2gom.cerberus.api.events.protocol.EventPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Protocol {

    PACKET(0, Packet.class),
    EVENT(1, EventPacket.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(Protocol.class);
    private static final BiMap<Integer, Class<?>> PROTOCOLS = HashBiMap.create();

    public static void initialize() {
        for (final Protocol protocol : Protocol.values()) {
            LOGGER.info("Registering packet {} with id {}...", protocol.name(), protocol.getId());
            PROTOCOLS.put(protocol.getId(), protocol.getType());
        }
    }

    private final int id;
    private final Class<?> type;

    Protocol(int id, Class<? extends Packet> type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public Class<?> getType() {
        return this.type;
    }

    public static Class<?> get(int id) {
        return PROTOCOLS.get(id);
    }

    public static int get(Class<?> type) {
        final Integer id = PROTOCOLS.inverse().get(type);

        return id == null ? -1 : id;
    }

    public static void register(int id, Class<? extends Packet> type) {
        if (PROTOCOLS.containsKey(id)) {
            throw new IllegalArgumentException("Protocol with id " + id + " is already registered!");
        }

        PROTOCOLS.put(id, type);
    }
}
