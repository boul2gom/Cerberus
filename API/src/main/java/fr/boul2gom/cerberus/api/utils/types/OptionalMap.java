package fr.boul2gom.cerberus.api.utils.types;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class OptionalMap<K, V> {

    private final Map<K, V> values;

    public OptionalMap(Map<K, V> values) {
        this.values = values;
    }

    public Optional<V> get(K key) {
        final V value = this.values.get(key);

        return Optional.ofNullable(value);
    }

    public OptionalMap<K, V> put(K key, V value) {
        this.values.put(key, value);
        return this;
    }

    public OptionalMap<K, V> apply(Consumer<Map<K, V>> consumer) {
        consumer.accept(this.values);
        return this;
    }

    public V fetch(Function<Map<K, V>, V> function) {
        return function.apply(this.values);
    }
}
