package me.biiee3l.bconfig.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IConfiguration {

    void save();
    void load();

    Object get(String key);
    String getString(String key);
    List<String> getStringList(String key);
    List<?> getList(String key);

    void set(String key, Object value);

    void addDefault(String key, Object value);

    int getInt(String number);
}
