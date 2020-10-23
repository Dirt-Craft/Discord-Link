package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Utility.Utility;

public class GamechatSender extends WrappedConsole implements ScheduledSender {
    private GuildMember member;
    private String command;

    public GamechatSender(GuildMember member, String command) {
        this.member = member;
        this.command = command;
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        GameChat.sendMessage(
                Utility.embedBuilder().addField("__Command__ \"**/" + command.toLowerCase() + "**\" __Sent__", message, false)
                        .setFooter("Sent By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                        .build());
    }
}