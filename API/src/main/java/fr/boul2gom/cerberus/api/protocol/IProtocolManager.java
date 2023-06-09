package fr.boul2gom.cerberus.api.protocol;

import fr.boul2gom.cerberus.api.protocol.listener.IPacketListener;

public interface IProtocolManager {

    void start();

    <P extends Packet> void publish(P packet);
    <P extends Packet> void publish(Channel channel, P packet);

    <P extends Packet> void dispatch(Channel channel, P packet);

    <P extends Packet> void subscribe(Channel channel, Class<P> packet, IPacketListener<P> listener);

    <P extends Packet> void unsubscribe(Channel channel, Class<P> packet, IPacketListener<P> listener);
}
