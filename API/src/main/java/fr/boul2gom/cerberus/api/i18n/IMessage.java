package fr.boul2gom.cerberus.api.i18n;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.player.IPlayer;

import java.util.Locale;

public interface IMessage {

    String getKey();

    String get(Object... args);

    String get(IPlayer player, Object... args);

    String get(Locale locale, Object... args);

    public static String get(String key, Object... args) {
        final IMessage message = CerberusAPI.get().getLanguageManager().get(key);

        return message.get(args);
    }

    public static String get(String key, IPlayer player, Object... args) {
        final IMessage message = CerberusAPI.get().getLanguageManager().get(key);

        return message.get(player, args);
    }
}
