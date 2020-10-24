package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Add;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.List;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Remove;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Time;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommand;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandTree;

public class NotifyBase extends DiscordCommandTree {
    public NotifyBase(){
        DiscordCommand time = DiscordCommand.builder()
                .setDescription("Sets the time to send a notification if the server is still on a boot stage.")
                .setRequiredRoles(Roles.DIRTY)
                .setCommandExecutor(new Time())
                .build();

        DiscordCommand add = DiscordCommand.builder()
                .setDescription("Sets the time to send a notification if the server is still on a boot stage.")
                .setRequiredRoles(Roles.ADMIN)
                .setCommandExecutor(new Add())
                .build();

        DiscordCommand list = DiscordCommand.builder()
                .setDescription("List anyone to be notified when the server fails to boot.")
                .setRequiredRoles(Roles.ADMIN)
                .setCommandExecutor(new List())
                .build();

        DiscordCommand remove = DiscordCommand.builder()
                .setDescription("Sets the time to send a notification if the server is still on a boot stage.")
                .setRequiredRoles(Roles.VERIFIED)
                .setCommandExecutor(new Remove())
                .build();

        register(time, "time");
        register(add, "add");
        register(remove, "remove");
        register(list, "list");
    }
}
