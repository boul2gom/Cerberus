package fr.boul2gom.cerberus.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.boul2gom.cerberus.api.AppType;
import fr.boul2gom.cerberus.api.player.IPlayer;
import fr.boul2gom.cerberus.common.CommonCerberusAPI;
import fr.boul2gom.cerberus.velocity.command.NetworkBanCommand;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "cerberus-velocity", name = "Cerberus service on Velocity", version = "1.0.0", authors = {"boul2gom"})
public class VelocityPlugin extends CommonCerberusAPI {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path data) {
        super(AppType.PROXY, data);
        this.server = server;
        this.logger = logger;

        logger.info("Cerberus service on Velocity has been loaded!");
    }

    @Subscribe
    public void onInitialization(ProxyInitializeEvent event) {
        this.logger.info("Registering event listeners...");

        this.logger.info("Registering commands...");
        final CommandManager commands = this.server.getCommandManager();
        final CommandMeta meta = commands.metaBuilder("network-ban").plugin(this).build();

        commands.register(meta, NetworkBanCommand.create(this.server));
        this.logger.info("Registered command 'network-ban' successfully !");
    }

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        final String uuid = event.getPlayer().getUniqueId().toString();
        this.logger.info("Checking login player, with ID: {}...", uuid);

        final IPlayer player = this.playerManager.get(uuid);
        if (player.isBanned()) {
            this.logger.info("The player {} is banned, kicking him...", uuid);
            final Component reason = Component.text("You are banned from this server.");

            event.setResult(ResultedEvent.ComponentResult.denied(reason));
        }
    }
}
