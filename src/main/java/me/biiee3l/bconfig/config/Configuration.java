package me.biiee3l.bconfig.config;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Configuration implements IConfiguration {
    protected Map<String, Object> config = new LinkedHashMap<>();

    @Override
    public void set(String key, Object value) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = config;

        for (int i = 0; i < keys.length - 1; i++) {
            current.putIfAbsent(keys[i], new LinkedHashMap<>());
            Object next = current.get(keys[i]);

            if (!(next instanceof Map)) {
                next = new LinkedHashMap<>();
                current.put(keys[i], next);
            }

            current = (Map<String, Object>) next;
        }

        current.put(keys[keys.length - 1], value);
    }

    @Override
    public void addDefault(String key, Object value) {
        if (get(key) != null) return;
        set(key, value);
    }

    @Override
    public Object get(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = config;

        for (int i = 0; i < keys.length - 1; i++) {
            Object value = current.get(keys[i]);
            if (!(value instanceof Map)) return null;
            current = (Map<String, Object>) value;
        }

        return current.get(keys[keys.length - 1]);
    }

    @Override
    public List<?> getList(String key) {
        Object value = get(key);
        return (value instanceof List<?>) ? (List<?>) value : Collections.emptyList();
    }

    public Set<String> keySet() {
        return config.keySet();
    }

    @Override
    public List<Integer> getIntegerList(String key) {
        Object value = get(key);
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .filter(o -> o instanceof Number)
                    .map(o -> ((Number) o).intValue())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Long> getLongList(String key) {
        Object value = get(key);
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .filter(o -> o instanceof Number)
                    .map(o -> ((Number) o).longValue())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getStringList(String key) {
        Object value = get(key);
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public long getLong(String key) {
        Object value = get(key);
        return (value instanceof Number) ? ((Number) value).longValue() : 0L;
    }

    public boolean getBoolean(String key) {
        Object value = get(key);
        return (value instanceof Boolean) && (Boolean) value;
    }

    @Override
    public String getString(String key) {
        Object value = get(key);
        return (value != null) ? value.toString() : null;
    }

    @Override
    public int getInt(String key) {
        Object value = get(key);
        return (value instanceof Number) ? ((Number) value).intValue() : 0;
    }

    public Set<String> getKeys(String path) {
        Object value = get(path);
        if (value instanceof Map<?, ?> map) {
            return new LinkedHashSet<>(map.keySet()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));
        }
        return Collections.emptySet();
    }

    public LinkedHashSet<String> getLinkedKeys(String path) {
        Object value = get(path);
        if (value instanceof Map<?, ?> map) {
            return map.keySet()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return new LinkedHashSet<>();
    }
}