package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;

import java.util.List;

public class Add implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (PluginConfiguration.Notifier.notify.contains(source.getIdLong())) throw new DiscordCommandException("User already present!");
        try {
            PluginConfiguration.Notifier.notify.add(source.getIdLong());
            DiscordLink.getInstance().saveConfig();
            source.sendCommandResponse("Command successfully executed", "Added " + source.getEffectiveName() + " to the notification list.", 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
