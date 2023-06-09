package fr.boul2gom.cerberus.common.i18n;

import com.google.gson.stream.JsonReader;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.References;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.configuration.IConfiguration;
import fr.boul2gom.cerberus.api.i18n.ILanguageManager;
import fr.boul2gom.cerberus.api.i18n.IMessage;
import fr.boul2gom.cerberus.api.player.IPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageManager implements ILanguageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ILanguageManager.class);

    private final Locale locale;
    private final Map<String, IMessage> messages;

    public LanguageManager(IConfiguration configuration, Path data) {
        this.locale = new Locale(configuration.get(ConfigurationKey.LANGUAGE));
        this.messages = new HashMap<>();

        final Path directory = data.resolve(References.LANGUAGE_PATH);

        try {
            Files.walk(directory).forEach(file -> {
                if (file.toFile().isDirectory()) return;
                if (!file.toFile().getName().endsWith(".json")) return;

                final String name = file.getFileName().toString();
                final Locale locale = new Locale(name.replace(".json", ""));

                try {
                    final JsonReader reader = new JsonReader(new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8));
                    final Map<String, String> keys = CerberusAPI.get().getGson().fromJson(reader, Map.class);

                    for (final Map.Entry<String, String> entry : keys.entrySet()) {
                        final String key = entry.getKey();
                        final String value = entry.getValue();

                        final Message message = (Message) this.messages.getOrDefault(key, new Message(key));
                        message.add(locale, value);

                        this.messages.put(key, message);
                    }

                    LOGGER.info("Loaded language file {}", name);
                } catch (IOException e) {
                    LOGGER.error("Failed to load language file {}", name, e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to load language files", e);
        }
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public IMessage get(String key) {
        return this.messages.get(key);
    }

    @Override
    public String get(String key, Object... args) {
        return this.get(this.locale, key, args);
    }

    @Override
    public String get(IPlayer player, String key, Object... args) {
        final Locale locale = player.getSettings().getLanguage();

        return this.get(locale, key, args);
    }

    @Override
    public String get(Locale locale, String key, Object... args) {
        final IMessage message = this.messages.get(key);

        if (message == null) {
            return null;
        }

        return message.get(locale, args);
    }
}
