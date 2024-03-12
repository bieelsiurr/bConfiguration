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
    public void save() throws IOException {
        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter(file);
        yaml.dump(config, writer);
    }

    @Override
    public void load() throws IOException {
        file.getParentFile().mkdirs();
        if(!file.exists()){
            file.createNewFile();
        }

        Yaml yaml = new Yaml();
        InputStream targetStream = new FileInputStream(file);
        config = yaml.load(targetStream);
    }
}
