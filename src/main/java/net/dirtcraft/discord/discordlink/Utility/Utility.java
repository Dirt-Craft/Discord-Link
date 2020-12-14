package net.dirtcraft.discord.discordlink.Utility;

import net.dirtcraft.discord.discordlink.API.*;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.spongediscordlib.DiscordUtil;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.blacklist;
import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.ignored;

public class Utility {

    public static final String STRIP_CODE_REGEX = "[§&]([0-9a-fA-FrlonmkRLONMK])";
    public static final String URL_DETECT_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

    public static Optional<Member> getMemberById(String id){
        try {
            return Optional.of(Channels.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMemberById(long id){
        try {
            return Optional.of(Channels.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMember(User user){
        try {
            return Optional.of(Channels.getGuild().retrieveMember(user).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static EmbedBuilder embedBuilder() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(PluginConfiguration.Embed.title);
        if (PluginConfiguration.Embed.timestamp) {
            embed.setTimestamp(Instant.now());
        }
        return embed;
    }

    public static void setTopic() {
        TextChannel channel = Channels.getDefaultChannel();
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            String name = SpongeDiscordLib.getServerName().split(" ")[1];
            String code = SpongeDiscordLib.getServerName().toLowerCase().split(" ")[1];
            switch (code) {
                case "redstone":
                    code = "red";
                    break;
                case "glowstone":
                    code = "glow";
                    break;
                default:
                case "lapiz":
                    break;
            }
            channel.getManager()
                    .setTopic("**Pixelmon " + name + "** — IP: " + code + ".pixelmon.gg")
                    .queue();
            return;
        }
        String code = channel.getName().split("-")[1];

        channel.getManager()
                .setTopic("ModPack: **" + SpongeDiscordLib.getServerName() + "** — IP: " + code + ".dirtcraft.gg")
                .queue();
    }

    public static void setRoles(PlatformPlayer player){
        CompletableFuture.runAsync(()-> GuildMember.fromPlayerId(player.getUUID())
                .ifPresent(member->setRoles(player, member)));
    }

    public static void setRoles(PlatformPlayer player, GuildMember member) {
        Guild guild = Channels.getGuild();
        if (player.hasPermission(Permission.ROLES_DONOR)) setRoleIfAbsent(guild, member, Roles.DONOR);
        if (!member.getRoles().contains(Roles.STAFF.getRole())) tryChangeNickname(guild, member, player.getName());
        setRoleIfAbsent(guild, member, Roles.VERIFIED);
    }

    public static void setRoleIfAbsent(Guild guild, GuildMember member, Roles role){
        try {
            Role discordRole = role.getRole();
            if (discordRole == null || member.hasRole(role)) return;
            guild.addRoleToMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }

    public static void setRoleIfAbsent(long id, Roles role){
        try {
            Guild guild = Channels.getGuild();
            Member discord = guild.retrieveMemberById(id).complete();
            if (discord == null) return;
            GuildMember member = new GuildMember(discord);
            Role discordRole = role.getRole();
            if (discordRole == null || member.hasRole(role)) return;
            guild.addRoleToMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }


    public static void removeRoleIfPresent(Guild guild, GuildMember member, Roles role){
        try {
            Role discordRole = role.getRole();
            if (discordRole == null || !member.hasRole(role)) return;
            guild.removeRoleFromMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }

    public static void removeRoleIfPresent(long id, Roles role){
        try {
            Guild guild = Channels.getGuild();
            Member discord = guild.retrieveMemberById(id).complete();
            if (discord == null) return;
            GuildMember member = new GuildMember(discord);
            Role discordRole = role.getRole();
            if (discordRole == null || !member.hasRole(role)) return;
            guild.removeRoleFromMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }

    public static void tryChangeNickname(Guild guild, GuildMember member, String name){
        try {
            if (name == null) return;
            guild.modifyNickname(member, name).submit();
        } catch (Exception ignored){ }
    }

    public static void setStatus() {
        DiscordUtil.setStatus(Activity.ActivityType.STREAMING, SpongeDiscordLib.getServerName(), "https://www.twitch.tv/dirtcraft/");
    }

    public static boolean toConsole(String command, MessageSource sender, Action type) {
        if (ignored.stream().anyMatch(command::startsWith)) return false;
        if (canUseCommand(sender, command)) {
            final ConsoleSource commandSender = type.getCommandSource(sender, command);
            toConsole(commandSender, command);
            return true;
        } else {
            sendPermissionError(sender);
            return false;
        }
    }

    public static void toConsole(ConsoleSource commandSender, String command) {
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(commandSender, command))
                .submit(DiscordLink.getInstance());
    }

    private static boolean canUseCommand(GuildMember sender, String command){
        return sender.hasRole(Roles.DIRTY) ||
               sender.hasRole(Roles.ADMIN) && blacklist.stream().noneMatch(command::startsWith);
    }

    public static void sendPermissionError(MessageSource event){
        event.sendCommandResponse("<@" + event.getUser().getId() + ">, you do **not** have permission to use this command!", 5);
        logCommand(event, "__Tried Executing Command__");
    }

    public static void sendCommandError(MessageSource event, String msg){
        event.sendCommandResponse("<@" + event.getUser().getId() + ">, " + msg, 5);
        logCommand(event, "__Tried Executing Command__");
    }

    public static void logCommand(MessageSource event, String message){
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField(message, event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                        .build())
                .queue();
    }

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

    public static String sanitiseMinecraftText(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "")
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("([_*~`>|\\\\])", "\\\\$1")
                .replaceAll("<@\\d+>", "");
    }

    public static void dmExceptionAsync(Exception e, long... id){
        CompletableFuture.runAsync(()->dmException(e, id));
    }

    private static void dmException(Exception e, long... id){
        Arrays.stream(id)
                .mapToObj(Utility::getMemberById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Member::getUser)
                .map(User::openPrivateChannel)
                .forEach(dm->dmException(dm, e));
    }

    private static void dmException(RestAction<PrivateChannel> dms, Exception e){
        String[] ex = ExceptionUtils.getStackTrace(e).split("\\r?\\n");
        sendMessages(s->dms.queue(dm->dm.sendMessage(s).queue()), 1980, ex);
    }

    public static void sendMessages(Consumer<String> destination, int limit, String... messages){
        StringBuilder sb = new StringBuilder();
        for (String s : messages) {
            if (sb.length() + s.length() < limit){
                sb.append(s);
                sb.append("\n");
            } else {
                destination.accept(sb.toString());
                sb = new StringBuilder(s);
            }
        }
        if (sb.length() > 0) destination.accept(sb.toString());
    }

    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
}
