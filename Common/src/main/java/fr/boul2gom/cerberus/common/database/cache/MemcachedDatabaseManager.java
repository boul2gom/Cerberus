package fr.boul2gom.cerberus.common.database.cache;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.database.Storable;
import fr.boul2gom.cerberus.common.support.database.MemcachedSupport;

public class MemcachedDatabaseManager implements IDatabaseManager {

    private final MemcachedSupport support;

    public MemcachedDatabaseManager(MemcachedSupport support) {
        this.support = support;
    }

    @Override
    public <T extends Storable> T fetch(String folder, String key, Class<T> clazz) {
        final String serialized = (String) this.support.getClient().get(folder + ":" + key);
        if (serialized == null) {
            return null;
        }

        return CerberusAPI.get().getGson().fromJson(serialized, clazz);
    }

    @Override
    public <T extends Storable> void save(String folder, String key, T value) {
        CerberusAPI.get().getScheduler().async(() -> {
            final String serialized = CerberusAPI.get().getGson().toJson(value);

            this.support.getClient().set(folder + ":" + key, 0, serialized);
        });
    }

    @Override
    public void delete(String folder, String key) {
        CerberusAPI.get().getScheduler().async(() -> this.support.getClient().delete(folder + ":" + key));
    }
}
