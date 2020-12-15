package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;

import java.util.*;

public class DiscordCommandTree implements DiscordCommandExecutor {
    protected final HashMap<String, DiscordCommand> commandMap = new HashMap<>();
    protected final HashSet<String> defaults = new HashSet<>(Arrays.asList("help", "?"));

    public void register(DiscordCommand command, String... alias){
        for (String name : alias) {
            commandMap.put(name.toLowerCase(), command);
        }
    }

    @Override
    public void execute(MessageSource member, String command, List<String> args) throws DiscordCommandException {
        if (args.size() == 0 || defaults.contains(args.get(0))) {
            defaultResponse(member, command, args);
            return;
        }

        final String base = args.get(0);
        final DiscordCommand discordCommand = commandMap.get(base.toLowerCase());
        if (discordCommand != null) {
            args.remove(0);
            discordCommand.process(member, base, args);
        } else defaultResponse(member, command, args);
    }

    public Map<String, DiscordCommand> getCommandMap(){
        Map<String, DiscordCommand> result = new HashMap<>();
        commandMap.forEach((alias, cmd) -> {
            if (result.containsValue(cmd)) return;
            result.put(alias, cmd);
        });
        return result;
    }

    public void defaultResponse(MessageSource member, String command, List<String> args) throws DiscordCommandException{
        throw new DiscordCommandException("Command not found");
    }
}
