package fr.boul2gom.cerberus.common.database.cache;

import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.database.Storable;

public class NoneDatabaseManager implements IDatabaseManager {

    @Override
    public <T extends Storable> T fetch(String folder, String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Storable> void save(String folder, String key, T value) {

    }

    @Override
    public void delete(String folder, String key) {

    }
}
