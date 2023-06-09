package fr.boul2gom.cerberus.api.events.listener;

import fr.boul2gom.cerberus.api.events.Event;

import java.io.Serializable;

@FunctionalInterface
public interface IEventListener<E extends Event> extends Serializable {

    String CALL_METHOD = "call";

    void call(E event);
}
