package net.dirtcraft.discordlink.commands;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.commands.discord.mute.MuteBase;
import net.dirtcraft.discordlink.commands.discord.notify.NotifyBase;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.commands.discord.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DiscordCommandManagerImpl extends DiscordCommandTree {

    private final HashSet<String> defaultAliases = new HashSet<>(Arrays.asList("", "help"));

    public DiscordCommandManagerImpl() {
        DiscordCommandImpl mute = DiscordCommandImpl.builder()
            .setDescription("Command base for mute manipulation")
            .setCommandExecutor(new MuteBase())
            .setRequiredRoles(DiscordRoles.STAFF)
            .build();

        DiscordCommandImpl list = DiscordCommandImpl.builder()
                .setDescription("Shows a list of all players online.")
                .setCommandExecutor(new PlayerList())
                .build();

        DiscordCommandImpl halt = DiscordCommandImpl.builder()
                .setDescription("Stops the server abruptly.")
                .setCommandExecutor(new StopServer(false))
                .setPreBootEnabled(true)
                .setRequiredRoles(DiscordRoles.ADMIN)
                .build();

        DiscordCommandImpl stop = DiscordCommandImpl.builder()
                .setDescription("Stops the server gracefully.")
                .setCommandExecutor(new StopServer(true))
                .setPreBootEnabled(true)
                .setRequiredRoles(DiscordRoles.ADMIN)
                .build();

        DiscordCommandImpl unstuck = DiscordCommandImpl.builder()
                .setDescription("Teleports you to spawn if you are verified.")
                .setCommandExecutor(new Unstuck())
                .setRequiredRoles(DiscordRoles.VERIFIED)
                .build();

        DiscordCommandImpl seen = DiscordCommandImpl.builder()
                .setDescription("Sends you a DM with a players info.")
                .setCommandExecutor(new SilentSeen())
                .setCommandUsage("<Player>")
                .setRequiredRoles(DiscordRoles.STAFF)
                .build();

        DiscordCommandImpl username = DiscordCommandImpl.builder()
                .setDescription("Reveals a verified players minecraft username.")
                .setCommandExecutor(new Username())
                .setCommandUsage("<@Discord>")
                .setRequiredRoles(DiscordRoles.STAFF)
                .build();

        DiscordCommandImpl discord = DiscordCommandImpl.builder()
                .setDescription("Reveals a verified players discord username.")
                .setCommandExecutor(new Discord())
                .setCommandUsage("<Player>")
                .setRequiredRoles(DiscordRoles.STAFF)
                .build();

        DiscordCommandImpl ranks = DiscordCommandImpl.builder()
                .setDescription("Reveals a players ranks.")
                .setCommandExecutor(new Ranks())
                .setRequiredRoles(DiscordRoles.VERIFIED)
                .build();

        DiscordCommandImpl kits = DiscordCommandImpl.builder()
                .setDescription("Reveals a players kits.")
                .setCommandExecutor(new Kits())
                .setRequiredRoles(DiscordRoles.VERIFIED)
                .build();

        DiscordCommandImpl sync = DiscordCommandImpl.builder()
                .setDescription("Runs LP Sync to re-sync the perms")
                .setCommandExecutor(new IngameCommand("lp sync"))
                .setRequiredRoles(DiscordRoles.STAFF)
                .build();

        DiscordCommandImpl unverify = DiscordCommandImpl.builder()
                .setDescription("Unverifies your account.")
                .setCommandExecutor(new Unlink())
                .build();

        DiscordCommandImpl notify = DiscordCommandImpl.builder()
                .setDescription("Shows server-boot notifier commands")
                .setPreBootEnabled(true)
                .setCommandExecutor(new NotifyBase())
                .build();

        DiscordCommandImpl prefix = DiscordCommandImpl.builder()
                .setDescription("Sets prefixes")
                .setCommandUsage("<title>")
                .setRequiredRoles(DiscordRoles.STAFF)
                .setCommandExecutor(new Prefix())
                .build();

        DiscordCommandImpl version = DiscordCommandImpl.builder()
                .setDescription("Shows the current version")
                .setRequiredRoles(DiscordRoles.MOD)
                .setCommandExecutor(new Version())
                .build();

        DiscordCommandImpl inv = DiscordCommandImpl.builder()
                .setDescription("Debug")
                .setRequiredRoles(DiscordRoles.MOD)
                .setCommandExecutor(new ItemBase())
                .build();

        DiscordCommandImpl logs = DiscordCommandImpl.builder()
                .setDescription("Shows latest logs")
                .setRequiredRoles(DiscordRoles.MOD)
                .setCommandExecutor(new Logs())
                .setPreBootEnabled(true)
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
        register(inv, "inv");
    }

    public void process(MessageSourceImpl member, String args){
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
