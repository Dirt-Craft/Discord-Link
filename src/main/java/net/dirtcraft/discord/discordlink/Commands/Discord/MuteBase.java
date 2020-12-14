package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.Mute.Mute;
import net.dirtcraft.discord.discordlink.Commands.Discord.Mute.MuteInfo;
import net.dirtcraft.discord.discordlink.Commands.Discord.Mute.Unmute;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommand;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandTree;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

public class MuteBase extends DiscordCommandTree {
    public MuteBase(){
        DiscordCommand add = DiscordCommand.builder()
                .setDescription("Mutes a player!")
                .setCommandUsage("<@Discord> [duration] [reason]")
                .setRequiredRoles(Roles.MOD)
                .setCommandExecutor(new Mute())
                .build();

        DiscordCommand remove = DiscordCommand.builder()
                .setDescription("Removes a players mute")
                .setCommandUsage("<@Discord>")
                .setRequiredRoles(Roles.MOD)
                .setCommandExecutor(new Unmute())
                .build();

        DiscordCommand info = DiscordCommand.builder()
                .setDescription("Shows the details of a mute")
                .setCommandUsage("<@Discord>")
                .setRequiredRoles(Roles.STAFF)
                .setCommandExecutor(new MuteInfo())
                .build();

        register(add, "add", "a");
        register(remove, "remove", "r");
        register(info, "info", "i");
    }

    @Override
    public void defaultResponse(MessageSource member, String command, java.util.List<String> args) throws DiscordCommandException {
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Prefixes.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String header = pre + command + " " + alias + " " + cmd.getUsage();
            embed.addField(header, cmd.getDescription(), false);
            embed.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
        });
        member.sendCommandResponse(embed.build(), 30);
    }

}
