package fr.boul2gom.cerberus.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.player.IPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class NetworkBanCommand {

    public static BrigadierCommand create(ProxyServer server) {
        return new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("network-ban")
                .executes(context -> {
                    final CommandSource source = context.getSource();

                    Component message = Component.text("You must provide the player name!", NamedTextColor.RED);
                    source.sendMessage(message);

                    return 0;
                }).then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (final Player player : server.getAllPlayers()) {
                                final String username = player.getUsername();
                                final Component message = MiniMessage.miniMessage().deserialize("<rainbow>" + username);

                                builder.suggest(username, VelocityBrigadierMessage.tooltip(message));
                            }

                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            final String username = context.getArgument("username", String.class);
                            final Player player = server.getPlayer(username).orElse(null);
                            if (player == null) {
                                return 0;
                            }

                            final String uuid = player.getUniqueId().toString();
                            final IPlayer account = CerberusAPI.get().getPlayerManager().get(uuid);

                            account.setBanned(!account.isBanned());
                            account.save();

                            final CommandSource source = context.getSource();
                            source.sendMessage(Component.text("Player " + username + " has been banned!"));

                            player.disconnect(Component.text("You have been banned from this server!"));

                            return Command.SINGLE_SUCCESS;
                        })
                ).build()
        );
    }
}
