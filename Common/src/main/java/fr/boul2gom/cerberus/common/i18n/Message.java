package fr.boul2gom.cerberus.common.i18n;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.i18n.IMessage;
import fr.boul2gom.cerberus.api.player.IPlayer;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;

public class Message implements IMessage {

    final String key;
    final Map<Locale, String> values;

    public Message(String key) {
        this(key, new HashMap<>());
    }

    public Message(String key, Map<Locale, String> values) {
        this.key = key;
        this.values = values;
    }

    public void add(Locale locale, String value) {
        this.values.put(locale, value);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String get(Object... args) {
        final Locale locale = CerberusAPI.get().getLanguageManager().getLocale();

        return this.get(locale, args);
    }

    @Override
    public String get(IPlayer player, Object... args) {
        final Locale locale = player.getSettings().getLanguage();

        return this.get(locale, args);
    }

    @Override
    public String get(Locale locale, Object... args) {
        final String value = this.values.get(locale);

        try {
            return value == null ? "Unknown message:" + this.key : String.format(value, args);
        } catch (IllegalFormatException e) {
            return "Invalid format for message: " + this.key;
        }
    }
}
