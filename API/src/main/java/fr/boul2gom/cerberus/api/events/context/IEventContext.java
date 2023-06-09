package fr.boul2gom.cerberus.api.events.context;

import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.events.listener.EventPriority;

public interface IEventContext<E extends Event> {

    Class<E> getEvent();

    EventPriority getPriority();

    Object getListener();

    void invoke(Object event) throws ReflectiveOperationException;
}
