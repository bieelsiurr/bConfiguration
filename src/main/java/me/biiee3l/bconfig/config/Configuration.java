package me.biiee3l.bconfig.config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Thread-safe configuration backed by ConcurrentHashMap per-level.
 *
 * Characteristics:
 * - Lock-free at global level: uses ConcurrentHashMap and compute/computeIfAbsent for atomic per-key operations.
 * - Does NOT preserve insertion order (use LinkedHashMap version if you need ordering).
 * - Provides convenience getters, typed list getters, merge, snapshot (deep copy) and section helpers.
 *
 * Note: This implementation keeps nested maps as {@link ConcurrentHashMap} instances when created internally.
 */
public abstract class Configuration implements IConfiguration {

    /**
     * Root map (concurrent).
     */
    protected final ConcurrentHashMap<String, Object> root = new ConcurrentHashMap<>();

    private static final String EMPTY_KEY = "";

    // --------------------
    // Basic operations
    // --------------------

    /**
     * Split a path into keys by '.' separator. Empty or null returns single empty key.
     *
     * @param path key path like "a.b.c"
     * @return array of path components
     */
    protected static String[] splitPath(String path) {
        if (path == null || path.isEmpty()) {
            return new String[]{EMPTY_KEY};
        }
        return path.split("\\.");
    }

