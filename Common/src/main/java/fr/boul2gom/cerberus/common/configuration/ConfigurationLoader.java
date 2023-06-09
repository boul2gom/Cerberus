package fr.boul2gom.cerberus.common.configuration;

import fr.boul2gom.cerberus.api.References;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.configuration.IConfiguration;
import fr.boul2gom.cerberus.api.utils.types.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoader.class);

    public static void save(Path config) throws RuntimeException {
        try {
            Files.createDirectories(config.getParent());
            Files.createFile(config);

            final FileWriter writer = new FileWriter(config.toFile());
            final PrintWriter printer = new PrintWriter(writer);

            Pair.Double<String> previous = null;
            for (final ConfigurationKey key : ConfigurationKey.values()) {
                if (key.isEnvironment()) {
                    continue;
                }

                final String category = key.getKey().split("\\.")[0];
                final String value = key.getKey().split("\\.")[1];
                if (previous != null) {
                    final boolean sameCategory = previous.first().equals(category);
                    final boolean sameValue = previous.second().equals(value);

                    if (!sameCategory && !sameValue) {
                        printer.println();
                    }
                }

                printer.println("# " + key.getDescription());

                if (key.getType() == String.class) {
                    printer.println(key.getKey() + ": \"" + key.getValue() + "\"");
                } else {
                    printer.println(key.getKey() + ": " + key.getValue());
                }

                previous = new Pair.Double<>(category, value);
            }

            printer.close();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save configuration file.", e);
        }
    }

    public static IConfiguration load(Path data) throws RuntimeException {
        final Path config = data.resolve(References.CONFIGURATION_PATH);

        if (Files.exists(config)) {
            LOGGER.info("Configuration file found, loading...");
            final Map<String, Object> configuration = new HashMap<>();

            try (Stream<String> stream = Files.lines(config)) {
                stream.filter(line -> !line.isEmpty()).filter(line -> !line.startsWith("#")).forEach(line -> {
                    final String[] split = line.split(":");
                    final String key = split[0].trim().replace("\"", "");
                    final String value = split[1].trim().replace("\"", "");

                    final ConfigurationKey parsed = ConfigurationKey.parse(key);
                    if (parsed == null) {
                        LOGGER.warn("Unknown configuration key: " + key);
                        return;
                    }

                    configuration.put(key, parsed.getParser().apply(value));
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to load configuration file.", e);
            }

            for (final ConfigurationKey key : ConfigurationKey.values()) {
                if (key.isEnvironment()) {
                    final String env = System.getenv(key.getKey());
                    final Object value = env == null ? key.getValue() : env;

                    configuration.put(key.getKey(), value);
                    continue;
                }

                if (!configuration.containsKey(key.getKey())) {
                    LOGGER.warn("Missing configuration key: " + key.getKey());
                }
            }

            return new Configuration(configuration);
        }

        LOGGER.info("Configuration file not found, creating...");
        ConfigurationLoader.save(config);

        LOGGER.error("Please edit the configuration file and restart the application.");
        System.exit(0);
        return null;
    }
}
