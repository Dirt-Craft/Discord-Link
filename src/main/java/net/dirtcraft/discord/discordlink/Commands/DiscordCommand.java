package net.dirtcraft.discord.discordlink.Commands;

import com.google.common.collect.Lists;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
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

    public boolean hasPermission(GuildMember member){
        return allowedRoles.stream().allMatch(member::hasRole);
    }

    public static Builder builder(){
        return new Builder();
    }

    public final void process(MessageSource member, String command, List<String> args) {
        if (!allowedRoles.stream().allMatch(member::hasRole)) {
            Utility.sendPermissionError(member);
            return;
        }
        try {
            executor.execute(member, command, args);
        } catch (DiscordPermissionException e) {
            Utility.sendPermissionError(member);
        } catch (Exception e) {
            //e.printStackTrace();
            Utility.sendCommandError(member, e.getMessage() != null ? e.getMessage() : "an error occurred while executing the command.");
        }
    }

    public String getDescription() {
        return description;
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
