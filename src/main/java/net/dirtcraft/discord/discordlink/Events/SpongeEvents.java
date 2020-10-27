package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;

public class SpongeEvents {

    private final DiscordLink main;
    private final Storage storage;

    public SpongeEvents(DiscordLink main, Storage storage) {
        this.main = main;
        this.storage = storage;
    }

    private final String modpack = SpongeDiscordLib.getServerName();

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        GameChat.sendMessage(
                Utility.embedBuilder()
                .setColor(Color.GREEN)
                .setDescription(PluginConfiguration.Format.serverStart
                        .replace("{modpack}", modpack)
                ).build());
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        GameChat.sendMessage(
                Utility.embedBuilder()
                        .setDescription(PluginConfiguration.Format.serverStop
                                .replace("{modpack}", modpack)
                        ).build());
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        if (player.get(Keys.VANISH).orElse(false)) return;
        if (player.hasPlayedBefore()) {
            String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));

            GameChat.sendMessage(PluginConfiguration.Format.playerJoin
                    .replace("{username}", player.getName())
                    .replace("{prefix}", prefix)
            );
        } else {
            MessageEmbed embed = Utility
                    .embedBuilder()
                    .setDescription(PluginConfiguration.Format.newPlayerJoin
                            .replace("{username}", player.getName()))
                    .build();

            GameChat.sendMessage(embed);
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event, @Root Player player) {
        if (player.get(Keys.VANISH).orElse(false)) return;
        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));

        GameChat.sendMessage(PluginConfiguration.Format.playerDisconnect
                        .replace("{username}", player.getName())
                        .replace("{prefix}", prefix)
                );
    }

}
