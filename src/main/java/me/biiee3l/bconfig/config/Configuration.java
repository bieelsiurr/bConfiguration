package me.biiee3l.bconfig.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Configuration implements IConfiguration {

    protected Map<String, Object> config = new HashMap<>();

    @Override
    public void set(String key, Object value) {
        config.put(key, value);
    }

    @Override
    public Object get(String key) {
        return config.get(key);
    }

    @Override
    public List<?> getList(String key) {
        return (List<?>) get(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return (List<String>) get(key);
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
