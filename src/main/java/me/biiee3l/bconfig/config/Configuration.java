package me.biiee3l.bconfig.config;

import java.util.*;

public abstract class Configuration implements IConfiguration {

    protected Map<String, Object> config = new HashMap<>();

    @Override
    public void set(String key, Object value) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = config;

        for (int i = 0; i < keys.length - 1; i++) {
            if (!current.containsKey(keys[i])) {
                current.put(keys[i], new HashMap<>());
            }
            current = (Map<String, Object>) current.get(keys[i]);
        }
        current.put(keys[keys.length - 1], value);
    }

    @Override
    public void addDefault(String key, Object value) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = config;

        for (int i = 0; i < keys.length - 1; i++) {
            if (!current.containsKey(keys[i])) {
                current.put(keys[i], new HashMap<>());
            }
            current = (Map<String, Object>) current.get(keys[i]);
        }
        if(current.containsKey(keys[keys.length - 1])) return;
        current.put(keys[keys.length - 1], value);
    }

    @Override
    public Object get(String key) {
        return config.get(key);
    }

    @Override
    public List<?> getList(String key) {
        return (List<?>) get(key);
    }

    public Set<String> keySet(){
        return config.keySet();
    }

    @Override
    public List<Integer> getIntegerList(String key) {
        return (List<Integer>) get(key);
    }

    @Override
    public List<Long> getLongList(String key) {
        return (List<Long>) get(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return (List<String>) get(key);
    }

    public long getLong(String key) {
        return (long) get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) get(key);
    }

    @Override
    public String getString(String key) {
        return (String) get(key);
    }

    @Override
    public int getInt(String key) {
        return (int) get(key);
    }
}
