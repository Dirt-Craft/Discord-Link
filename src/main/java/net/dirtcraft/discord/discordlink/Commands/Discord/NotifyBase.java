package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Add;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.List;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Remove;
import net.dirtcraft.discord.discordlink.Commands.Discord.notify.Time;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommand;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandTree;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

public class NotifyBase extends DiscordCommandTree {
    public NotifyBase(){
        DiscordCommand time = DiscordCommand.builder()
                .setDescription("Sets when the boot failure threshold is reached, In minutes")
                .setRequiredRoles(Roles.DIRTY)
                .setCommandExecutor(new Time())
                .build();

        DiscordCommand add = DiscordCommand.builder()
                .setDescription("Starts notifying you when the boot failure threshold is reached.")
                .setRequiredRoles(Roles.ADMIN)
                .setCommandExecutor(new Add())
                .build();

        DiscordCommand list = DiscordCommand.builder()
                .setDescription("List anyone to be notified when the boot failure threshold is reached.")
                .setRequiredRoles(Roles.ADMIN)
                .setCommandExecutor(new List())
                .build();

        DiscordCommand remove = DiscordCommand.builder()
                .setDescription("Stops notifying you when the boot failure threshold is reached.")
                .setRequiredRoles(Roles.VERIFIED)
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
            String header = pre + command + " " + alias;
            embed.addField(header, cmd.getDescription(), false);
        });
        GameChat.sendMessage(embed.build(), 30);
    }
}
