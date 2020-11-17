package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;

import java.util.List;

public class Time implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) {
            source.sendCommandResponse("Boot-stall notificator", "notify time is currently " + PluginConfiguration.Notifier.maxStageMinutes + "minutes.", 30);
            return;
        }
        try {
            PluginConfiguration.Notifier.maxStageMinutes = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            source.sendCommandResponse("Command successfully executed", "notify time has been set to " + PluginConfiguration.Notifier.maxStageMinutes + "minutes.", 30);
        } catch (Exception e){
            throw new DiscordCommandException("Invalid input type!");
        }
    }
}
