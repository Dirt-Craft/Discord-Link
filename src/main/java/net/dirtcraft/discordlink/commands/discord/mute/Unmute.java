package net.dirtcraft.discordlink.commands.discord.mute;

import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.tables.Mutes;
import net.dirtcraft.discordlink.utility.Utility;

import java.util.List;

public class Unmute implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        DiscordLink discordLink = DiscordLink.get();
        Database database = discordLink.getStorage();
        UserManagerImpl userManager = discordLink.getUserManager();
        DiscordMember target = removeIfPresent(args, userManager::getMember).orElseThrow(()->new DiscordCommandException("Discord user not specified."));
        Utility.removeRoleIfPresent(target.getIdLong(), DiscordRoles.MUTED);
        Mutes.MuteData data = database.hasActiveMute(target.getIdLong()).orElseThrow(()->new DiscordCommandException("The user was never muted."));
        database.deactivateMute(source.getIdLong(), target.getIdLong());
        source.sendCommandResponse(source.getAsMention() + " The following mute has been removed", 30);
        source.sendCommandResponse(MuteInfo.getInfo(data), 30);
    }
}