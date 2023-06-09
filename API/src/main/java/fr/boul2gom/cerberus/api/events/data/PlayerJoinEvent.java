package fr.boul2gom.cerberus.api.events.data;

import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.player.IPlayer;

public class PlayerJoinEvent extends Event {

    private final IPlayer player;

    public PlayerJoinEvent(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return this.player;
    }
}
