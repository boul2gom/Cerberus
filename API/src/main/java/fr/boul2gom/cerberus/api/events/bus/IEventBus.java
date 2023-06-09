package fr.boul2gom.cerberus.api.events.bus;

import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.events.context.IEventContext;
import fr.boul2gom.cerberus.api.events.listener.EventPriority;
import fr.boul2gom.cerberus.api.events.listener.IEventListener;

public interface IEventBus {

    void start();

    String getName();

    void publish(Event event);

    void dispatch(Event event);

    <E extends Event> IEventContext<E> subscribe(Class<E> event, IEventListener<E> listener);

    <E extends Event> IEventContext<E> subscribe(Class<E> event, EventPriority priority, IEventListener<E> listener);

    <E extends Event> void unsubscribe(Class<E> event, IEventListener<E> listener);
}
