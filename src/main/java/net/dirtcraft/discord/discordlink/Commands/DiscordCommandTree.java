package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordCommandTree implements DiscordCommandExecutor {

    protected final HashMap<String, DiscordCommand> commandMap = new HashMap<>();

    public void register(DiscordCommand command, String... alias){
        for (String name : alias) {
            commandMap.put(name, command);
        }
    }

    @Override
    public void execute(MessageSource member, String command, List<String> args) throws DiscordCommandException {
        if (args.size() == 0) {
            defaultResponse(member, command, args);
            return;
        }

        String base = args.remove(0);
        DiscordCommand discordCommand = commandMap.get(base);

        if (discordCommand != null) discordCommand.process(member, base, args);
        else throw new DiscordCommandException("Command not found");
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
