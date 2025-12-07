package me.biiee3l.bconfig.config.types;

import me.biiee3l.bconfig.config.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class YamlConfiguration extends Configuration {

    private final File file;

    public YamlConfiguration(File file) {
        this.file = file;
    }

    @Override
    public void save() {
        try {
            if (file.getParentFile() != null) file.getParentFile().mkdirs();

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setIndent(2);
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);

            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
                yaml.dump(config, writer);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public boolean load() {
        try {
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();

            Yaml yaml = new Yaml();
            try (InputStream inputStream = Files.newInputStream(file.toPath());
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                Map<String, Object> loaded = yaml.load(reader);
                this.config = (loaded != null) ? loaded : new HashMap<>();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }
}
