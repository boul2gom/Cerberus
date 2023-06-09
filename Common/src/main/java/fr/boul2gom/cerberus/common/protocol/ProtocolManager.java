package fr.boul2gom.cerberus.common.protocol;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.events.listener.IEventListener;
import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.api.protocol.IProtocolManager;
import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.context.IPacketContext;
import fr.boul2gom.cerberus.api.protocol.listener.IPacketListener;
import fr.boul2gom.cerberus.common.protocol.context.PacketContext;
import fr.boul2gom.cerberus.common.utils.lambda.LambdaException;
import fr.boul2gom.cerberus.common.utils.lambda.LambdaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public abstract class ProtocolManager implements IProtocolManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(IProtocolManager.class);

    protected final Table<Channel, Class<?>, Set<IPacketContext<?>>> contexts = HashBasedTable.create();

    @Override
    public <P extends Packet> void dispatch(Channel channel, P packet) {
        CerberusAPI.get().getScheduler().async(() -> this.dispatch0(channel, packet));
    }

    @Override
    public <P extends Packet> void subscribe(Channel channel, Class<P> packet, IPacketListener<P> listener) {
        try {
            final SerializedLambda lambda = LambdaUtils.serialize(listener);
            final Method method = LambdaUtils.getMethod(lambda);

            this.subscribe0(channel, packet, listener, method);
        } catch (LambdaException e) {
            LOGGER.info("Listener {} is not a lambda expression, trying to find call method...", listener.getClass().getSimpleName());

            try {
                final Class<?> clazz = listener.getClass();
                final Method method = clazz.getMethod(IEventListener.CALL_METHOD, Channel.class, Packet.class);

                this.subscribe0(channel, packet, listener, method);
            } catch (NoSuchMethodException ex) {
                LOGGER.error("An error occurred while subscribing listener {} to packet {}", listener.getClass().getSimpleName(), packet.getSimpleName(), ex);
            }
        }
    }

    @Override
    public <P extends Packet> void unsubscribe(Channel channel, Class<P> packet, IPacketListener<P> listener) {
        final Set<IPacketContext<?>> set = this.contexts.get(channel, packet);

        if (set == null) {
            return;
        }

        set.removeIf(context -> context.getPacket() == packet && context.getListener() == listener);
        this.contexts.put(channel, packet, set);
    }

    @Override
    public <P extends Packet> void publish(P packet) {
        this.publish(Channel.GLOBAL, packet);
    }

    private <P extends Packet> void dispatch0(Channel channel, P packet) {
        final Set<IPacketContext<?>> set = this.contexts.get(channel, packet.getClass());

        if (set == null) {
            return;
        }

        set.forEach(context -> {
            try {
                context.invoke(channel, packet);
            } catch (ReflectiveOperationException e) {
                LOGGER.error("An error occurred while dispatching packet {}", packet.getClass().getSimpleName(), e);
            }
        });
    }

    private <P extends Packet> void subscribe0(Channel channel, Class<P> packet, IPacketListener<P> listener, Method method) {
        if (!this.contexts.contains(channel, packet)) {
            this.contexts.put(channel, packet, new HashSet<>());
        }

        final Set<IPacketContext<?>> set = this.contexts.get(channel, packet);
        final IPacketContext<P> context = new PacketContext<>(packet, listener, method);

        set.add(context);
        this.contexts.put(channel, packet, set);
    }
}
