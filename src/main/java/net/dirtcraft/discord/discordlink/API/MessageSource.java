package net.dirtcraft.discord.discordlink.API;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageSource extends GuildMember {
    private final Message message;
    public MessageSource(MessageReceivedEvent event){
        super(event.getMember());
        this.message = event.getMessage();
    }

    public Message getMessage() {
        return message;
    }
}
