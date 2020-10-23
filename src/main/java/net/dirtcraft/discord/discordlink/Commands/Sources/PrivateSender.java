package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;

public class PrivateSender extends WrappedConsole implements ScheduledSender {
    private final GuildMember member;

    public PrivateSender(GuildMember member, String command) {
        this.member = member;
        member.sendMessage(String.format("Command sent: \"%s\" @ <#%s>\n", command, SpongeDiscordLib.getGamechatChannelID()));
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        member.sendMessage("``" + message + "``");
    }

    @Override
    public int getCharLimit(){
        return 1996;
    }

    @Override
    public boolean sanitise(){
        return false;
    }
}