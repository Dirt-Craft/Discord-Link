package net.dirtcraft.discordlink.users;


import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.commands.sources.DiscordResponder;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.channels.DiscordChannelImpl;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;

public class MessageSourceImpl extends GuildMember implements DiscordResponder, MessageSource {
    private final Message message;
    private final DiscordChannelImpl source;

    public MessageSourceImpl(Database storage, Member author, DiscordChannelImpl discordChannel, RoleManagerImpl roleManager, MessageReceivedEvent event){
        super(storage,roleManager, author);
        this.message = event.getMessage();
        this.source = discordChannel;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        sendPrivateMessage("``" + message + "``");
    }

    @Override
    public int getCharLimit(){
        return 1996;
    }

    @Override
    public boolean sanitise(){
        return false;
    }

    @Override
    public DiscordChannelImpl getChannel(){
        return source;
    }

    @Override
    public ConsoleSource getCommandSource(String command){
        DiscordResponder responder = source.getCommandResponder(this, command);
        return DiscordResponder.getSender(responder);
    }

    @Override
    public boolean isPrivateMessage(){
        return source.getChannel().getType() == ChannelType.PRIVATE;
    }

    @Override
    public void sendCommandResponse(String message){
        source.sendMessage(message);
    }

    @Override
    public void sendCommandResponse(MessageEmbed message){
        source.sendMessage(message);
    }

    @Override
    public void sendCommandResponse(String message, int delay){
        source.sendMessage(message, delay);
    }

    @Override
    public void sendCommandResponse(MessageEmbed message, int delay){
        source.sendMessage(message, delay);
    }

    @Override
    public void sendCommandResponse(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("Requested By: " + getUser().getAsTag(), getUser().getAvatarUrl())
                .build();
        sendCommandResponse(embed);
    }

    @Override
    public void sendCommandResponse(String header, String message, int duration) {
        header = header == null? "" : header;
        message = message == null? "" : message;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter(getUser().getAsTag(), getUser().getAvatarUrl())
                .build();
        sendCommandResponse(embed, duration);
    }

    @Override
    public void sendPrivateFile(File file){
        getUser().openPrivateChannel()
                .flatMap(a->a.sendFile(file))
                .submit();

    }
}
