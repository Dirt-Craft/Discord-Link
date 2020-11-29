package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class Logs implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        final File logs = Paths.get("logs", "latest.log").toFile();
        source.sendPrivateFile(logs);
    }
}