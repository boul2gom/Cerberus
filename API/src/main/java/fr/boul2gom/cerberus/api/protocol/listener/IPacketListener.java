package fr.boul2gom.cerberus.api.protocol.listener;

import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.api.protocol.Packet;

import java.io.Serializable;

@FunctionalInterface
public interface IPacketListener<P extends Packet> extends Serializable {

    String CALL_METHOD = "call";

    void call(Channel channel, P packet);
}
