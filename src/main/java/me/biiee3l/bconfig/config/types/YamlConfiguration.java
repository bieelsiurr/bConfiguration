package me.biiee3l.bconfig.config.types;

import com.sun.corba.se.spi.ior.ObjectKey;
import me.biiee3l.bconfig.config.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

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
        try {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }

            Yaml yaml = new Yaml();
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                Map<String, Object> config = yaml.load(inputStream);
                if (config != null) {
                    this.config = config;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

}
