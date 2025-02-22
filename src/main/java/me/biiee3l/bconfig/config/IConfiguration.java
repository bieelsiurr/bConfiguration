package me.biiee3l.bconfig.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IConfiguration {

    void save();
    boolean load();

    Object get(String key);
    String getString(String key);
    List<String> getStringList(String key);
    List<Long> getLongList(String key);
    List<Integer> getIntegerList(String key);
    List<?> getList(String key);

    void set(String key, Object value);

    void addDefault(String key, Object value);

    boolean getBoolean(String key);
    int getInt(String number);
}
