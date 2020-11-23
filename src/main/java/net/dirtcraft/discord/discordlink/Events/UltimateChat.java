package net.dirtcraft.discord.discordlink.Events;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.serializer.TextSerializers;

public class UltimateChat {

    @Listener
    public void onSendChannelMessage(SendChannelMessageEvent event, @Root Object cause) {
        if (event.getChannel() == null) return;
        if (!event.getChannel().getName().equalsIgnoreCase("global")) return;
        if (!(cause instanceof Player)) return;
        Player player = (Player) cause;


        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));
        String username = player.getName();
        String message = TextSerializers.FORMATTING_CODE.stripCodes(Utility.sanitiseMinecraftText(event.getMessage().toPlain()));

        Channels.sendPlayerMessage(prefix, username, message);
    }

}
