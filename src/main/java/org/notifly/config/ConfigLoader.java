package org.notifly.config;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class ConfigLoader {

    // Path to the YAML configuration file in resources folder
    private static final String CONFIG_PATH = "notifly.yaml";

    // This map will hold all the configuration data loaded from the YAML file
    private static Map<String, Object> config;

    // Static block executes once when the class is first loaded
    static {
        // Create a YAML parser
        Yaml yaml = new Yaml();
        try (InputStream inputStream = ConfigLoader.class.getClassLoader()
                .getResourceAsStream(CONFIG_PATH)) { // Load the YAML file from resources

            if (inputStream == null) {
                // If the file is not found, throw an exception
                throw new RuntimeException("Config file not found!");
            }

            // Parse the YAML file into a Map
            config = yaml.load(inputStream);

        } catch (Exception e) {
            // Print any errors that occur during loading/parsing
            e.printStackTrace();
        }
    }

    // This method returns the bot token stored under the "notifly" section in YAML
    @SuppressWarnings("unchecked")
    public static String getToken() {
        // Get the "notifly" section from the YAML map
        Map<String, Object> botConfig = (Map<String, Object>) config.get("notifly");
        // Return the "token" value
        return (String) botConfig.get("token");
    }
}
