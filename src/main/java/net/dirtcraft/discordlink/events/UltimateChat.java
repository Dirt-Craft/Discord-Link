package net.dirtcraft.discordlink.events;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.channels.GameChatChannelImpl;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.serializer.TextSerializers;

public class UltimateChat {

    private GameChatChannelImpl gameChatChannel;

    public UltimateChat(DiscordLink discordLink){
        gameChatChannel = discordLink
                .getChannelManager()
                .getGameChat();
    }

    @Listener
    public void onSendChannelMessage(SendChannelMessageEvent event, @Root Object cause) {
        if (event.getChannel() == null) return;
        if (!event.getChannel().getName().equalsIgnoreCase("global")) return;
        if (!(cause instanceof Player)) return;
        Player player = (Player) cause;


        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));
        String username = player.getName();
        String message = TextSerializers.FORMATTING_CODE.stripCodes(Utility.sanitiseMinecraftText(event.getMessage().toPlain()));
        gameChatChannel.sendPlayerMessage(prefix, username, message);
    }

}
