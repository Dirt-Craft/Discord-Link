package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.spongediscordlib.users.MessageSource;

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
            DiscordLink.get().saveConfig();
            source.sendCommandResponse("Command successfully executed", "notify time has been set to " + PluginConfiguration.Notifier.maxStageMinutes + "minutes.", 30);
        } catch (Exception e){
            throw new DiscordCommandException("Invalid input type!");
        }
    }
}
