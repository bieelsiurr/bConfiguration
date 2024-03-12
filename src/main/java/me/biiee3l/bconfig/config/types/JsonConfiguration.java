package me.biiee3l.bconfig.config.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.biiee3l.bconfig.config.Configuration;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonConfiguration extends Configuration {

    private final File file;

    public JsonConfiguration(File file){
        this.file = file;
    }

    @Override
    public void save() throws IOException {
        Reader reader = new FileReader(file);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();

        Gson gson = new Gson().fromJson(reader, type);
        gson.toJson(config);
    }

    public void load() throws IOException {
        file.getParentFile().mkdirs();
        if(!file.exists()){
            file.createNewFile();
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Reader reader = new FileReader(file);

        Type type = new TypeToken<Map<String, Object>>() {}.getType();

        config = gsonBuilder.create().fromJson(reader, type);
    }
}
