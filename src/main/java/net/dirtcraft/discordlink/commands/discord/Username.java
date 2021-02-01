package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Username implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        DiscordLink discordLink = DiscordLink.get();
        UserManagerImpl userManager = discordLink.getUserManager();
        if (args.isEmpty()) throw new DiscordCommandException("Invalid Discord ID");
        final String discordID = args.get(0);
        Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        Matcher matcher = pattern.matcher(discordID);
        Optional<Member> member;
        if (!matcher.matches() || !(member = Utility.getMemberById(matcher.group(1))).isPresent()) throw new DiscordCommandException("Invalid Discord ID");

        final GuildMember player =userManager.getMember(member.get());
        final Optional<PlatformUser> user = player.getPlayerData();
        if (!user.isPresent()) throw new DiscordCommandException("The user was not verified!");
        source.sendCommandResponse("Minecraft Username:", user.flatMap(PlatformUser::getNameIfPresent).get());
    }
}
