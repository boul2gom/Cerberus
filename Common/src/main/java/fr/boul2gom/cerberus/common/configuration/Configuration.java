package fr.boul2gom.cerberus.common.configuration;

import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.configuration.IConfiguration;

import java.util.Map;

public class Configuration implements IConfiguration {

    private final Map<String, Object> keys;

    public Configuration(Map<String, Object> configuration) {
        this.keys = configuration;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return this.get(key, type, null);
    }

    @Override
    public <T> T get(String key, Class<T> type, T value) {
        final Object object = this.keys.get(key);

        return object == null ? value : type.cast(object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(ConfigurationKey key) {
        final Object object = this.keys.get(key.getKey());

        return (T) (object == null ? key.getValue() : object);
    }
}
