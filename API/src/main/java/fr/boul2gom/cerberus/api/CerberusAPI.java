package fr.boul2gom.cerberus.api;

import com.google.gson.Gson;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.configuration.IConfiguration;
import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.events.bus.IEventBus;
import fr.boul2gom.cerberus.api.i18n.ILanguageManager;
import fr.boul2gom.cerberus.api.player.IPlayerManager;
import fr.boul2gom.cerberus.api.protocol.IProtocolManager;
import fr.boul2gom.cerberus.api.scheduler.IScheduler;
import fr.boul2gom.cerberus.api.utils.crypto.IEncoder;

public interface CerberusAPI {

    Gson getGson();
    AppType getType();
    IConfiguration getConfiguration();
    ILanguageManager getLanguageManager();

    IEncoder getEncoder();

    IScheduler getScheduler();
    IProtocolManager getProtocol();
    IEventBus getEventBus();

    IDatabaseManager getCache();
    IDatabaseManager getDatabase();

    IPlayerManager getPlayerManager();

    public static CerberusAPI get() {
        return References.Provider.get();
    }

    public static <T> T config(ConfigurationKey key) {
        return CerberusAPI.get().getConfiguration().get(key);
    }
}
