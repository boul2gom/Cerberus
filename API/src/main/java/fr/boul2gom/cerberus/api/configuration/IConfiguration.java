package fr.boul2gom.cerberus.api.configuration;

public interface IConfiguration {

    <T> T get(ConfigurationKey key);
    <T> T get(String key, Class<T> type);
    <T> T get(String key, Class<T> type, T value);
}
