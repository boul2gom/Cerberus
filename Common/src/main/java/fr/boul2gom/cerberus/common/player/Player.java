package fr.boul2gom.cerberus.common.player;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.player.IPlayer;
import fr.boul2gom.cerberus.api.player.settings.IPlayerSettings;

import java.util.UUID;

public class Player implements IPlayer {

    private final UUID uuid;

    private String name;
    private String lastIp;
    private boolean banned;
    private IPlayerSettings settings;

    public Player(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLastIp() {
        return this.lastIp;
    }

    @Override
    public void setLastIp(String ip) {
        this.lastIp = ip;
    }

    @Override
    public boolean isBanned() {
        return this.banned;
    }

    @Override
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    @Override
    public IPlayerSettings getSettings() {
        return this.settings;
    }

    @Override
    public void setSettings(IPlayerSettings settings) {
        this.settings = settings;
    }

    @Override
    public void save() {
        CerberusAPI.get().getPlayerManager().save(this);
    }
}
