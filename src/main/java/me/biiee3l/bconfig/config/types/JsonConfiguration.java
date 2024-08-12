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
    public void save() {
        try {
            Reader reader = new FileReader(file);
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = new Gson().fromJson(reader, type);

            data.putAll(config);

            Writer writer = new FileWriter(file);
            new Gson().toJson(data, writer);
            writer.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public boolean load() {
        try{
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            if(!file.exists()){
                file.createNewFile();
            }

            GsonBuilder gsonBuilder = new GsonBuilder();
            Reader reader = new FileReader(file);

            Type type = new TypeToken<Map<String, Object>>() {}.getType();

            Map<String, Object> config = gsonBuilder.create().fromJson(reader, type);
            if(config != null) this.config = config;
            return true;
        }catch (Exception e){
            e.printStackTrace(System.out);
            return false;
        }
    }
}
