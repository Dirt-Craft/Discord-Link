package net.dirtcraft.discordlink.commands.discord.mute;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandTree;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

public class MuteBase extends DiscordCommandTree {
    DiscordCommandImpl add = DiscordCommandImpl.builder()
            .setDescription("Mutes a player!")
            .setCommandUsage("<@Discord> [duration] [reason]")
            .setRequiredRoles(DiscordRoles.MOD)
            .setCommandExecutor(new Mute())
            .build();

    public MuteBase(){
        DiscordCommandImpl remove = DiscordCommandImpl.builder()
                .setDescription("Removes a players mute")
                .setCommandUsage("<@Discord>")
                .setRequiredRoles(DiscordRoles.MOD)
                .setCommandExecutor(new Unmute())
                .build();

        DiscordCommandImpl info = DiscordCommandImpl.builder()
                .setDescription("Shows the details of a mute")
                .setCommandUsage("<@Discord>")
                .setRequiredRoles(DiscordRoles.STAFF)
                .setCommandExecutor(new MuteInfo())
                .build();

        register(add, "add");
        register(remove, "remove");
        register(info, "info");
    }

    @Override
    public void defaultResponse(MessageSource member, String command, java.util.List<String> args) throws DiscordCommandException {
        if (!args.isEmpty() && !defaults.contains(args.get(0))) {
            add.process(member, command, args);
            return;
        }
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Main.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String header = pre + command + " " + alias + " " + cmd.getUsage();
            embed.addField(header, cmd.getDescription(), false);
            embed.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
        });
        member.sendCommandResponse(embed.build(), 30);
    }

}