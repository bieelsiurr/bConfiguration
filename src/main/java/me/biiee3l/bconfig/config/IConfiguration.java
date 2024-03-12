package me.biiee3l.bconfig.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IConfiguration {

    void save() throws IOException;
    void load() throws IOException;

    Object get(String key);
    String getString(String key);
    List<String> getStringList(String key);
    List<?> getList(String key);

    void set(String key, Object value);
}
