package fr.boul2gom.cerberus.common.player;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.player.IPlayer;
import fr.boul2gom.cerberus.api.player.IPlayerManager;

import java.util.UUID;

public class PlayerManager implements IPlayerManager {

    private final IDatabaseManager cache;
    private final IDatabaseManager database;

    public PlayerManager(CerberusAPI api) {
        this.cache = api.getCache();
        this.database = api.getDatabase();
    }

    @Override
    public IPlayer get(String uuid) {
        final IPlayer cached = this.cache.fetch("players", uuid, Player.class);
        if (cached != null) {
            return cached;
        }

        final IPlayer fetched = this.database.fetch("players", uuid, Player.class);
        if (fetched != null) {
            this.cache.save("players", uuid, fetched);
            return fetched;
        }

        final IPlayer player = new Player(UUID.fromString(uuid));
        this.save(player);

        return player;
    }

    @Override
    public void save(IPlayer player) {
        final String uuid = player.getUniqueId().toString();

        this.cache.save("players", uuid, player);
        this.database.save("players", uuid, player);
    }
}
