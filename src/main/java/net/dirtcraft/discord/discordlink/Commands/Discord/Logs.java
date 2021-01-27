package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Logs implements DiscordCommandExecutor {
    private static final int MAX_ENTRIES = 20;
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) sendLatest(source);
        else if (!args.get(0).matches("(?i)crash-?(log|report)?s?")) getLogs(source, args.size() > 1? args.get(1): null);
        else getCrashLogs(source, args.size() > 1? args.get(1): null);
    }

    private void sendLatest(MessageSource source){
        final File logs = Paths.get("logs", "latest.log").toFile();
        source.sendPrivateFile(logs);
    }

    private void getCrashLogs(MessageSource source, String log){
        if (log == null){
            final String[] logs = Paths.get("crash-reports").toFile().list();
            String[] newOnly = new String[MAX_ENTRIES];
            Arrays.sort(logs);
            if (logs.length < MAX_ENTRIES) newOnly = logs;
            else System.arraycopy(logs, logs.length - (MAX_ENTRIES), newOnly, 0, MAX_ENTRIES);
            source.sendMessage(String.join("\n", newOnly));
        } else  if (log.contains("\\") || log.contains("/")) {
            source.sendMessage("Filename contains illegal characters!");
        } else {
            final File logs = Paths.get("crash-reports", log).toFile();
            source.sendPrivateFile(logs);
        }
    }

    private void getLogs(MessageSource source, String log){
        if (log == null) {
            final String[] logs = Paths.get("logs").toFile().list();
            String[] newOnly = new String[MAX_ENTRIES];
            Arrays.sort(logs);
            if (logs.length < MAX_ENTRIES) newOnly = logs;
            else System.arraycopy(logs, logs.length - (MAX_ENTRIES), newOnly, 0, MAX_ENTRIES);
            source.sendMessage(String.join("\n", newOnly));
        } else  if (log.contains("\\") || log.contains("/")) {
            source.sendMessage("Filename contains illegal characters!");
        } else {
            final File logs = Paths.get("logs", log).toFile();
            source.sendPrivateFile(logs);
        }

    }
}