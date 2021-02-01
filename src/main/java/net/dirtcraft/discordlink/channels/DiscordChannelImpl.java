package net.dirtcraft.discordlink.channels;

import net.dirtcraft.spongediscordlib.channels.DiscordChannel;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.commands.sources.DiscordResponder;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DiscordChannelImpl implements DiscordChannel {
    private final JDA jda;
    private final long channel;
    private final boolean isPrivate;

    DiscordChannelImpl(JDA jda, long channel){
        this.jda = jda;
        this.channel = channel;
        this.isPrivate = false;
    }

    DiscordChannelImpl(JDA jda, long channel, boolean isPrivate){
        this.jda = jda;
        this.channel = channel;
        this.isPrivate = isPrivate;
    }

    public MessageChannel getChannel(){
        return isPrivate? jda.getPrivateChannelById(channel) : jda.getTextChannelById(channel);
    }

    public long getId(){
        return channel;
    }

    @Override
    public CompletableFuture<Message> sendMessage(MessageEmbed embed) {
        return getChannel().sendMessage(embed).submit();
    }

    @Override
    public CompletableFuture<Message> sendMessage(String message) {
        return getChannel().sendMessage(message).submit();
    }

    @Override
    public CompletableFuture<Message> sendMessage(String message, int delay){
        return sendMessage(message).whenComplete((msg, ex)-> msg.delete().queueAfter(delay, TimeUnit.SECONDS));
    }

    @Override
    public CompletableFuture<Message> sendMessage(MessageEmbed message, int delay){
        return sendMessage(message).whenComplete((msg, ex)-> msg.delete().queueAfter(delay, TimeUnit.SECONDS));
    }

    @Override
    public void sendMessage(DiscordMember source, String header, String message) {
        if (!(source instanceof GuildMember)) return;
        GuildMember member = (GuildMember) source;
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                .build();
        sendMessage(embed);
    }

    @Override
    public void sendMessage(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .build();
        sendMessage(embed);
    }

    public DiscordResponder getCommandResponder(MessageSourceImpl member, String command){
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