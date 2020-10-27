package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;

import java.util.List;

public class Remove implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        try {
            PluginConfiguration.Notifier.notify.remove(source.getUser().getIdLong());
            DiscordLink.getInstance().saveConfig();
            GameChat.sendEmbed("Command successfully executed", "Removed " + source.getEffectiveName() + " from the notification list.", 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
