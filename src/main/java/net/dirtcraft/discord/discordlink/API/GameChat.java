package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class GameChat {

    private static final TextChannel channel = DiscordLink.getJDA().getTextChannelById(SpongeDiscordLib.getGamechatChannelID());

    public static TextChannel getChannel(){
        return channel;
    }

    public static Guild getGuild(){
        return channel.getGuild();
    }

    public static void sendMessage(MessageEmbed embed) {
        channel.sendMessage(embed).queue();
    }


    public static void sendMessage(String message) {
        channel.sendMessage(message).queue();
    }

    public static void sendMessage(String message, int duration) {
        channel.sendMessage(message).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));
    }

    public static void sendMessage(MessageEmbed embed, int duration) {
        channel.sendMessage(embed).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));

    }

    public static void sendEmbed(String header, String message, int duration) {
        header = header == null? "" : header;
        message = message == null? "" : message;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                //.setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                .build();
        channel.sendMessage(embed).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));
    }

    public static void sendEmbed(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                //.setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                .build();
        channel.sendMessage(embed).queue();
    }

    public static void sendPlayerMessage(String prefix, String playerName, String message) {
        final String output = PluginConfiguration.Format.serverToDiscord
                .replace("{prefix}", prefix)
                .replace("{username}", playerName)
                .replace("{message}", message);
        channel.sendMessage(output).queue();
    }
}
