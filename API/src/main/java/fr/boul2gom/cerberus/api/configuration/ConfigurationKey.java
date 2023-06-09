package fr.boul2gom.cerberus.api.configuration;

import fr.boul2gom.cerberus.api.configuration.driver.CacheDriver;
import fr.boul2gom.cerberus.api.configuration.driver.DatabaseDriver;
import fr.boul2gom.cerberus.api.configuration.driver.ProtocolDriver;
import fr.boul2gom.cerberus.api.protocol.ProtocolVersion;
import fr.boul2gom.cerberus.api.utils.EnumUtils;

import java.util.function.Function;

public enum ConfigurationKey {

    API_NAME("api.name", "CerberusAPI", "The name of the API", String.class, true),
    API_VERSION("api.version", "Unknown", "The version of the API", String.class, true),
    SERVICE_NAME("service.name", "Unknown", "The name of the service", String.class, true),

    PROTOCOL_DRIVER("protocol.driver", ProtocolDriver.REDIS, "The protocol driver to use. Supported drivers are: \"Redis\", \"RabbitMQ\".", ProtocolDriver.class, ProtocolDriver::parse),
    PROTOCOL_VERSION("protocol.version", ProtocolVersion.V1_0_0, "The version of the protocol. Refer to documentation for the latest release, or follow scheme: \"Major.Minor.Patch\".", ProtocolVersion.class, ProtocolVersion::parse),

    DATABASE_DRIVER("database.driver", DatabaseDriver.REDIS, "The database driver to use. Supported drivers are: \"Redis\", \"MongoDB\".", DatabaseDriver.class, DatabaseDriver::parse),
    CACHE_DRIVER("cache.driver", CacheDriver.NONE, "The cache driver to use. Supported drivers are: \"None\", \"Redis\", \"Memcached\".", CacheDriver.class, CacheDriver::parse),

    PUBLIC_KEY("public.key", "public.pem", "The public key of the service, used to encrypt packets.", String.class),
    PRIVATE_KEY("private.key", "private.pem", "The private key of the service, used to decrypt packets.", String.class),

    LANGUAGE("language.id", "en_US", "The language of the service, used to translate messages.", String.class),


    REDIS_HOST("redis.host", "127.0.0.1", "The host of the Redis server, usually an IP address such as \"127.0.0.1\".", String.class),
    REDIS_PORT("redis.port", 6379, "The port of the Redis server, usually \"6379\"", Integer.class),
    REDIS_USERNAME("redis.username", "", "The username of the Redis server, may not be supported on old Redis servers", String.class),
    REDIS_PASSWORD("redis.password", "", "The password of the Redis server, may not be supported on old Redis servers", String.class),
    REDIS_DATABASE("redis.database", 0, "The database of the Redis server, usually \"0\"", Integer.class),

    RABBITMQ_HOST("rabbitmq.host", "127.0.0.1", "The host of the RabbitMQ server, usually an IP address such as \"127.0.0.1\"", String.class),
    RABBITMQ_PORT("rabbitmq.port", 5672, "The port of the RabbitMQ server, usually \"5672\"", Integer.class),
    RABBITMQ_USERNAME("rabbitmq.username", "guest", "The username of the RabbitMQ server, usually \"guest\" by default", String.class),
    RABBITMQ_PASSWORD("rabbitmq.password", "guest", "The password of the RabbitMQ server, usually \"guest\" by default", String.class),

    MONGODB_HOST("mongodb.host", "127.0.0.1", "The host of the MongoDB server, usually an IP address such as \"127.0.0.1\"", String.class),
    MONGODB_PORT("mongodb.port", 27017, "The port of the MongoDB server, usually \"27017\"", Integer.class),
    MONGODB_USERNAME("mongodb.username", "", "The username of the MongoDB server", String.class),
    MONGODB_PASSWORD("mongodb.password", "", "The password of the MongoDB server", String.class),
    MONGODB_OPTIONS("mongodb.options", "", "The options of the MongoDB server", String.class),
    MONGODB_DATABASE("mongodb.database", "cerberus", "The database of the MongoDB server", String.class),

    MEMCACHED_HOST("memcached.host", "127.0.0.1", "The host of the Memcached server, usually an IP address such as \"127.0.0.1\"", String.class),
    MEMCACHED_PORT("memcached.port", 11211, "The port of the Memcached server, usually \"11211\"", Integer.class),

    DOCKER_HOST("docker.host", "unix:///var/run/docker.sock", "The host of the Docker server, usually \"unix:///var/run/docker.sock\" on Linux or \"tcp://localhost:2375\" on Windows", String.class),
    DOCKER_API_VERSION("docker.api.version", "", "The API version of the Docker server, leave empty to use the latest", String.class),
    DOCKER_REGISTRY_URL("docker.registry.url", "", "The URL of the custom Docker registry, leave empty to use the default one", String.class),
    DOCKER_REGISTRY_USERNAME("docker.registry.username", "", "The username of the custom Docker registry", String.class),
    DOCKER_REGISTRY_EMAIL("docker.registry.email", "", "The email of the custom Docker registry", String.class),
    DOCKER_REGISTRY_PASSWORD("docker.registry.password", "", "The password of the custom Docker registry", String.class),
    ;

    private final String key;
    private final Object value;
    private final String description;
    private final Class<?> type;
    private final boolean environment;
    private final Function<String, Object> parser;

    <T> ConfigurationKey(String key, T value, String description, Class<T> type) {
        this(key, value, description, type, false);
    }

    <T> ConfigurationKey(String key, T value, String description, Class<T> type, boolean environment) {
        this(key, value, description, type, ConfigurationKey.parser(type), environment);
    }

    <T> ConfigurationKey(String key, T value, String description, Class<T> type, Function<String, Object> parser) {
        this(key, value, description, type, parser, false);
    }

    <T> ConfigurationKey(String key, T value, String description, Class<T> type, Function<String, Object> parser, boolean environment) {
        this.key = key;
        this.description = description;
        this.type = type;
        this.environment = environment;
        this.parser = parser;

        this.value = environment ? (System.getenv(key) != null ? System.getenv(key) : value) : value;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public Class<?> getType() {
        return this.type;
    }

    public boolean isEnvironment() {
        return this.environment;
    }

    public Function<String, Object> getParser() {
        return this.parser;
    }

    public static ConfigurationKey parse(String key) {
        return EnumUtils.parse(key, ConfigurationKey::getKey, null, ConfigurationKey.class);
    }

    private static Function<String, Object> parser(Class<?> type) {
        if (Integer.class.isAssignableFrom(type)) {
            return Integer::parseInt;
        }

        if (Boolean.class.isAssignableFrom(type)) {
            return Boolean::parseBoolean;
        }

        return object -> object;
    }
}
