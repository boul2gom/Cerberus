package fr.boul2gom.cerberus.api.database;

public interface IDatabaseManager {

    <T extends Storable> T fetch(String folder, String key, Class<T> clazz);

    <T extends Storable> void save(String folder, String key, T value);

    void delete(String folder, String key);
}
