package fr.boul2gom.cerberus.common.support;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.common.support.database.MemcachedSupport;
import fr.boul2gom.cerberus.common.support.database.MongoSupport;
import fr.boul2gom.cerberus.common.support.protocol.RabbitSupport;
import fr.boul2gom.cerberus.common.support.service.DockerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

public enum SupportManager {

    MEMCACHED("Memcached", MemcachedSupport.class),
    REDIS("Redis", RedisSupport.class),
    MONGODB("MongoDB", MongoSupport.class),
    RABBITMQ("RabbitMQ", RabbitSupport.class),
    DOCKER("Docker", DockerSupport.class),

    UNKNOWN("Unknown", null);

    private static final EnumMap<SupportManager, Object> SUPPORTS = new EnumMap<>(SupportManager.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportManager.class);

    private final String name;
    private final Class<?> clazz;

    SupportManager(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public boolean is(SupportManager support) {
        return this == support;
    }

    public boolean isNot(SupportManager support) {
        return this != support;
    }

    public Object get(CerberusAPI api) {
        if (SupportManager.SUPPORTS.containsKey(this)) {
            return SupportManager.SUPPORTS.get(this);
        }

        try {
            final Constructor<?> constructor = this.clazz.getConstructor(CerberusAPI.class);
            final Object instance = constructor.newInstance(api);

            SupportManager.SUPPORTS.put(this, instance);
            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Unable to instantiate {} support", this.name, e);
            return null;
        }
    }

    public static SupportManager get(Enum<?> support) {
        for (final SupportManager manager : SupportManager.values()) {
            if (manager.is(SupportManager.UNKNOWN)) {
                continue;
            }

            if (manager.name().equalsIgnoreCase(support.name())) {
                return manager;
            }
        }

        return SupportManager.UNKNOWN;
    }
}
