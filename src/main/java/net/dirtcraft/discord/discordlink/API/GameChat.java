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

    private static final long channel = Long.parseLong(SpongeDiscordLib.getGamechatChannelID());
    private static final long guild = getChannel().getGuild().getIdLong();

    public static TextChannel getChannel(){
        return DiscordLink.getJDA().getTextChannelById(channel);
    }

    public static Guild getGuild(){
        return DiscordLink.getJDA().getGuildById(guild);
    }

    public static void sendMessage(MessageEmbed embed) {
        getChannel().sendMessage(embed).queue();
    }


    public static void sendMessage(String message) {
        getChannel().sendMessage(message).queue();
    }

    public static void sendMessage(String message, int duration) {
        getChannel().sendMessage(message).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));
    }

    public static void sendMessage(MessageEmbed embed, int duration) {
        getChannel().sendMessage(embed).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));

    }

    public static void sendEmbed(String header, String message, int duration) {
        header = header == null? "" : header;
        message = message == null? "" : message;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                //.setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                .build();
        getChannel().sendMessage(embed).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));
    }

    public static void sendEmbed(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                //.setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                .build();
        getChannel().sendMessage(embed).queue();
    }

    public static void sendPlayerMessage(String prefix, String playerName, String message) {
        final String output = PluginConfiguration.Format.serverToDiscord
                .replace("{prefix}", prefix)
                .replace("{username}", playerName)
                .replace("{message}", message);
        getChannel().sendMessage(output).queue();
    }
}
