package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.spongediscordlib.users.MessageSource;

import java.util.List;

public class Add implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (PluginConfiguration.Notifier.notify.contains(source.getIdLong())) throw new DiscordCommandException("User already present!");
        try {
            PluginConfiguration.Notifier.notify.add(source.getIdLong());
            DiscordLink.get().saveConfig();
            source.sendCommandResponse("Command successfully executed", "Added " + source.getEffectiveName() + " to the notification list.", 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
