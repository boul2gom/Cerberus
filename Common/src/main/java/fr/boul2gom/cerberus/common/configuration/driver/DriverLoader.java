package fr.boul2gom.cerberus.common.configuration.driver;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.configuration.driver.CacheDriver;
import fr.boul2gom.cerberus.api.configuration.driver.DatabaseDriver;
import fr.boul2gom.cerberus.api.configuration.driver.ProtocolDriver;
import fr.boul2gom.cerberus.api.configuration.driver.ServiceDriver;
import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.protocol.IProtocolManager;
import fr.boul2gom.cerberus.api.protocol.cipher.IPacketEncoder;
import fr.boul2gom.cerberus.api.service.IServiceManager;
import fr.boul2gom.cerberus.common.database.cache.MemcachedDatabaseManager;
import fr.boul2gom.cerberus.common.database.cache.NoneDatabaseManager;
import fr.boul2gom.cerberus.common.database.MongoDatabaseManager;
import fr.boul2gom.cerberus.common.database.RedisDatabaseManager;
import fr.boul2gom.cerberus.common.protocol.rabbit.RabbitProtocolManager;
import fr.boul2gom.cerberus.common.protocol.redis.RedisProtocolManager;
import fr.boul2gom.cerberus.common.service.DockerServiceManager;
import fr.boul2gom.cerberus.common.service.baremetal.BareMetalServiceManager;
import fr.boul2gom.cerberus.common.support.RedisSupport;
import fr.boul2gom.cerberus.common.support.SupportManager;
import fr.boul2gom.cerberus.common.support.database.MemcachedSupport;
import fr.boul2gom.cerberus.common.support.database.MongoSupport;
import fr.boul2gom.cerberus.common.support.protocol.RabbitSupport;
import fr.boul2gom.cerberus.common.support.service.DockerSupport;

public class DriverLoader {

    public static IProtocolManager protocol(ProtocolDriver driver, IPacketEncoder encoder) throws RuntimeException {
        switch (driver) {
            case RABBITMQ -> {
                final RabbitSupport rabbit = (RabbitSupport) DriverLoader.get(driver, "protocol");
                return new RabbitProtocolManager(rabbit, encoder);
            }
            case REDIS -> {
                final RedisSupport redis = (RedisSupport) DriverLoader.get(driver, "protocol");
                return new RedisProtocolManager(redis, encoder);
            }
        }

        throw new RuntimeException("Unknown protocol driver: " + driver);
    }

    public static IDatabaseManager database(DatabaseDriver driver) throws RuntimeException {
        switch (driver) {
            case REDIS -> {
                final RedisSupport redis = (RedisSupport) DriverLoader.get(driver, "database");
                return new RedisDatabaseManager(redis);
            }
            case MONGODB -> {
                final MongoSupport mongo = (MongoSupport) DriverLoader.get(driver, "database");
                return new MongoDatabaseManager(mongo);
            }
        }

        throw new RuntimeException("Unknown database driver: " + driver);
    }

    public static IDatabaseManager cache(CacheDriver driver) throws RuntimeException {
        switch (driver) {
            case NONE -> {
                return new NoneDatabaseManager();
            }
            case REDIS -> {
                final RedisSupport redis = (RedisSupport) DriverLoader.get(driver, "cache");
                return new RedisDatabaseManager(redis);
            }
            case MEMCACHED -> {
                final MemcachedSupport memcached = (MemcachedSupport) DriverLoader.get(driver, "cache");
                return new MemcachedDatabaseManager(memcached);
            }
        }

        throw new RuntimeException("Unknown cache driver: " + driver);
    }

    public static IServiceManager service(ServiceDriver driver) throws RuntimeException {
        switch (driver) {
            case BARE_METAL -> {
                return new BareMetalServiceManager();
            }
            case DOCKER -> {
                final DockerSupport docker = (DockerSupport) DriverLoader.get(driver, "service");
                return new DockerServiceManager(docker);
            }
        }

        throw new RuntimeException("Unknown service driver: " + driver);
    }

    private static Object get(Enum<?> driver, String type) throws RuntimeException {
        final SupportManager support = SupportManager.get(driver);
        if (support.is(SupportManager.UNKNOWN)) {
            throw new RuntimeException("Unknown " + type + " driver: " + driver);
        }

        return support.get(CerberusAPI.get());
    }
}
