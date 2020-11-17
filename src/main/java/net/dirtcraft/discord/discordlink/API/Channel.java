package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Channel {

    private final long channel;
    private final boolean isPrivate;

    public Channel(long channel){
        this.channel = channel;
        this.isPrivate = false;
    }

    public Channel(long channel, boolean isPrivate){
        this.channel = channel;
        this.isPrivate = isPrivate;
    }

    public MessageChannel getChannel(){
        final JDA jda = DiscordLink.getJDA();
        return isPrivate? jda.getPrivateChannelById(channel) : jda.getTextChannelById(channel);
    }

    public long getId(){
        return channel;
    }

    public CompletableFuture<Message> sendMessage(MessageEmbed embed) {
        return getChannel().sendMessage(embed).submit();
    }

    public CompletableFuture<Message> sendMessage(String message) {
        return getChannel().sendMessage(message).submit();
    }

    public CompletableFuture<Message> sendMessage(String message, int delay){
        return sendMessage(message).whenComplete((msg, ex)-> msg.delete().queueAfter(delay, TimeUnit.SECONDS));
    }

    public CompletableFuture<Message> sendMessage(MessageEmbed message, int delay){
        return sendMessage(message).whenComplete((msg, ex)-> msg.delete().queueAfter(delay, TimeUnit.SECONDS));
    }

    public void sendMessage(MessageSource source, String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl())
                .build();
        sendMessage(embed);
    }

    public void sendMessage(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .build();
        sendMessage(embed);
    }

    public void sendLog(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("modpack: " + SpongeDiscordLib.getServerName())
                .build();
        sendMessage(embed);
    }

    public DiscordResponder getCommandResponder(MessageSource member, String command){
        return new DiscordResponder() {
            @Override
            public void sendDiscordResponse(String message) {
                if (message.length() > getCharLimit()) return;
                sendMessage(toEmbed(message));
            }

            private MessageEmbed toEmbed(String s){
                return Utility.embedBuilder().addField("__Command__ \"**/" + command.toLowerCase() + "**\" __Sent__", s, false)
                        .setFooter("Sent By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                        .build();
            }

            public int getCharLimit(){
                return 1024;
            }

            public boolean sanitise(){
                return true;
            }
        };
    }

    public DiscordResponder getChatResponder(){
        return new DiscordResponder() {
            @Override
            public void sendDiscordResponse(String message) {
                if (message.length() > getCharLimit()) return;
                sendMessage(message);
            }

            public int getCharLimit(){
                return 1996;
            }

            public boolean sanitise(){
                return false;
            }
        };
    }
}