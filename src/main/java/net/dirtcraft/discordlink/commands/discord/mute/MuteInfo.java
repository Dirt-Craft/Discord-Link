package net.dirtcraft.discordlink.commands.discord.mute;

import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Mutes;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MuteInfo implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        DiscordLink discordLink = DiscordLink.get();
        Database database = discordLink.getStorage();
        UserManagerImpl userManager = discordLink.getUserManager();
        DiscordMember target =removeIfPresent(args, userManager::getMember).orElseThrow(() -> new DiscordCommandException("Discord user not specified."));
        Mutes.MuteData data = database.hasActiveMute(target.getIdLong()).orElseThrow(()->new DiscordCommandException("The player does not have an active mute!"));

        source.sendCommandResponse(getInfo(data));
    }

    public static MessageEmbed getInfo(Mutes.MuteData data){
        ChannelManagerImpl channelManager = DiscordLink.get().getChannelManager();
        UserManagerImpl userManager = DiscordLink.get().getUserManager();
        String name = or(
                "Unknown",
                ()->data.getSubmitter()
                        .map(id-> channelManager.getGuild().retrieveMemberById(id))
                        .map(RestAction::complete)
                        .map(userManager::getMember)
                        .map(GuildMember::getEffectiveName),
                ()->data.getSubmitterUUID()
                        .flatMap(PlatformProvider::getPlayerOffline)
                        .flatMap(PlatformUser::getNameIfPresent)
        );
        if (!data.getSubmitter().isPresent()) name += " (via MC/Proxy)";
        else name += " (via Discord)";
        return Utility.embedBuilder()
                .addField("Punisher", name, true)
                .addField("Submitted", data.getSubmitted().toLocalDateTime().format(DateTimeFormatter.ofPattern("d MMM uuuu")), true)
                .addField("Expires", getDuration(data.getExpires().orElse(null)), true)
                .addField("Reason", data.getReason(), false)
                .build();
    }

    private static String getDuration(Timestamp date){
        Timestamp now = Timestamp.from(Instant.now());
        if (date == null) return "Never.";
        else if (!date.after(now)) return "Has already expired";
        long msRemaining = date.getTime() - now.getTime();

        long seconds = msRemaining / 1000;
        long minutes = seconds / 60;

        if (minutes == 0) return seconds + " seconds.";
        long hours = minutes / 60;
        seconds = seconds % 60;

        if (hours == 0) return minutes + " minutes, " + seconds + " seconds.";
        long days = hours / 24;
        minutes = minutes % 60;

        if (days == 0) return hours + " hours, " + minutes + " minutes.";
        hours = hours % 24;
        return days + " days, " + hours + " hours.";
    }

    @SafeVarargs
    private static <T> Optional<T> or(Supplier<Optional<T>>... optionals){
        for (Supplier<Optional<T>> supplier : optionals) {
            Optional<T> optional = supplier.get();
            if (optional.isPresent()) return optional;
        }
        return Optional.empty();
    }

    @SafeVarargs
    private static <T> T or(T t, Supplier<Optional<T>>... optionals){
        for (Supplier<Optional<T>> supplier : optionals) {
            Optional<T> optional = supplier.get();
            if (optional.isPresent()) return optional.get();
        }
        return t;
    }
}