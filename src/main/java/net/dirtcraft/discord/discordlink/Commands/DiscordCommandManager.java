package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.*;

public class DiscordCommandManager extends DiscordCommandTree {

    public DiscordCommandManager() {
        DiscordCommand help = DiscordCommand.builder()
                .setCommandExecutor(new Help())
                .build();

        DiscordCommand list = DiscordCommand.builder()
                .setDescription("Shows a list of all players online.")
                .setCommandExecutor(new PlayerList())
                .build();

        DiscordCommand halt = DiscordCommand.builder()
                .setDescription("Stops the server abruptly.")
                .setCommandExecutor(new StopServer(false))
                .setRequiredRoles(Roles.ADMIN)
                .build();

        DiscordCommand stop = DiscordCommand.builder()
                .setDescription("Stops the server gracefully.")
                .setCommandExecutor(new StopServer(true))
                .setRequiredRoles(Roles.ADMIN)
                .build();

        DiscordCommand unstuck = DiscordCommand.builder()
                .setDescription("Teleports you to spawn if you are verified.")
                .setCommandExecutor(new Unstuck())
                .setRequiredRoles(Roles.VERIFIED)
                .build();

        DiscordCommand seen = DiscordCommand.builder()
                .setDescription("Sends you a DM with a players info.")
                .setCommandExecutor(new SilentSeen())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand username = DiscordCommand.builder()
                .setDescription("Reveals a verified players minecraft username.")
                .setCommandExecutor(new Username())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand discord = DiscordCommand.builder()
                .setDescription("Reveals a verified players discord username.")
                .setCommandExecutor(new Discord())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand ranks = DiscordCommand.builder()
                .setDescription("Reveals a players ranks.")
                .setCommandExecutor(new Ranks())
                .setRequiredRoles(Roles.VERIFIED)
                .build();

        DiscordCommand sync = DiscordCommand.builder()
                .setDescription("Runs LP Sync to re-sync the perms")
                .setCommandExecutor(new IngameCommand("lp sync"))
                .setRequiredRoles(Roles.ADMIN)
                .build();

        DiscordCommand unverify = DiscordCommand.builder()
                .setDescription("Unverifies your account.")
                .setCommandExecutor(new Unlink())
                .build();

        DiscordCommand notify = DiscordCommand.builder()
                .setDescription("Manages notification settings.")
                .setCommandExecutor(new NotifyBase())
                .build();

        register(help, "help");
        register(list, "list");
        register(stop, "stop");
        register(halt, "halt");
        register(seen, "seen");
        register(unstuck, "unstuck", "spawn");
        register(username, "username");
        register(discord, "discord");
        register(ranks, "ranks");
        register(sync, "sync");
        register(unverify, "unverify", "unlink");
        register(notify, "notify");
    }
}
