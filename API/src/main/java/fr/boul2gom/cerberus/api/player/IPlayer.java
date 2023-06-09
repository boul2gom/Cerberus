package fr.boul2gom.cerberus.api.player;

import fr.boul2gom.cerberus.api.database.Storable;
import fr.boul2gom.cerberus.api.player.settings.IPlayerSettings;

import java.util.UUID;

public interface IPlayer extends Storable {

    UUID getUniqueId();

    String getName();
    void setName(String name);

    String getLastIp();
    void setLastIp(String ip);

    boolean isBanned();
    void setBanned(boolean banned);

    IPlayerSettings getSettings();
    void setSettings(IPlayerSettings settings);
}
