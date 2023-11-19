package model;

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
     * Saves the configuration to a serialized file.
     *
     * @param config The ConfigurationSave object to be saved.
     */
    public static void saveConf(ConfigurationSave config) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Saves/config.ser"))) {
            out.writeObject(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the saved configuration from a serialized file.
     *
     * @return The loaded ConfigurationSave object.
     */
    public static ConfigurationSave loadConf() {
        ConfigurationSave config = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("Saves/config.ser"))) {
            config = (ConfigurationSave) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return config;
    }
}