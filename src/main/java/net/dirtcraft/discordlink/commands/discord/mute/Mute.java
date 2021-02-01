package net.dirtcraft.discordlink.commands.discord.mute;

import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.Database;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Mute implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        DiscordLink discordLink = DiscordLink.get();
        UserManagerImpl userManager = discordLink.getUserManager();
        DiscordMember target = removeIfPresent(args, userManager::getMember)
                .orElseThrow(() -> new DiscordCommandException("Discord user not specified."));
        Database storage = discordLink.getStorage();
        Timestamp expires = getExpireDate(args);
        String reason = args.isEmpty() ? "None given." : String.join(" ", args);
        target.setRoleIfAbsent(DiscordRoles.MUTED);
        storage.deactivateMute(source.getIdLong(), target.getIdLong());
        storage.registerMute(source.getIdLong(), target, expires, reason);
        String message = "You have been muted by " + source.getEffectiveName() + " for " +
                "```\n" + reason + "\n```" +
                "\nThe mute " + getDuration(expires) + "" +
                "\nFeel free to make an appeal in <#590388043379376158>.";
        target.sendPrivateMessage(message);

        source.sendCommandResponse(source.getAsMention() + " Mute has been applied to user " + target.getAsMention(), 30);
        storage.hasActiveMute(target.getIdLong())
                .ifPresent(data->source.sendCommandResponse(MuteInfo.getInfo(data), 30));
    }

    public Timestamp getExpireDate(List<String> args){
        if (args.isEmpty() || !args.get(0).matches("(?i)(\\d+)([smhdwy]|mo)")) return null;
        String base = args.remove(0);
        String duration = base.replaceAll("(?i)(\\d+)([smhdwy]|mo)", "$1");
        String unit = base.replaceAll("(?i)(\\d+)([smhdwy]|mo)", "$2");
        return Timestamp.from(Instant.now().plus(Long.parseLong(duration), getUnit(unit)));
    }

    public ChronoUnit getUnit(String input){
        switch (input){
            case "s": return ChronoUnit.SECONDS;
            case "m": return ChronoUnit.MINUTES;
            case "h": return ChronoUnit.HOURS;
            case "w": return ChronoUnit.WEEKS;
            case "mo": return ChronoUnit.MONTHS;
            case "y": return ChronoUnit.YEARS;
            default: return ChronoUnit.FOREVER;
        }
    }

    public String getDuration(Timestamp date){
        Timestamp now = Timestamp.from(Instant.now());
        if (date == null) return "is permanent";
        else if (!date.after(now)) return "has already expired";
        long msRemaining = date.getTime() - now.getTime();

        long seconds = msRemaining / 1000;
        long minutes = seconds / 60;

        if (minutes == 0) return "will expire in " + seconds + " seconds.";
        long hours = minutes / 60;
        seconds = seconds % 60;

        if (hours == 0) return "will expire in " + minutes + " minutes, " + seconds + " seconds.";
        long days = hours / 24;
        minutes = minutes % 60;

        if (days == 0) return "will expire in " + hours + " hours, " + minutes + " minutes.";
        hours = hours % 24;
        return "will expire in " + days + " days, " + hours + " hours.";
    }
}