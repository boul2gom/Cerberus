package fr.boul2gom.cerberus.api.protocol.cipher;

import fr.boul2gom.cerberus.api.protocol.Packet;

import java.nio.ByteBuffer;

public interface IPacketEncoder {

    ByteBuffer encode(Packet packet);

    Packet decode(ByteBuffer buffer);
}
