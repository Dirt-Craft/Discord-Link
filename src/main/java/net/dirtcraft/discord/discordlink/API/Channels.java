package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.Sources.ResponseScheduler;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class Channels {
    private static final JDA jda = DiscordLink.getJDA();
    private static final Channel logChannel = new Channel(Long.parseLong(PluginConfiguration.Main.serverLogChannelID));
    private static final Channel defaultChannel = new Channel(PluginConfiguration.Main.defaultChannelID);
    private static final long guild = getDefaultChannel() != null ? getDefaultChannel().getGuild().getIdLong() : -1;

    public static TextChannel getDefaultChannel(){
        return jda.getTextChannelById(defaultChannel.getId());
    }

    public static Channel getDefaultChat(){
        return defaultChannel;
    }

    public static Channel getLogChannel() {
        return logChannel;
    }

    public static boolean isGamechat(MessageChannel channel){
        return channel.getIdLong() == Channels.defaultChannel.getId();
    }

    public static Guild getGuild(){
        return DiscordLink.getJDA().getGuildById(guild);
    }

    public static void sendPlayerMessage(String prefix, String playerName, String message) {
        final Channel gamechat = getDefaultChat();
        final String output = PluginConfiguration.Format.serverToDiscord
                .replace("{prefix}", prefix)
                .replace("{username}", playerName)
                .replace("{message}", message);
        ResponseScheduler.submit(gamechat.getChatResponder(), output);
    }
}
