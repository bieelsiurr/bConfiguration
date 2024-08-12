package me.biiee3l.bconfig;

import me.biiee3l.bconfig.config.Configuration;
import me.biiee3l.bconfig.config.types.YamlConfiguration;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Create a new YamlConfiguration instance
        Configuration configuration = new YamlConfiguration(new File("filename.yml"));

        // Set this where you like to load the file
        if (!configuration.load()){
            System.err.println("Can't load configuration properly.");
        }

        // Start customizing your Configuration file
        configuration.set("key", "value");

        // And you can also get values too!
        String myFirstValue = configuration.getString("key_of_the_string");
        int myNumber = configuration.getInt("key_of_the_number");

        // Save the configuration file
        configuration.save();
    }
}