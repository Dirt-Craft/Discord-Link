package net.dirtcraft.discord.discordlink.API;


import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;

public class MessageSource extends GuildMember implements DiscordResponder {
    private final Message message;
    private final Channel source;
    public MessageSource(MessageReceivedEvent event){
        super(Channels.getGuild().retrieveMember(event.getAuthor()).complete());
        this.message = event.getMessage();
        this.source = new Channel(event.getChannel().getIdLong(), event.getMessage().isFromType(ChannelType.PRIVATE));
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        sendMessage("``" + message + "``");
    }

    @Override
    public int getCharLimit(){
        return 1996;
    }

    @Override
    public boolean sanitise(){
        return false;
    }

    public Channel getChannel(){
        return source;
    }

    public ConsoleSource getCommandSource(String command){
        DiscordResponder responder = source.getCommandResponder(this, command);
        return DiscordResponder.getSender(responder);
    }

    public boolean isPrivateMessage(){
        return source.getChannel().getType() == ChannelType.PRIVATE;
    }

    public void sendCommandResponse(String message){
        source.sendMessage(message);
    }

    public void sendCommandResponse(MessageEmbed message){
        source.sendMessage(message);
    }

    public void sendCommandResponse(String message, int delay){
        source.sendMessage(message, delay);
    }

    public void sendCommandResponse(MessageEmbed message, int delay){
        source.sendMessage(message, delay);
    }

    public void sendCommandResponse(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("Requested By: " + getUser().getAsTag(), getUser().getAvatarUrl())
                .build();
        sendCommandResponse(embed);
    }

    public void sendCommandResponse(String header, String message, int duration) {
        header = header == null? "" : header;
        message = message == null? "" : message;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter(getUser().getAsTag(), getUser().getAvatarUrl())
                .build();
        sendCommandResponse(embed, duration);
    }

    public void sendPrivateMessage(MessageEmbed message){
        getUser().openPrivateChannel().queue(dm -> dm.sendMessage(message).queue());
    }

    public void sendPrivateFile(File file){
        getUser().openPrivateChannel()
                .flatMap(a->a.sendFile(file))
                .submit();

    }
}
