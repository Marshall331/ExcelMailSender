package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utility class for managing configuration serialization and deserialization.
 */
public class SaveManagement {

    /**
     * Saves the configuration to a serialized file, create the file it not already
     * exists.
     *
     * @param config The ConfigurationSave object to be saved.
     */
    public static void saveConf(ConfigurationSave config) {
        File configFile = new File("config.ser");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(configFile));
            out.writeObject(config);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the saved configuration from a serialized file if the file exists,
     * create it if not.
     *
     * @return The loaded ConfigurationSave object. Returns null if no configuration
     *         is found.
     */
    public static ConfigurationSave loadConf() {
        ConfigurationSave config = null;
        File configFile = new File("config.ser");
        try {
            if (!configFile.exists()) {
                // Create a new default configuration if the file doesn't exist
                config = new ConfigurationSave();
                configFile.createNewFile();
                saveConf(config); // Save the default configuration
                return config;
            }
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(configFile));
            config = (ConfigurationSave) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return config;
    }
}