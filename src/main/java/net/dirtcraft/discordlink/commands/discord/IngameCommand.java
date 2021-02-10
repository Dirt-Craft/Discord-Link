package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngameCommand implements DiscordCommandExecutor {
    private final String command;
    private final boolean args;
    private final DiscordRole argsReq;
    private int required;

    public IngameCommand(String command){
        this.command = command;
        this.args = false;
        this.argsReq = DiscordRoles.NONE;
        required = 0;
        Pattern pattern = Pattern.compile("\\{arg}");
        Matcher matcher = pattern.matcher(command);
        while (matcher.find()) required++;
    }

    public IngameCommand(String command, DiscordRole role){
        this.command = command;
        this.args = true;
        this.argsReq = role;
        required = 0;
        Pattern pattern = Pattern.compile("\\{arg}");
        Matcher matcher = pattern.matcher(command);
        while (matcher.find()) required++;
    }

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (required > args.size()) throw new DiscordCommandException("Expected " + required + " arguments, but got " + args.size());
        String runCommand = parseCommand(source, this.command, args);
        ConsoleSource sender = source.getCommandSource(runCommand);
        Task.builder()
                .execute( () -> Sponge.getCommandManager().process(sender, runCommand))
                .submit(DiscordLink.get());
    }

    private boolean canUseArgs(MessageSource source){
        return args && argsReq == DiscordRoles.NONE || source.hasRole(argsReq);
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
        while (template.contains("{arg}") && !arguments.isEmpty()) template = template.replaceFirst("\\{arg}", arguments.remove(0));
        return template + " " + String.join(" ", arguments);
    }
}
