package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Username implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) throw new DiscordCommandException("Invalid Discord ID");
        final String discordID = args.get(0);
        Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        Matcher matcher = pattern.matcher(discordID);
        if (!matcher.matches() || GameChat.getGuild().getMemberById(matcher.group(1)) == null) throw new DiscordCommandException("Invalid Discord ID");

        final GuildMember player = new GuildMember(GameChat.getGuild().getMemberById(matcher.group(1)));
        final Optional<User> user = player.getSpongeUser();
        if (!user.isPresent()) throw new DiscordCommandException("The user was not verified!");
        GameChat.sendEmbed(null, user.get().getName());
    }
}
