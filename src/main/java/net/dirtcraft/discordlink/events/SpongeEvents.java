package net.dirtcraft.discordlink.events;

import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.channels.GameChatChannelImpl;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

public class SpongeEvents {

    private final GameChatChannelImpl gameChatChannel;

    public SpongeEvents(DiscordLink main, Database storage) {
        this.gameChatChannel = main.getChannelManager()
                .getGameChat();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        Utility.setRoles(PlatformProvider.getPlayer(player));
        if (player.get(Keys.VANISH).orElse(false)) return;
        if (player.hasPlayedBefore()) {
            String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(PlatformProvider.getPlayer(player).getPrefix().orElse("")));

            gameChatChannel.sendMessage(PluginConfiguration.Format.playerJoin
                    .replace("{username}", player.getName())
                    .replace("{prefix}", prefix)
            );
        } else {
            MessageEmbed embed = Utility
                    .embedBuilder()
                    .setDescription(PluginConfiguration.Format.newPlayerJoin
                            .replace("{username}", player.getName()))
                    .build();

            gameChatChannel.sendMessage(embed);
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event, @Root Player player) {
        if (player.get(Keys.VANISH).orElse(false)) return;
        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));

        gameChatChannel.sendMessage(PluginConfiguration.Format.playerDisconnect
                        .replace("{username}", player.getName())
                        .replace("{prefix}", prefix)
                );
    }

}
