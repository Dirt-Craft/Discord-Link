package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class Time implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, List<String> args, MessageReceivedEvent event) throws DiscordCommandException {
        if (args.isEmpty()) throw new DiscordCommandException("You need to specify a time in minutes.");
        try {
            PluginConfiguration.Notifier.maxStageMinutes = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            GameChat.sendMessage("Successfully set notify time to " + PluginConfiguration.Notifier.maxStageMinutes + ".", 30);

        } catch (Exception e){
            throw new DiscordCommandException("Invalid input type!");
        }
    }
}
