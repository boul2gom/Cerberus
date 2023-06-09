package fr.boul2gom.cerberus.common.protocol.context;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.context.IPacketContext;
import fr.boul2gom.cerberus.api.protocol.listener.IPacketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class PacketContext<P extends Packet> implements IPacketContext<P> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPacketContext.class);

    private final Class<P> packet;
    private final IPacketListener<P> listener;

    private final Method method;

    public PacketContext(Class<P> packet, IPacketListener<P> listener, Method method) {
        this.packet = packet;
        this.listener = listener;
        this.method = method;
    }

    @Override
    public Class<P> getPacket() {
        return this.packet;
    }

    @Override
    public IPacketListener<P> getListener() {
        return this.listener;
    }

    @Override
    public void invoke(Channel channel, Object data) throws ReflectiveOperationException {
        this.method.setAccessible(true);

        if (this.method.getDeclaringClass() == CerberusAPI.get().getEventBus().getClass()) {
            this.method.invoke(CerberusAPI.get().getEventBus(), channel, data);
            return;
        }

        this.method.invoke(this.listener, channel, data);
    }
}
