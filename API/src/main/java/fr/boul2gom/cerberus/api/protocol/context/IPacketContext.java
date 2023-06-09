package fr.boul2gom.cerberus.api.protocol.context;

import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.listener.IPacketListener;

public interface IPacketContext<P extends Packet> {

    Class<P> getPacket();

    IPacketListener<P> getListener();

    void invoke(Channel channel, Object packet) throws ReflectiveOperationException;
}
