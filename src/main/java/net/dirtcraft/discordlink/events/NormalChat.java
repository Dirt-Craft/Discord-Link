package net.dirtcraft.discordlink.events;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.channels.GameChatChannelImpl;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class NormalChat {
    private static final String ADMIN_PERM = "discordlink.chat.notify";
    private GameChatChannelImpl gameChat;

    public NormalChat(DiscordLink link){
        gameChat = link
                .getChannelManager()
                .getGameChat();
    }

    @Listener
    public void onChat(MessageChannelEvent.Chat event, @Root Object cause) {
        if (!(cause instanceof Player)) return;
        Player player = (Player) cause;

        //boolean profane = event.getRawMessage().toPlain().contains("(?i)n+( *[1il])+( *[69g])+(( *[3e])+( *r)+|( *[a4])+)");
        //if (profane) notify(event.getRawMessage());

        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));
        String username = player.getName();
        String message = TextSerializers.FORMATTING_CODE.stripCodes(Utility.sanitiseMinecraftText(event.getRawMessage().toPlain()));

        gameChat.sendPlayerMessage(prefix, username, message);
    }

    private void notify(Text msg){
        Sponge.getServer().getOnlinePlayers().stream()
                .filter(player->player.hasPermission(ADMIN_PERM))
                .forEach(player -> {
                    player.sendMessage(Text.of("The following message has been censored:"));
                    player.sendMessage(msg);
                });
    }

}
