package fr.boul2gom.cerberus.common.support;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSupport.class);

    private final RedisClient client;
    private final RedisAsyncCommands<String, String> async;

    public RedisSupport(CerberusAPI api) {
        LOGGER.info("Connecting asynchronously to Redis database...");

        final String host = api.getConfiguration().get(ConfigurationKey.REDIS_HOST);
        final int port = api.getConfiguration().get(ConfigurationKey.REDIS_PORT);
        final String username = api.getConfiguration().get(ConfigurationKey.REDIS_USERNAME);
        final String password = api.getConfiguration().get(ConfigurationKey.REDIS_PASSWORD);
        final int database = api.getConfiguration().get(ConfigurationKey.REDIS_DATABASE);

        final RedisURI.Builder builder = RedisURI.Builder.redis(host, port);
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            builder.withAuthentication(username, password.toCharArray());
        } else if (password != null && !password.isEmpty()) {
            builder.withPassword(password.toCharArray());
        }
        builder.withDatabase(database);

        this.client = RedisClient.create(builder.build());
        this.client.setOptions(ClientOptions.builder()
                .pingBeforeActivateConnection(true)
                .autoReconnect(true)
                .build());

        this.async = this.client.connect().async();
    }

    public RedisClient getClient() {
        return this.client;
    }

    public RedisAsyncCommands<String, String> get() {
        return this.async;
    }
}
