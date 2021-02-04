package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.exceptions.DiscordPermissionException;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Logs implements DiscordCommandExecutor {
    private static final int MAX_ENTRIES = 20;
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) sendLatest(source);
        else if (args.get(0).matches("(?i)crash-?(log|report)?s?")) getCrashLogs(source, args.size() > 1? args.get(1): null);
        else if (args.get(0).matches("(?i)(debug|special|secret)")) getDebug(source, args.size() > 1? args.get(1): null);
        else getLogs(source, args.size() > 1? args.get(1): null);
    }

    private void sendLatest(MessageSource source){
        final File logs = Paths.get("logs", "latest.log").toFile();
        source.sendPrivateFile(logs);
    }

    private void getCrashLogs(MessageSource source, String log){
        if (log == null){
            final String[] logs = Paths.get("crash-reports").toFile().list();
            String[] newOnly = new String[MAX_ENTRIES];
            if (logs == null) return;
            Arrays.sort(logs);
            if (logs.length < MAX_ENTRIES) newOnly = logs;
            else System.arraycopy(logs, logs.length - (MAX_ENTRIES), newOnly, 0, MAX_ENTRIES);
            source.sendPrivateMessage(String.join("\n", newOnly));
        } else  if (log.contains("\\") || log.contains("/")) {
            source.sendPrivateMessage("Filename contains illegal characters!");
        } else {
            final File logs = Paths.get("crash-reports", log).toFile();
            source.sendPrivateFile(logs);
        }
    }

    private void getLogs(MessageSource source, String log){
        if (log == null) {
            final String[] logs = Paths.get("logs").toFile().list();
            if (logs == null) return;
            Arrays.sort(logs);
            source.sendPrivateMessage(String.join("\n", getLogs(logs, false)));
        } else  if (log.contains("\\") || log.contains("/") || !isLog(log)) {
            source.sendPrivateMessage("Filename contains illegal characters or is a debug log!");
        } else {
            final File logs = Paths.get("logs", log).toFile();
            source.sendPrivateFile(logs);
        }

    }

    private void getDebug(MessageSource source, String log) throws DiscordCommandException{
        if (!source.hasRole(DiscordRoles.DIRTY)) throw new DiscordPermissionException();
        else if (log == null) {
            final String[] logs = Paths.get("logs").toFile().list();
            if (logs == null) return;
            Arrays.sort(logs);
            source.sendPrivateMessage(String.join("\n", getLogs(logs, true)));
        } else  if (log.contains("\\") || log.contains("/") || isLog(log)) {
            source.sendPrivateMessage("Filename contains illegal characters or is not a debug log!");
        } else {
            final File logs = Paths.get("logs", log).toFile();
            source.sendPrivateFile(logs);
        }

    }

    private boolean isLog(String file){
        return file.equalsIgnoreCase("latest.log") ||
                file.matches("\\d+-\\d+-\\d+(-\\d+)?\\.log\\.gz");
    }

    //We iterate in reverse to find the latest 20 files in order,
    //then do a 0-index insert to reverse order it. (Latest last)
    private List<String> getLogs(String[] files, boolean debug){
        List<String> logs = new ArrayList<>();
        for(int i = files.length -1; i >= 0 && logs.size() < MAX_ENTRIES; i--){
            String file = files[i];
            if (debug ^ isLog(file)) logs.add(0, file);
        }
        return logs;
    }
}