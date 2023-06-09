package fr.boul2gom.cerberus.common.events.context;

import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.events.context.IEventContext;
import fr.boul2gom.cerberus.api.events.listener.EventPriority;
import fr.boul2gom.cerberus.api.events.listener.IEventListener;

import java.lang.reflect.Method;

public class EventContext<E extends Event> implements IEventContext<E> {

    private final Class<E> event;
    private final EventPriority priority;
    private final Object listener;

    private final Method method;

    public EventContext(Class<E> event, EventPriority priority, IEventListener<E> listener, Method method) {
        this.event = event;
        this.priority = priority;
        this.listener = listener;
        this.method = method;
    }

    @Override
    public Class<E> getEvent() {
        return this.event;
    }

    @Override
    public EventPriority getPriority() {
        return this.priority;
    }

    @Override
    public Object getListener() {
        return this.listener;
    }

    @Override
    public void invoke(Object event) throws ReflectiveOperationException {
        this.method.setAccessible(true);
        this.method.invoke(this.listener, event);
    }
}
