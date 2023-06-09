package fr.boul2gom.cerberus.common.support.database;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class MemcachedSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemcachedSupport.class);

    private final MemcachedClient client;

    public MemcachedSupport(CerberusAPI api) {
        LOGGER.info("Connecting asynchronously to Memcached database...");

        final String host = api.getConfiguration().get(ConfigurationKey.MEMCACHED_HOST);
        final int port = api.getConfiguration().get(ConfigurationKey.MEMCACHED_PORT);

        try {
            this.client = new MemcachedClient(new InetSocketAddress(host, port));
        } catch (Exception exception) {
            LOGGER.error("Unable to connect to Memcached database", exception);
            throw new RuntimeException(exception);
        }
    }

    public MemcachedClient getClient() {
        return this.client;
    }
}
