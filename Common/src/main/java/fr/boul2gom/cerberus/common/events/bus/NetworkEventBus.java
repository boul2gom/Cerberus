package fr.boul2gom.cerberus.common.events.bus;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.events.bus.IEventBus;
import fr.boul2gom.cerberus.api.events.context.IEventContext;
import fr.boul2gom.cerberus.api.events.listener.EventPriority;
import fr.boul2gom.cerberus.api.events.listener.IEventListener;
import fr.boul2gom.cerberus.api.events.protocol.EventPacket;
import fr.boul2gom.cerberus.api.i18n.IMessage;
import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.common.events.context.EventContext;
import fr.boul2gom.cerberus.common.utils.lambda.LambdaException;
import fr.boul2gom.cerberus.common.utils.lambda.LambdaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class NetworkEventBus implements IEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(IEventBus.class);

    private final String name;

    private final PriorityComparator comparator;
    private final Map<Class<? extends Event>, PriorityQueue<IEventContext<?>>> contexts;

    public NetworkEventBus(String name) {
        this.name = name + " Event Bus";

        this.comparator = new PriorityComparator();
        this.contexts = new HashMap<>();
    }

    @Override
    public void start() {
        LOGGER.info(IMessage.get("starting.message"), this.name);

        CerberusAPI.get().getProtocol().subscribe(Channel.EVENTS, EventPacket.class, (channel, packet) -> {
            if (channel.isNot(Channel.EVENTS)) {
                return;
            }

            final Event event = packet.getEvent();
            this.dispatch0(event);
        });
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void publish(Event event) {
        final EventPacket packet = new EventPacket(event);

        CerberusAPI.get().getProtocol().publish(Channel.EVENTS, packet);
    }

    @Override
    public void dispatch(Event event) {
        CerberusAPI.get().getScheduler().async(() -> this.dispatch0(event));
    }

    @Override
    public <E extends Event> IEventContext<E> subscribe(Class<E> event, IEventListener<E> listener) {
        return this.subscribe(event, EventPriority.NORMAL, listener);
    }

    @Override
    public <E extends Event> IEventContext<E> subscribe(Class<E> event, EventPriority priority, IEventListener<E> listener) {
        try {
            final SerializedLambda lambda = LambdaUtils.serialize(listener);
            final Method method = LambdaUtils.getMethod(lambda);

            return this.subscribe0(event, priority, listener, method);
        } catch (LambdaException e) {
            LOGGER.info(IMessage.get("event-bus.subscribing.lambda-not-found"), listener.getClass().getSimpleName());

            try {
                final Class<?> clazz = listener.getClass();
                final Method method = clazz.getMethod(IEventListener.CALL_METHOD, Event.class);

                return this.subscribe0(event, priority, listener, method);
            } catch (NoSuchMethodException ex) {
                LOGGER.error(IMessage.get("event-bus.subscribing.error"), listener.getClass().getSimpleName(), event.getSimpleName(), e);
                return null;
            }
        }
    }

    @Override
    public <E extends Event> void unsubscribe(Class<E> event, IEventListener<E> listener) {
        final PriorityQueue<IEventContext<?>> queue = this.contexts.get(event);

        if (queue == null) {
            return;
        }

        queue.removeIf(context -> context.getEvent() == event && context.getListener() == listener);
    }

    private <E extends Event> void dispatch0(E event) {
        final PriorityQueue<IEventContext<?>> queue = this.contexts.get(event.getClass());

        if (queue == null) {
            return;
        }

        queue.forEach(context -> {
            try {
                context.invoke(event);
            } catch (ReflectiveOperationException e) {
                LOGGER.error(IMessage.get("event-bus.dispatching.error"), event.getClass().getSimpleName(), e);
            }
        });
    }

    private <E extends Event> IEventContext<E> subscribe0(Class<E> event, EventPriority priority, IEventListener<E> listener, Method method) {
        final PriorityQueue<IEventContext<?>> queue = this.contexts.computeIfAbsent(event, k -> new PriorityQueue<>(this.comparator));
        final IEventContext<E> context = new EventContext<>(event, priority, listener, method);

        queue.add(context);
        this.contexts.put(event, queue);

        return context;
    }

    public static class PriorityComparator implements Comparator<IEventContext<?>> {

        @Override
        public int compare(IEventContext<?> o1, IEventContext<?> o2) {
            final int first = o1.getPriority().getWeight();
            final int second = o2.getPriority().getWeight();

            return Integer.compare(first, second);
        }
    }
}
