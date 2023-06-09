package fr.boul2gom.cerberus.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.boul2gom.cerberus.api.AppType;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.References;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.configuration.IConfiguration;
import fr.boul2gom.cerberus.api.configuration.driver.CacheDriver;
import fr.boul2gom.cerberus.api.configuration.driver.DatabaseDriver;
import fr.boul2gom.cerberus.api.configuration.driver.ProtocolDriver;
import fr.boul2gom.cerberus.api.configuration.driver.ServiceDriver;
import fr.boul2gom.cerberus.api.database.IDatabaseManager;
import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.events.bus.IEventBus;
import fr.boul2gom.cerberus.api.i18n.ILanguageManager;
import fr.boul2gom.cerberus.api.player.IPlayerManager;
import fr.boul2gom.cerberus.api.protocol.IProtocolManager;
import fr.boul2gom.cerberus.api.protocol.Protocol;
import fr.boul2gom.cerberus.api.scheduler.IScheduler;
import fr.boul2gom.cerberus.api.utils.crypto.IEncoder;
import fr.boul2gom.cerberus.api.utils.types.Pair;
import fr.boul2gom.cerberus.common.configuration.ConfigurationLoader;
import fr.boul2gom.cerberus.common.configuration.driver.DriverLoader;
import fr.boul2gom.cerberus.common.events.bus.NetworkEventBus;
import fr.boul2gom.cerberus.common.i18n.LanguageManager;
import fr.boul2gom.cerberus.common.player.PlayerManager;
import fr.boul2gom.cerberus.common.protocol.cipher.PacketEncoder;
import fr.boul2gom.cerberus.common.scheduler.Scheduler;
import fr.boul2gom.cerberus.common.serializer.DataSerializer;
import fr.boul2gom.cerberus.common.utils.LoggerUtils;
import fr.boul2gom.cerberus.common.utils.crypto.CertUtils;
import fr.boul2gom.cerberus.common.utils.crypto.Encoder;

import java.nio.file.Path;

public class CommonCerberusAPI implements CerberusAPI {

    protected final Gson gson;
    protected final AppType type;
    protected final IConfiguration configuration;
    protected final ILanguageManager language;

    private final IEncoder encoder;

    protected final IScheduler scheduler;
    protected final IProtocolManager protocol;
    protected final IEventBus eventBus;

    protected final IDatabaseManager cache;
    protected final IDatabaseManager database;

    protected final IPlayerManager playerManager;

    protected CommonCerberusAPI(AppType type, Path data) {
        References.Provider.register(this);
        LoggerUtils.disable();

        this.type = type;
        this.configuration = ConfigurationLoader.load(data);
        this.language = new LanguageManager(this.configuration, data);

        this.gson = new GsonBuilder().registerTypeAdapter(Event.class, new DataSerializer<Event>())
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        final Pair.Double<Path> certificates = CertUtils.fetch(this.configuration, data);
        this.encoder = new Encoder(certificates);

        this.scheduler = new Scheduler();
        this.eventBus = new NetworkEventBus("Network");

        final ProtocolDriver protocolDriver = this.configuration.get(ConfigurationKey.PROTOCOL_DRIVER);
        final CacheDriver cacheDriver = this.configuration.get(ConfigurationKey.CACHE_DRIVER);
        final DatabaseDriver databaseDriver = this.configuration.get(ConfigurationKey.DATABASE_DRIVER);

        this.protocol = DriverLoader.protocol(protocolDriver, new PacketEncoder(this));
        this.cache = DriverLoader.cache(cacheDriver);
        this.database = DriverLoader.database(databaseDriver);

        this.playerManager = new PlayerManager(this);

        Protocol.initialize();
        this.protocol.start();
        this.eventBus.start();
    }

    @Override
    public Gson getGson() {
        return this.gson;
    }

    @Override
    public AppType getType() {
        return this.type;
    }

    @Override
    public IConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public ILanguageManager getLanguageManager() {
        return this.language;
    }

    @Override
    public IEncoder getEncoder() {
        return this.encoder;
    }

    @Override
    public IScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public IProtocolManager getProtocol() {
        return this.protocol;
    }

    @Override
    public IEventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public IDatabaseManager getCache() {
        return this.cache;
    }

    @Override
    public IDatabaseManager getDatabase() {
        return this.database;
    }

    @Override
    public IPlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
