package fr.boul2gom.cerberus.api.player;

public interface IPlayerManager {

    IPlayer get(String uuid);

    void save(IPlayer player);
}
