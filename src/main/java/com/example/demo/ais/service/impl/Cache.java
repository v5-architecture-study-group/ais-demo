package com.example.demo.ais.service.impl;

import com.example.demo.ais.domain.base.Identifiable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class Cache<K, V extends Identifiable<K>> {

    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final Predicate<? super V> filter;

    Cache(Collection<V> initialData) {
        this(initialData, null);
    }

    Cache(Collection<V> initialData, Predicate<? super V> filter) {
        requireNonNull(initialData, "initialData must not be null");
        this.filter = filter;
        if (filter == null) {
            initialData.forEach(v -> cache.put(v.id(), v));
        } else {
            initialData.stream().filter(filter).forEach(v -> cache.put(v.id(), v));
        }
    }

    Optional<V> get(K key) {
        if (filter == null) {
            return Optional.ofNullable(cache.get(key));
        } else {
            return Optional.ofNullable(cache.get(key)).filter(filter);
        }
    }

    Stream<V> values() {
        return cache.values().stream();
    }

    int size() {
        return cache.size();
    }

    void put(V value) {
        requireNonNull(value, "value must not be null");
        if (filter == null || filter.test(value)) {
            cache.put(value.id(), value);
        } else {
            cache.remove(value.id());
        }
    }

    void remove(V value) {
        requireNonNull(value, "value must not be null");
        cache.remove(value.id());
    }

    void removeKey(K key) {
        requireNonNull(key, "key must not be null");
        cache.remove(key);
    }
}
