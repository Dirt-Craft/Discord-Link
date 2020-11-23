package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Main.*;

public enum Action {
    CHAT            ( "\n",    Sender.NONE,     Type.CHAT_MESSAGE    ),
    PUBLIC_BUNGEE   ( bungeePublic,   Sender.GAMECHAT, Type.BUNGEE_COMMAND  ),
    PRIVATE_BUNGEE  ( bungeePrivate,  Sender.PRIVATE,  Type.BUNGEE_COMMAND  ),
    PUBLIC_COMMAND  ( consolePublic,  Sender.GAMECHAT, Type.CONSOLE_COMMAND ),
    PRIVATE_COMMAND ( consolePrivate, Sender.PRIVATE,  Type.CONSOLE_COMMAND ),
    DISCORD_COMMAND ( discordCommand, Sender.NONE,     Type.DISCORD_COMMAND );

    final public String prefix;
    final public Sender sender;
    final public Type type;

    Action(String prefix, Sender sender, Type type){
        this.sender = sender;
        this.prefix = prefix;
        this.type = type;
    }

    public static String filterConsolePrefixes(String command){
        String prefixes = Arrays.stream(values())
                .filter(Action::isConsole)
                .map(Action::getPrefix)
                .collect(Collectors.joining("|"));
        return command.replaceAll("^(" + prefixes + ")", "");
    }

    public static Action fromMessageRaw(String rawMessage){
        return Arrays.stream(values())
                .filter(cmd->rawMessage.startsWith(cmd.prefix))
                .filter(cmd->cmd != CHAT)
                .max(Comparator.comparingInt(Action::length))
                .orElse(CHAT);
    }

    public String getCommand(MessageReceivedEvent event){
        return event.getMessage().getContentRaw().substring(prefix.length());
    }

    public boolean isChat(){
        return type == Type.CHAT_MESSAGE;
    }

    public boolean isConsole(){
        return type == Type.CONSOLE_COMMAND;
    }

    public boolean isBotCommand(){
        return type == Type.DISCORD_COMMAND;
    }

    public boolean isBungee(){
        return type == Type.BUNGEE_COMMAND;
    }

    public boolean isPrivate() {
        return sender == Sender.PRIVATE;
    }

    public String getPrefix(){
        return prefix;
    }

    public ConsoleSource getCommandSource(MessageSource sender, String command){
        if (this.sender == Sender.PRIVATE) {
            return DiscordResponder.getSender(sender);
        } else {
            return DiscordResponder.getSender(sender.getChannel().getCommandResponder(sender, command));
        }
    }

    private int length() {
        return prefix.length();
    }

    public enum Sender{
        PRIVATE,
        GAMECHAT,
        NONE
    }

    public enum Type{
        BUNGEE_COMMAND,
        CONSOLE_COMMAND,
        DISCORD_COMMAND,
        CHAT_MESSAGE
    }
}
