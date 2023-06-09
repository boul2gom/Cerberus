package fr.boul2gom.cerberus.common.protocol.rabbit;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.i18n.IMessage;
import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.api.protocol.IProtocolManager;
import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.cipher.IPacketEncoder;
import fr.boul2gom.cerberus.common.protocol.ProtocolManager;
import fr.boul2gom.cerberus.common.support.protocol.RabbitSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.UUID;

public class RabbitProtocolManager extends ProtocolManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitProtocolManager.class);

    private final RabbitSupport support;
    private final IPacketEncoder encoder;

    public RabbitProtocolManager(RabbitSupport support, IPacketEncoder encoder) {
        LOGGER.info(IMessage.get("service.connecting.message"), "RabbitMQ Pub/Sub");

        this.support = support;
        this.encoder = encoder;
    }

    @Override
    public void start() {
        for (final Channel channel : Channel.values()) {
            if (channel.is(Channel.UNKNOWN)) {
                continue;
            }

            final String name = channel.getChannel();
            LOGGER.info(IMessage.get("rabbitmq.protocol.exchange.subscribe", name));
            this.support.declare(name);

            final DeliverCallback callback = new Listener(channel, this.encoder, this);
            final String queue = UUID.randomUUID().toString();

            this.support.queue(queue);
            this.support.bind(name, queue);

            this.support.consume(queue, callback);
        }
    }

    @Override
    public <P extends Packet> void publish(Channel channel, P packet) {
        CerberusAPI.get().getScheduler().async(() -> {
            final byte[] content = this.encoder.encode(packet).array();
            final String exchange = channel.getChannel();

            this.support.publish(exchange, content);
        });
    }

    public static class Listener implements DeliverCallback {

        private final Channel channel;
        private final IPacketEncoder encoder;
        private final IProtocolManager manager;

        public Listener(Channel channel, IPacketEncoder encoder, IProtocolManager manager) {
            this.channel = channel;
            this.encoder = encoder;
            this.manager = manager;
        }

        @Override
        public void handle(String consumerTag, Delivery message) {
            final byte[] content = message.getBody();
            final Packet packet = this.encoder.decode(ByteBuffer.wrap(content));

            if (packet == null) {
                LOGGER.warn(IMessage.get("rabbitmq.protocol.receiver.error.invalid-packet", this.channel));
                return;
            }

            this.manager.dispatch(this.channel, packet);
        }
    }
}
