package fr.boul2gom.cerberus.common.protocol.redis;

import fr.boul2gom.cerberus.api.i18n.IMessage;
import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.api.protocol.IProtocolManager;
import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.cipher.IPacketEncoder;
import fr.boul2gom.cerberus.common.protocol.ProtocolManager;
import fr.boul2gom.cerberus.common.support.RedisSupport;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisProtocolManager extends ProtocolManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisProtocolManager.class);

    private final StatefulRedisPubSubConnection<String, Packet> connection;
    private final RedisPubSubAsyncCommands<String, Packet> pubsub;
    private final RedisAsyncCommands<String, Packet> commands;

    public RedisProtocolManager(RedisSupport support, IPacketEncoder encoder) {
        LOGGER.info(IMessage.get("service.connecting.message", "Redis Pub/Sub"));

        this.connection = support.getClient().connectPubSub(new PacketCodec(encoder));
        this.commands = support.getClient().connect(new PacketCodec(encoder)).async();
        this.pubsub = this.connection.async();
    }

    @Override
    public void start() {
        for (final Channel channel : Channel.values()) {
            if (channel.is(Channel.UNKNOWN)) {
                continue;
            }

            final String name = channel.getChannel();
            LOGGER.info(IMessage.get("redis.protocol.subscribe", name));
            this.pubsub.subscribe(name);
        }

        this.connection.addListener(new Listener(this));
    }

    @Override
    public <P extends Packet> void publish(Channel channel, P packet) {
        this.commands.publish(channel.getChannel(), packet).thenAcceptAsync(clients -> {
            if (clients != 0) {
                return;
            }

            LOGGER.warn(IMessage.get("redis.protocol.receiver.warning.no-receivers", packet.getClass().getSimpleName(), channel));
        });
    }

    public static class Listener extends RedisPubSubAdapter<String, Packet> {

        private final IProtocolManager manager;

        public Listener(IProtocolManager manager) {
            this.manager = manager;
        }

        @Override
        public void message(String channel, Packet message) {
            if (message == null) {
                LOGGER.warn(IMessage.get("protocol.receiver.error.invalid-packet", channel));
                return;
            }

            final Channel parsed = Channel.parse(channel);

            if (parsed.is(Channel.UNKNOWN)) {
                LOGGER.warn(IMessage.get("redis.protocol.receiver.warning.unknown-channel"), channel);
                return;
            }

            this.manager.dispatch(parsed, message);
        }
    }
}
