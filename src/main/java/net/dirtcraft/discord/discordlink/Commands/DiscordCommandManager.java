package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.*;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DiscordCommandManager extends DiscordCommandTree {

    private final HashSet<String> defaultAliases = new HashSet<>(Arrays.asList("", "help"));

    public DiscordCommandManager() {
        DiscordCommand list = DiscordCommand.builder()
                .setDescription("Shows a list of all players online.")
                .setCommandExecutor(new PlayerList())
                .build();

        DiscordCommand halt = DiscordCommand.builder()
                .setDescription("Stops the server abruptly.")
                .setCommandExecutor(new StopServer(false))
                .setPreBootEnabled(true)
                .setRequiredRoles(Roles.ADMIN)
                .build();

        DiscordCommand stop = DiscordCommand.builder()
                .setDescription("Stops the server gracefully.")
                .setCommandExecutor(new StopServer(true))
                .setPreBootEnabled(true)
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
                .setCommandUsage("<Player>")
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand username = DiscordCommand.builder()
                .setDescription("Reveals a verified players minecraft username.")
                .setCommandExecutor(new Username())
                .setCommandUsage("<@Discord>")
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand discord = DiscordCommand.builder()
                .setDescription("Reveals a verified players discord username.")
                .setCommandExecutor(new Discord())
                .setCommandUsage("<Player>")
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
                .setPreBootEnabled(true)
                .setCommandExecutor(new NotifyBase())
                .build();

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

    public void process(MessageSource member, String args){
        try {
            String[] command = args == null || defaultAliases.contains(args)? new String[0] : args.toLowerCase().split(" ");
            execute(member, null, new ArrayList<>(Arrays.asList(command)));
        } catch (Exception e){
            String message = e.getMessage() != null? e.getMessage() : "an error occurred while executing the command.";
            Utility.sendCommandError(member, message);
        } finally {
            member.getMessage().delete().queue();
        }
    }

    @Override
    public void defaultResponse(MessageSource member, String command, java.util.List<String> args) throws DiscordCommandException {
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Main.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String title = pre + alias + " " + cmd.getUsage();
            embed.addField(title, cmd.getDescription(), false);
        });
        GameChat.sendMessage(embed.build());
    }
}
