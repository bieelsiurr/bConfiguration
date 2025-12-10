package me.biiee3l.bconfig.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a general-purpose configuration API.
 * All implementations must be thread-safe unless stated otherwise.
 */
public interface IConfiguration {

    /** Saves the configuration to disk or its persistent target. */
    void save();

    /** Loads the configuration from disk or its persistent source. */
    boolean load();

    // ---------------------------
    // Basic getters
    // ---------------------------

    /** Gets a raw object from the configuration. */
    Object get(String key);

    /** Gets a String value or null. */
    String getString(String key);

    /** Gets a boolean value (false if invalid). */
    boolean getBoolean(String key);

    /** Gets an int value (0 if invalid). */
    int getInt(String key);

    /** Gets a long value (0L if invalid). */
    long getLong(String key);

    /** Gets a double value (0.0 if invalid). */
    double getDouble(String key);

    // ---------------------------
    // Typed list getters
    // ---------------------------

    /** Gets a raw list or an empty list. */
    List<?> getList(String key);

    /** Gets a list of Strings. */
    List<String> getStringList(String key);

    /** Gets a list of Long values. */
    List<Long> getLongList(String key);

    /** Gets a list of Integer values. */
    List<Integer> getIntegerList(String key);

    /** Gets a typed list filtered by class. */
    <T> List<T> getList(String key, Class<T> clazz);

    // ---------------------------
    // Basic mutations
    // ---------------------------

    /** Sets a value at the given key. */
    void set(String key, Object value);

    /** Adds a default value only if the key does not exist. */
    void addDefault(String key, Object value);

    /** Removes a value at the given key. */
    Object remove(String key);

    /** Clears the entire configuration. */
    void clear();

    // ---------------------------
    // Existence
    // ---------------------------

    /** Returns true if a key exists in the configuration. */
    boolean contains(String key);

    // ---------------------------
    // Default / fallback getters
    // ---------------------------

    /**
     * Gets a value or returns the provided default if absent or not castable.
     */
    <T> T getOrDefault(String key, T defaultValue);

    // ---------------------------
    // Section handling
    // ---------------------------

    /**
     * Gets a deep-copy of a section as a Map, or an empty map if the section does not exist.
     */
    Map<String, Object> getSection(String key);

    /**
     * Gets (and optionally creates) a modifiable section.  
     * If create == false and the section doesn't exist, returns null.
     */
    Map<String, Object> getOrCreateSection(String key, boolean create);

    /**
     * Gets all keys inside a section.  
     * If the path is empty, returns top-level keys.
     */
    Set<String> getKeys(String key);

    // ---------------------------
    // Merge
    // ---------------------------

    /**
     * Merges the given map into the configuration.
     *
     * @param input     map to merge
     * @param overwrite true = replaces existing values,  
     *                  false = keeps existing ones
     */
    void merge(Map<String, Object> input, boolean overwrite);

    // ---------------------------
    // Snapshot
    // ---------------------------

    /**
     * Returns an immutable deep-copy snapshot of the whole configuration.
     */
    Map<String, Object> toMap();
}
