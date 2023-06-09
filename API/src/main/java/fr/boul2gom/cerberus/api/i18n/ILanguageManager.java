package fr.boul2gom.cerberus.api.i18n;

import fr.boul2gom.cerberus.api.player.IPlayer;

import java.util.Locale;

public interface ILanguageManager {

    Locale getLocale();

    IMessage get(String key);

    String get(String key, Object... args);

    String get(IPlayer player, String key, Object... args);

    String get(Locale locale, String key, Object... args);
}
