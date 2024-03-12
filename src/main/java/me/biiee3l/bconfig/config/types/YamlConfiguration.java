package me.biiee3l.bconfig.config.types;

import me.biiee3l.bconfig.config.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class YamlConfiguration extends Configuration {

    private final File file;

    public YamlConfiguration(File file){
        this.file = file;
    }

    @Override
    public void save() {
        try{
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(file);
            yaml.dump(config, writer);
        }catch (Exception e){
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void load() {
        try{
            file.getParentFile().mkdirs();
            if(!file.exists()){
                file.createNewFile();
            }

            Yaml yaml = new Yaml();
            InputStream targetStream = new FileInputStream(file);
            config = yaml.load(targetStream);
        }catch (Exception e){
            e.printStackTrace(System.out);
        }
    }
}
