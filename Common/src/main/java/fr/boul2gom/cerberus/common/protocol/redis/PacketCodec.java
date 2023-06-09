package fr.boul2gom.cerberus.common.protocol.redis;

import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.cipher.IPacketEncoder;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

import java.nio.ByteBuffer;

public class PacketCodec implements RedisCodec<String, Packet> {

    private static final StringCodec STRING_CODEC = StringCodec.UTF8;

    private final IPacketEncoder encoder;

    public PacketCodec(IPacketEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return STRING_CODEC.decodeKey(bytes);
    }

    @Override
    public Packet decodeValue(ByteBuffer bytes) {
        return this.encoder.decode(bytes);
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return STRING_CODEC.encodeKey(key);
    }

    @Override
    public ByteBuffer encodeValue(Packet value) {
        return this.encoder.encode(value);
    }
}
