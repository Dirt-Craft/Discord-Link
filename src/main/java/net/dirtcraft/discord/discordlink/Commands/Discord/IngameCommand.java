package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.List;

public class IngameCommand implements DiscordCommandExecutor {
    private final String command;
    private final boolean args;
    private final Roles argsReq;

    public IngameCommand(String command){
        this.command = command;
        this.args = false;
        this.argsReq = Roles.NONE;
    }

    public IngameCommand(String command, Roles role){
        this.command = command;
        this.args = true;
        this.argsReq = role;
    }

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        String runCommand = parseCommand(source, this.command, args);
        ConsoleSource sender = source.getCommandSource(runCommand);
        Task.builder()
                .execute( () -> Sponge.getCommandManager().process(sender, runCommand))
                .submit(DiscordLink.getInstance());
    }

    private boolean canUseArgs(MessageSource source){
        return args && argsReq == Roles.NONE || source.hasRole(argsReq);
    }

    private String parseCommand(MessageSource source, String command, List<String> args) throws DiscordCommandException{
        //final String sender = source.getPlayerData().flatMap(PlatformUser::getName).orElseThrow(()->new DiscordCommandException("You must be verified to use this!"));
        if (canUseArgs(source)) command = parseArguments(command, args);
        return command;//.replaceAll("\\{sender}", sender);
    }

    private String parseArguments(String template, List<String> arguments){
        //final List<String> targets = arguments.stream()
        //        .filter(s->s.matches("--player=\\S+"))
        //        .peek(arguments::remove)
        //        .collect(Collectors.toList());
        final String marker = "\\{arg}";
        while (template.matches("(?i)^.*" + marker + ".*$") && !arguments.isEmpty()) template = template.replaceFirst("(?i)" + marker, arguments.get(0));
        return template + " " + String.join(" ", arguments);
    }
}
