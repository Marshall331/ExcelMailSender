package application.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.ConfigurationSave;

public class SaveManagement {

    public static void saveConf(ConfigurationSave config) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Saves/config.ser"))) {
            out.writeObject(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
