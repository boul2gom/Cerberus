package fr.boul2gom.cerberus.common.support.protocol;

import com.rabbitmq.client.*;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitSupport.class);

    private final ConnectionFactory factory;
    private final Connection connection;
    private final Channel channel;

    public RabbitSupport(CerberusAPI api) {
        LOGGER.info("Connecting asynchronously to RabbitMQ server...");

        this.factory = new ConnectionFactory();
        this.factory.setHost(api.getConfiguration().get(ConfigurationKey.RABBITMQ_HOST));
        this.factory.setPort(api.getConfiguration().get(ConfigurationKey.RABBITMQ_PORT));
        this.factory.setUsername(api.getConfiguration().get(ConfigurationKey.RABBITMQ_USERNAME));
        this.factory.setPassword(api.getConfiguration().get(ConfigurationKey.RABBITMQ_PASSWORD));
        this.factory.setAutomaticRecoveryEnabled(api.getType().isMaster());

        try {
            final String version = api.getConfiguration().get(ConfigurationKey.API_VERSION);
            this.connection = this.factory.newConnection("Cerberus-" + version);
            this.channel = this.connection.createChannel();
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Unable to connect to RabbitMQ server", e);
            throw new RuntimeException(e);
        }
    }

    public ConnectionFactory getFactory() {
        return this.factory;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void declare(String name) {
        try {
            this.channel.exchangeDeclare(name, BuiltinExchangeType.FANOUT);
        } catch (IOException exception) {
            LOGGER.error("Unable to declare exchange for channel {}", name, exception);
        }
    }

    public void queue(String name) {
        try {
            final boolean master = CerberusAPI.get().getType().isMaster();
            this.channel.queueDeclare(name, master, true, !master, null);
        } catch (IOException exception) {
            LOGGER.error("Unable to declare queue for channel {}", name, exception);
        }
    }

    public void bind(String exchange, String queue) {
        try {
            this.channel.queueBind(queue, exchange, "");
        } catch (IOException exception) {
            LOGGER.error("Unable to bind queue {} to exchange {}", queue, exchange, exception);
        }
    }

    public void publish(String name, byte[] content) {
        try {
            this.channel.basicPublish(name, "", null, content);
        } catch (IOException exception) {
            LOGGER.error("Unable to publish packet to channel {}", name, exception);
        }
    }

    public void consume(String name, DeliverCallback callback) {
        try {
            this.channel.basicConsume(name, true, callback, consumerTag -> {});
        } catch (IOException exception) {
            LOGGER.error("Unable to consume channel {}", name, exception);
        }
    }
}
