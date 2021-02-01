package net.dirtcraft.discordlink.channels;

import net.dirtcraft.spongediscordlib.channels.GameChatChannel;
import net.dirtcraft.discordlink.commands.sources.ResponseScheduler;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dv8tion.jda.api.JDA;

public class GameChatChannelImpl extends DiscordChannelImpl implements GameChatChannel {
    GameChatChannelImpl(JDA jda, long channel) {
        super(jda, channel);
    }

    GameChatChannelImpl(JDA jda, long channel, boolean isPrivate) {
        super(jda, channel, isPrivate);
    }

    @Override
    public void sendPlayerMessage(String prefix, String playerName, String message) {
        final String output = PluginConfiguration.Format.serverToDiscord
                .replace("{prefix}", prefix)
                .replace("{username}", playerName)
                .replace("{message}", message);
        ResponseScheduler.submit(getChatResponder(), output);
    }
}
