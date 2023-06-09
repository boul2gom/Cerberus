package fr.boul2gom.cerberus.api.events.protocol;

import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.protocol.Packet;

public class EventPacket extends Packet {

    private final Event event;

    public EventPacket(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return this.event;
    }
}
