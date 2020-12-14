package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.*;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DiscordCommandManager extends DiscordCommandTree {

    private final HashSet<String> defaultAliases = new HashSet<>(Arrays.asList("", "help"));

    public DiscordCommandManager() {
        DiscordCommand mute = DiscordCommand.builder()
            .setDescription("Command base for mute manipulation")
            .setCommandExecutor(new MuteBase())
            .setRequiredRoles(Roles.STAFF)
            .build();

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

        DiscordCommand kits = DiscordCommand.builder()
                .setDescription("Reveals a players kits.")
                .setCommandExecutor(new Kits())
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
                .setDescription("Shows server-boot notifier commands")
                .setPreBootEnabled(true)
                .setCommandExecutor(new NotifyBase())
                .build();

        DiscordCommand prefix = DiscordCommand.builder()
                .setDescription("Sets prefixes")
                .setCommandUsage("<title>")
                .setRequiredRoles(Roles.STAFF)
                .setCommandExecutor(new Prefix())
                .build();

        DiscordCommand version = DiscordCommand.builder()
                .setDescription("Shows the current version")
                .setRequiredRoles(Roles.DIRTY)
                .setCommandExecutor(new Version())
                .build();

        DiscordCommand logs = DiscordCommand.builder()
                .setDescription("Shows latest logs")
                .setRequiredRoles(Roles.DIRTY)
                .setCommandExecutor(new Logs())
                .build();

        register(mute, "mute");
        register(list, "list", "players");
        register(stop, "stop");
        register(halt, "halt");
        register(seen, "seen");
        register(unstuck, "unstuck", "spawn");
        register(username, "username");
        register(discord, "discord");
        register(ranks, "ranks", "groups", "parents");
        register(sync, "sync");
        register(unverify, "unverify", "unlink");
        register(notify, "notify");
        register(prefix, "prefix");
        register(kits, "kits");
        register(version, "version", "info");
        register(logs, "logs");
    }

    public void process(MessageSource member, String args){
        try {
            String[] command = args == null || defaultAliases.contains(args)? new String[0] : args.split(" ");
            execute(member, null, new ArrayList<>(Arrays.asList(command)));
        } catch (Exception e){
            String message = e.getMessage() != null? e.getMessage() : "an error occurred while executing the command.";
            Utility.sendCommandError(member, message);
        } finally {
            if (!member.isPrivateMessage()) member.getMessage().delete().queue();
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
            embed.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
        });
        member.sendCommandResponse(embed.build());
    }
}
