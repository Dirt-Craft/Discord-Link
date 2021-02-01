package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.spongediscordlib.users.MessageSource;

import java.util.List;

public class Remove implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        try {
            PluginConfiguration.Notifier.notify.remove(source.getUser().getIdLong());
            DiscordLink.get().saveConfig();
            source.sendCommandResponse("Command successfully executed", "Removed " + source.getEffectiveName() + " from the notification list.", 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
