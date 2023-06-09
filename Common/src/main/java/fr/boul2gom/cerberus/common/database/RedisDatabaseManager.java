package fr.boul2gom.cerberus.common.database;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.database.Storable;
import fr.boul2gom.cerberus.common.support.RedisSupport;
import io.lettuce.core.RedisFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class RedisDatabaseManager implements IDatabaseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IDatabaseManager.class);

    private final RedisSupport support;

    public RedisDatabaseManager(RedisSupport support) {
        this.support = support;
    }

    @Override
    public <T extends Storable> T fetch(String folder, String key, Class<T> clazz) {
        try {
            final RedisFuture<String> future = this.support.get().get(folder + ":" + key);
            final String serialized = future.get();

            return CerberusAPI.get().getGson().fromJson(serialized, clazz);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("An error occurred while fetching data from Redis!", e);
        }

        return null;
    }

    @Override
    public <T extends Storable> void save(String folder, String key, T value) {
        CerberusAPI.get().getScheduler().async(() -> {
            final String serialized = CerberusAPI.get().getGson().toJson(value);

            this.support.get().set(folder + ":" + key, serialized);
        });
    }

    @Override
    public void delete(String folder, String key) {
        CerberusAPI.get().getScheduler().async(() -> {
            this.support.get().del(folder + ":" + key);
        });
    }
}
