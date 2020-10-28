package net.dirtcraft.discord.discordlink.Commands;

import com.google.common.collect.Lists;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordPermissionException;
import net.dirtcraft.discord.discordlink.Utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommand {
    private final List<Roles> allowedRoles;
    private final String description;
    private final DiscordCommandExecutor executor;

    private DiscordCommand(List<Roles> allowed, DiscordCommandExecutor executor, String description){
        this.allowedRoles = allowed;
        this.executor = executor;
        this.description = description;
    }

    private void sendPermissionError(MessageSource event){
        GameChat.sendMessage("<@" + event.getUser().getId() + ">, you do **not** have permission to use this command!", 5);
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                        .build())
                .queue();

    }

    private void sendCommandError(MessageSource event, String msg){
        GameChat.sendMessage("<@" + event.getUser().getId() + ">, " + msg, 5);
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                        .build())
                .queue();
    }

    public boolean hasPermission(GuildMember member){
        return allowedRoles.stream().allMatch(member::hasRole);
    }

    public static Builder builder(){
        return new Builder();
    }

    public final void process(MessageSource member, String command, List<String> args) {
        if (!allowedRoles.stream().allMatch(member::hasRole)) {
            sendPermissionError(member);
            return;
        }
        try {
            executor.execute(member, command, args);
        } catch (DiscordPermissionException e) {
            sendPermissionError(member);
        } catch (Exception e) {
            //e.printStackTrace();
            sendCommandError(member, e.getMessage() != null ? e.getMessage() : "an error occurred while executing the command.");
        }
    }

    public static class Builder{
        private List<Roles> allowedRoles;
        private DiscordCommandExecutor executor;
        private String description;

        private Builder(){}

        public final Builder setRequiredRoles(Roles... roles){
            allowedRoles = Lists.newArrayList(roles);
            return this;
        }

        public final Builder setCommandExecutor(DiscordCommandExecutor executor){
            this.executor = executor;
            return this;
        }

        public final Builder setDescription(String description){
            this.description = description;
            return this;
        }

        public DiscordCommand build(){
            return new DiscordCommand(
                    allowedRoles != null ? allowedRoles : new ArrayList<>(),
                    executor     != null ? executor     : (member, command, event)->{},
                    description  != null ? description  : ""
            );
        }

    }
}