    /**
     * Put a value at the given path. Intermediate sections are created as ConcurrentHashMap if missing or if existing
     * value at that position is not a Map (the old value will be replaced by a map).
     *
     * This method is thread-safe for concurrent access across different branches and keys.
     *
     * @param key   dotted path
     * @param value value to set (may be null)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void set(String key, Object value) {
        Objects.requireNonNull(key, "key");
        String[] keys = splitPath(key);
        if (keys.length == 0) return;

        // Traverse and create intermediate maps atomically using compute
        ConcurrentHashMap<String, Object> current = root;
        for (int i = 0; i < keys.length - 1; i++) {
            final String k = keys[i];
            Object next = current.compute(k, (kk, existing) -> {
                if (existing instanceof ConcurrentHashMap) return existing;
                if (existing instanceof Map) {
                    // If it's some other Map implementation, convert to ConcurrentHashMap copy
                    ConcurrentHashMap<String, Object> chm = new ConcurrentHashMap<>();
                    chm.putAll((Map) existing);
                    return chm;
                }
                // replace non-map value with a new ConcurrentHashMap
                return new ConcurrentHashMap<String, Object>();
            });
            current = (ConcurrentHashMap<String, Object>) next;
        }
        // set last key (atomic at that map)
        current.put(keys[keys.length - 1], value);
    }

    /**
     * Get the raw object at the given dotted path. Returns null if not found or if traversal meets non-map before last key.
     *
     * @param key dotted path
     * @return stored object or null
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object get(String key) {
        Objects.requireNonNull(key, "key");
        String[] keys = splitPath(key);
        if (keys.length == 0) return null;

        Map<String, Object> current = root;
        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (!(next instanceof Map)) return null;
            current = (Map<String, Object>) next;
        }
        return current.get(keys[keys.length - 1]);
    }

    /**
     * Returns true if the given path exists (even if value is null).
     *
     * @param path dotted path
     * @return true if key exists
     */
    @SuppressWarnings("unchecked")
    public boolean contains(String path) {
        Objects.requireNonNull(path, "path");
        String[] keys = splitPath(path);
        if (keys.length == 0) return false;
        Map<String, Object> current = root;
        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (!(next instanceof Map)) return false;
            current = (Map<String, Object>) next;
        }
        return current.containsKey(keys[keys.length - 1]);
    }

    /**
     * Adds a default value only if the key is absent.
     *
     * @param key   dotted path
     * @param value default value
     */
    @Override
    public void addDefault(String key, Object value) {
        if (!contains(key)) {
            set(key, value);
        }
    }

    // --------------------
    // Typed getters & utilities
    // --------------------

    /**
     * Returns the value at key or defaultValue if absent.
     *
     * @param key          dotted path
     * @param defaultValue default to return when absent
     * @param <T>          expected type
     * @return value casted to T or defaultValue
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        Object v = get(key);
        if (v == null) return defaultValue;
        try {
            return (T) v;
        } catch (ClassCastException ex) {
            return defaultValue;
        }
    }

    /**
     * Get a String value or null.
     *
     * @param key dotted path
     * @return String or null
     */
    @Override
    public String getString(String key) {
        Object value = get(key);
        return (value != null) ? value.toString() : null;
    }

    /**
     * Get an int value. Returns 0 if not present or not numeric.
     *
     * @param key dotted path
     * @return int value or 0
     */
    @Override
    public int getInt(String key) {
        Object value = get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    /**
     * Get a long value. Returns 0L if not present or not numeric.
     *
     * @param key dotted path
     * @return long value or 0L
     */
    public long getLong(String key) {
        Object value = get(key);
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0L;
    }

    /**
     * Get a boolean value. Accepts Boolean or String (true/false). Returns false otherwise.
     *
     * @param key dotted path
     * @return boolean value or false
     */
    public boolean getBoolean(String key) {
        Object value = get(key);
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return Boolean.parseBoolean((String) value);
        return false;
    }

    /**
     * Get a double value. Returns 0.0 if not present or not numeric.
     *
     * @param key dotted path
     * @return double value or 0.0
     */
    public double getDouble(String key) {
        Object value = get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0.0D;
    }

    // --------------------
    // List getters
    // --------------------

    /**
     * Get raw list (copy) or empty list if absent or not a list.
     *
     * @param key dotted path
     * @return copy of list or empty list
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<?> getList(String key) {
        Object value = get(key);
        if (value instanceof List<?>) {
            return new ArrayList<>((List<?>) value);
        }
        return Collections.emptyList();
    }

    /**
     * Get typed list filtering by the provided class.
     *
     * @param key   dotted path
     * @param clazz element class
     * @param <T>   element type
     * @return list of T or empty list
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz) {
        Object value = get(key);
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .filter(Objects::nonNull)
                    .filter(clazz::isInstance)
                    .map(o -> (T) o)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getIntegerList(String key) {
        Object value = get(key);
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .filter(Objects::nonNull)
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
                    .filter(Objects::nonNull)
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

    // --------------------
    // Section helpers
    // --------------------

    /**
     * Get a snapshot map for the given section. Returns empty map if absent or not a map.
     *
     * @param path dotted path
     * @return immutable deep copy map of the section
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSection(String path) {
        Object val = get(path);
        if (val instanceof Map<?, ?>) {
            return deepCopyMap((Map<String, Object>) val);
        }
        return Collections.emptyMap();
    }

    /**
     * Get or create a section at path. If create==true then missing intermediate sections will be created.
     *
     * @param path   dotted path
     * @param create whether to create if absent
     * @return the Map for that section (ConcurrentHashMap) or null if not present and create==false
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getOrCreateSection(String path, boolean create) {
        Objects.requireNonNull(path, "path");
        String[] keys = splitPath(path);
        if (keys.length == 0) return null;

        ConcurrentHashMap<String, Object> current = root;
        for (String k : keys) {
            Object next = current.get(k);
            if (next == null) {
                if (!create) return null;
                ConcurrentHashMap<String, Object> created = new ConcurrentHashMap<>();
                Object prev = current.putIfAbsent(k, created);
                if (prev instanceof ConcurrentHashMap) {
                    current = (ConcurrentHashMap<String, Object>) prev;
                } else if (prev instanceof Map) {
                    // convert other Map to concurrent
                    ConcurrentHashMap<String, Object> chm = new ConcurrentHashMap<>();
                    chm.putAll((Map) prev);
                    if (current.replace(k, prev, chm)) {
                        current = chm;
                    } else {
                        Object now = current.get(k);
                        current = (now instanceof ConcurrentHashMap) ? (ConcurrentHashMap<String, Object>) now : chm;
                    }
                } else {
                    // previous was non-map
                    Object replaced = current.replace(k, prev, created) ? created : current.get(k);
                    current = (ConcurrentHashMap<String, Object>) replaced;
                }
            } else if (next instanceof ConcurrentHashMap) {
                current = (ConcurrentHashMap<String, Object>) next;
            } else if (next instanceof Map) {
                // wrap existing map into concurrent map
                ConcurrentHashMap<String, Object> chm = new ConcurrentHashMap<>();
                chm.putAll((Map) next);
                if (current.replace(k, next, chm)) {
                    current = chm;
                } else {
                    Object now = current.get(k);
                    current = (now instanceof ConcurrentHashMap) ? (ConcurrentHashMap<String, Object>) now : chm;
                }
            } else {
                // existing value is not a map
                if (!create) return null;
                ConcurrentHashMap<String, Object> created = new ConcurrentHashMap<>();
                if (current.replace(k, next, created)) {
                    current = created;
                } else {
                    Object now = current.get(k);
                    current = (now instanceof ConcurrentHashMap) ? (ConcurrentHashMap<String, Object>) now : created;
                }
            }
        }
        return current;
    }

    // --------------------
    // Mutations
    // --------------------

    /**
     * Remove a key at path. Returns previous value or null.
     *
     * @param path dotted path
     * @return removed value or null
     */
    @SuppressWarnings("unchecked")
    public Object remove(String path) {
        Objects.requireNonNull(path, "path");
        String[] keys = splitPath(path);
        if (keys.length == 0) return null;
        Map<String, Object> current = root;
        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (!(next instanceof Map)) return null;
            current = (Map<String, Object>) next;
        }
        return current.remove(keys[keys.length - 1]);
    }

    /**
     * Clears the whole configuration.
     */
    public void clear() {
        root.clear();
    }

    /**
     * Recursively merges another map into this configuration.
     * Use overwrite=true to replace existing values, false to keep existing values when conflict.
     *
     * @param other     source map to merge (may be nested)
     * @param overwrite whether to overwrite existing values
     */
    @SuppressWarnings("unchecked")
    public void merge(Map<String, Object> other, boolean overwrite) {
        Objects.requireNonNull(other, "other");
        // recursive function to merge into targetMap
        BiFunction<Map<String, Object>, Map<String, Object>, Void> mergeFn = new BiFunction<>() {
            @Override
            public Void apply(Map<String, Object> target, Map<String, Object> src) {
                for (Map.Entry<String, Object> e : src.entrySet()) {
                    String k = e.getKey();
                    Object v = e.getValue();
                    Object existing = target.get(k);
                    if (existing instanceof Map && v instanceof Map) {
                        apply((Map<String, Object>) existing, (Map<String, Object>) v);
                    } else {
                        if (!target.containsKey(k) || overwrite) {
                            if (v instanceof Map) {
                                // copy nested map into a ConcurrentHashMap
                                ConcurrentHashMap<String, Object> chm = new ConcurrentHashMap<>();
                                chm.putAll((Map) v);
                                target.put(k, chm);
                            } else if (v instanceof List<?>) {
                                target.put(k, new ArrayList<>((List<?>) v));
                            } else {
                                target.put(k, v);
                            }
                        }
                    }
                }
                return null;
            }
        };
        mergeFn.apply(root, other);
    }

    // --------------------
    // Views / snapshots
    // --------------------

    /**
     * Returns a deep-copy snapshot of the whole configuration as an immutable Map.
     * Nested maps and lists are copied to avoid exposing internal mutable structures.
     *
     * @return immutable snapshot
     */
    public Map<String, Object> toMap() {
        return deepCopyMap(root);
    }

    /**
     * Returns the top-level key set (copy).
     *
     * @return set of top-level keys
     */
    public Set<String> keySet() {
        return new HashSet<>(root.keySet());
    }

    /**
     * Returns the set of keys for the given path (if path refers to a map). Empty set otherwise.
     *
     * @param path dotted path
     * @return set of keys (copy)
     */
    @SuppressWarnings("unchecked")
    public Set<String> getKeys(String path) {
        if (path == null || path.isEmpty()) {
            return keySet();
        }
        Object v = get(path);
        if (v instanceof Map<?, ?>) {
            return ((Map<?, ?>) v).keySet().stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    // --------------------
    // Helpers: deep copy
    // --------------------

    /**
     * Deep-copies a map into an immutable HashMap. Nested maps and lists are copied recursively.
     *
     * @param source source map
     * @return unmodifiable deep copy
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Object> deepCopyMap(Map<String, Object> source) {
        Map<String, Object> copy = new HashMap<>();
        for (Map.Entry<String, Object> e : source.entrySet()) {
            Object v = e.getValue();
            if (v instanceof Map<?, ?>) {
                copy.put(e.getKey(), deepCopyMap((Map<String, Object>) v));
            } else if (v instanceof List<?>) {
                List<?> list = (List<?>) v;
                List<Object> copyList = new ArrayList<>(list.size());
                for (Object item : list) {
                    if (item instanceof Map<?, ?>) {
                        copyList.add(deepCopyMap((Map<String, Object>) item));
                    } else if (item instanceof List<?>) {
                        // shallow copy nested lists (you can extend if you need deep nested list copy)
                        copyList.add(new ArrayList<>((List<?>) item));
                    } else {
                        copyList.add(item);
                    }
                }
                copy.put(e.getKey(), Collections.unmodifiableList(copyList));
            } else {
                copy.put(e.getKey(), v);
            }
        }
        return Collections.unmodifiableMap(copy);
    }
}

