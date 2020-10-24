package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Add;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.List;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Remove;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Time;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommand;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandTree;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
        register(list, "list");
        register(add, "add");
        register(remove, "remove");
    }

    @Override
    public void defaultResponse(GuildMember member, java.util.List<String> args, MessageReceivedEvent event) throws DiscordCommandException {
        StringBuilder result = new StringBuilder();
        String pre = PluginConfiguration.Main.botPrefix;
        String command = event.getMessage().getContentDisplay().replaceAll("(?i)(" + pre + "\\S+).*", "$1");
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            result.append(" **-** ")
                    .append(command)
                    .append(" ")
                    .append(alias)
                    .append("\n");
        });
        GameChat.sendEmbed("Sub Commands:", result.toString(), 30);
        event.getMessage().delete().queue();
    }
}
