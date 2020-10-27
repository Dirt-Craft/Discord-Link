package net.dirtcraft.discord.discordlink.API;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

import static net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration.Main.*;

public enum  ActionType {
    CHAT( "\n",               false,false),
    DISCORD(     botPrefix,          false,false),
    ADMIN(       consolePrefix,      false,false),
    ADMIN_SILENT(silentConsolePrefix,true, false),
    PROXY_DISCORD("~$",       true, true),
    PROXY_MANAGER("$",        true, true),
    PROXY_SILENT ("#$",       true, true);

    final public String prefix;
    final public boolean silent;
    final public boolean proxy;

    ActionType(String prefix, boolean silent, boolean proxy){
        this.silent = silent;
        this.prefix = prefix;
        this.proxy = proxy;
    }

    public static ActionType fromMessageRaw(String rawMessage){
        return Arrays.stream(values())
                .filter(cmd->rawMessage.startsWith(cmd.prefix))
                .filter(cmd->cmd != CHAT)
                .findFirst().orElse(CHAT);
    }

    public String getCommand(MessageReceivedEvent event){
        return event.getMessage().getContentRaw().substring(prefix.length());
    }
}
