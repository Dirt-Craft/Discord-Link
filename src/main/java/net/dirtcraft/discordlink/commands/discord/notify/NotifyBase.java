package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandTree;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

public class NotifyBase extends DiscordCommandTree {
    public NotifyBase(){
        DiscordCommandImpl time = DiscordCommandImpl.builder()
                .setDescription("Sets when the boot failure threshold is reached, In minutes")
                .setPreBootEnabled(true)
                .setRequiredRoles(DiscordRoles.ADMIN)
                .setCommandUsage("[<number>]")
                .setCommandExecutor(new Time())
                .build();

        DiscordCommandImpl add = DiscordCommandImpl.builder()
                .setDescription("Starts notifying you when the boot failure threshold is reached.")
                .setPreBootEnabled(true)
                .setRequiredRoles(DiscordRoles.ADMIN)
                .setCommandExecutor(new Add())
                .build();

        DiscordCommandImpl list = DiscordCommandImpl.builder()
                .setDescription("List anyone to be notified when the boot failure threshold is reached.")
                .setPreBootEnabled(true)
                .setRequiredRoles(DiscordRoles.ADMIN)
                .setCommandExecutor(new List())
                .build();

        DiscordCommandImpl remove = DiscordCommandImpl.builder()
                .setDescription("Stops notifying you when the boot failure threshold is reached.")
                .setPreBootEnabled(true)
                .setRequiredRoles(DiscordRoles.VERIFIED)
                .setCommandExecutor(new Remove())
                .build();

        register(time, "time");
        register(list, "list");
        register(add, "add");
        register(remove, "remove");
    }

    @Override
    public void defaultResponse(MessageSource member, String command, java.util.List<String> args) throws DiscordCommandException {
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
