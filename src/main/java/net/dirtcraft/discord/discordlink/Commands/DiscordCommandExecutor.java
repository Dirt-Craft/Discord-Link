package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;

import java.util.List;

public interface DiscordCommandExecutor {
    void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException;
}
