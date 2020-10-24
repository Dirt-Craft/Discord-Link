package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class Add implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, List<String> args, MessageReceivedEvent event) throws DiscordCommandException {
        try {
            PluginConfiguration.Notifier.notify.add(source.getUser().getIdLong());
            DiscordLink.getInstance().saveConfig();
            GameChat.sendMessage("Successfully added " + source.getEffectiveName() + " to the notification list.", 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
