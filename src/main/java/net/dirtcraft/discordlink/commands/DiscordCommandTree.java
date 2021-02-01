package net.dirtcraft.discordlink.commands;

import net.dirtcraft.spongediscordlib.commands.DiscordCommand;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandManager;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.users.MessageSource;

import java.util.*;

public abstract class DiscordCommandTree implements DiscordCommandExecutor, DiscordCommandManager {
    protected final HashMap<String, DiscordCommandImpl> commandMap = new HashMap<>();
    protected final HashSet<String> defaults = new HashSet<>(Arrays.asList("help", "?"));

    @Override
    public void register(DiscordCommand command, String... alias){
        if (!(command instanceof DiscordCommandImpl)) return;
        for (String name : alias) {
            commandMap.put(name.toLowerCase(), (DiscordCommandImpl) command);
        }
    }

    @Override
    public void execute(MessageSource member, String command, List<String> args) throws DiscordCommandException {
        if (args.size() == 0 || defaults.contains(args.get(0))) {
            defaultResponse(member, command, args);
            return;
        }

        final String base = args.get(0);
        final DiscordCommandImpl discordCommand = commandMap.get(base.toLowerCase());
        if (discordCommand != null) {
            args.remove(0);
            discordCommand.process(member, base, args);
        } else defaultResponse(member, command, args);
    }

    @Override
    public Map<String, DiscordCommandImpl> getCommandMap(){
        Map<String, DiscordCommandImpl> result = new HashMap<>();
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
