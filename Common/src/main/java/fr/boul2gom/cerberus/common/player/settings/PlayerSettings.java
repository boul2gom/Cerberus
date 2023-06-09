package fr.boul2gom.cerberus.common.player.settings;

import fr.boul2gom.cerberus.api.player.settings.IPlayerSettings;

import java.util.Locale;

public class PlayerSettings implements IPlayerSettings {

    private final Locale language;

    public PlayerSettings(Locale language) {
        this.language = language;
    }

    @Override
    public Locale getLanguage() {
        return this.language;
    }
}
