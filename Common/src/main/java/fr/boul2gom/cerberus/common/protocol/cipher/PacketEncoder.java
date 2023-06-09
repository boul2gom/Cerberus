package fr.boul2gom.cerberus.common.protocol.cipher;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.Protocol;
import fr.boul2gom.cerberus.api.protocol.ProtocolVersion;
import fr.boul2gom.cerberus.api.protocol.cipher.IPacketEncoder;
import fr.boul2gom.cerberus.api.utils.crypto.IEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;

public class PacketEncoder implements IPacketEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPacketEncoder.class);

    private final IEncoder encoder;

    public PacketEncoder(CerberusAPI api) throws RuntimeException {
        this.encoder = api.getEncoder();
    }

    @Override
    public ByteBuffer encode(Packet packet) {
         try {
            final byte[] bytes = CerberusAPI.get().getGson().toJson(packet).getBytes();
            final byte[] encrypted = this.encoder.getEncrypt().doFinal(bytes);

            final ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + 4 + encrypted.length);

            final ProtocolVersion version = CerberusAPI.config(ConfigurationKey.PROTOCOL_VERSION);
            buffer.put((byte) version.getId());

            final int id = Protocol.get(packet.getClass());
            if (id == -1) {
                LOGGER.warn("Unable to find packet id for packet {}", packet.getClass().getSimpleName());
                return null;
            }
            buffer.put((byte) id);

            buffer.putInt(encrypted.length);
            buffer.put(encrypted);

            buffer.flip();
            return buffer;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error("Unable to encrypt packet", e);
            return null;
        }
    }

    @Override
    public Packet decode(ByteBuffer buffer) {
        try {
            final ProtocolVersion version = ProtocolVersion.parse(buffer.get());
            if (version.is(ProtocolVersion.UNKNOWN)) {
                LOGGER.warn("Unknown protocol version: {}", version);
                return null;
            }

            if (version.isNot(CerberusAPI.config(ConfigurationKey.PROTOCOL_VERSION))) {
                LOGGER.warn("Invalid protocol version: {}", version);
                return null;
            }

            final int id = buffer.get();
            final Class<?> clazz = Protocol.get(id);
            if (clazz == null) {
                LOGGER.warn("Unable to find packet class for id {}", id);
                return null;
            }

            final int length = buffer.getInt();
            final byte[] bytes = new byte[length];
            buffer.get(bytes);

            final byte[] decrypted = this.encoder.getDecrypt().doFinal(bytes);
            final String json = new String(decrypted);

            return (Packet) CerberusAPI.get().getGson().fromJson(json, clazz);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error("Unable to decrypt packet", e);
            return null;
        }
    }
}
