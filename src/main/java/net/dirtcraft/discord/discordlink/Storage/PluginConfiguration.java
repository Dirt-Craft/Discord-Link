package net.dirtcraft.discord.discordlink.Storage;

import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class PluginConfiguration {
    @Setting(value = "Main")
    private PluginConfiguration.Main main = new PluginConfiguration.Main();
    @Setting(value = "Format")
    private PluginConfiguration.Format format = new PluginConfiguration.Format();
    @Setting(value = "Embed")
    private PluginConfiguration.Embed embed = new PluginConfiguration.Embed();
    @Setting(value = "Roles")
    private PluginConfiguration.Roles roles = new PluginConfiguration.Roles();
    @Setting(value = "Commands")
    private PluginConfiguration.Command command = new PluginConfiguration.Command();
    @Setting(value = "Notifier")
    private PluginConfiguration.Notifier notifier = new PluginConfiguration.Notifier();



    @ConfigSerializable
    public static class Main {

        @Setting(value = "Silent-Console-Prefix", comment = "Prefix to use private console command")
        public static String consolePrivate = "#/";

        @Setting(value = "Console-Prefix", comment = "Prefix to use console command")
        public static String consolePublic = "/";

        @Setting(value = "Bot-Prefix", comment = "Prefix to use bot commands")
        public static String discordCommand = "!";

        @Setting(value = "Discord-Server-ID")
        public static String discordServerID = "269639757351354368";

        @Setting(value = "Discord-Server-ID")
        public static String gamechatChannelID = SpongeDiscordLib.getGamechatChannelID();

        @Setting(value = "Discord-Server-ID")
        public static String serverLogChannelID = "566095634008899585";

    }

    @ConfigSerializable
    public static class Format {

        @Setting(value = "Discord-To-Server", comment = "Message format from Discord to server")
        public static String discordToServer = "&9&l» &7[&9Discord&7] &7{username}&8:&r {message}";

        @Setting(value = "Server-To-Discord", comment = "Message format from server to Discord")
        public static String serverToDiscord = "**{prefix} {username}**: {message}";

        @Setting(value = "Server-Start", comment = "Message to Discord when the server has started")
        public static String serverStart = "**{modpack}** has successfully started!";

        @Setting(value = "Server-Stop", comment = "Message to Discord when the server is stopping")
        public static String serverStop = "**{modpack}** is now restarting...";

        @Setting(value = "Player-Join", comment = "Message to Discord when a player joins the server")
        public static String playerJoin = "`{prefix} {username} has joined the game`";

        @Setting(value = "New-Player-Join", comment = "Message to Discord when a NEW player joins the server")
        public static String newPlayerJoin = "Welcome **{username}** to DirtCraft!";

        @Setting(value = "Player-Disconnect", comment = "Message to Discord when a player leaves the server")
        public static String playerDisconnect = "`{prefix} {username} has left the game`";

    }

    @ConfigSerializable
    public static class Embed {

        @Setting(value = "Title")
        public static String title = "<:redbulletpoint:539273059631104052>**DirtCraft ChatBot**<:redbulletpoint:539273059631104052>";

        @Setting(value = "Timestamp")
        public static boolean timestamp = true;

    }

    @ConfigSerializable
    public static class Roles {

        @Setting(value = "Owner-Role-ID")
        public static String ownerRoleID = "307551061156298762";

        @Setting(value = "Dirty-Role-ID")
        public static String dirtyRoleID = "591732856443895808";

        @Setting(value = "Admin-Role-ID")
        public static String adminRoleID = "531631265443479562";

        @Setting(value = "Moderator-Role-ID")
        public static String moderatorRoleID = "332701183477284867";

        @Setting(value = "Helper-Role-ID")
        public static String helperRoleID = "563538434333999108";

        @Setting(value = "Staff-Role-ID")
        public static String staffRoleID = "549039481450397699";

        @Setting(value = "Verified-Role-ID")
        public static String verifiedRoleID = "578447006662524940";

        @Setting(value = "Donator-Role-ID")
        public static String donatorRoleID = "591145069810155530";

        @Setting(value = "Nitro-Role-ID")
        public static String nitroRoleID = "581195961813172225";
    }

    @ConfigSerializable
    public static class Command {
        @Setting(value = "Proxy-Commands", comment = "A list of commands that will be processed by the proxy-link module (This list should match the list in said module)")
        public static List<String> ignored = Arrays.asList(
                "find",
                "ban",
                "tempban",
                "ipban",
                "unban",
                "mute",
                "tempmute",
                "ipmute",
                "unmute",
                "warn",
                "unwarn",
                "kick",
                "history",
                "staffhistory",
                "banlist",
                "warnings",
                "dupeip",
                "geoip",
                "checkban",
                "checkmute",
                "lastuuid"
        );

        @Setting(value = "Admin-Command-Blacklist", comment = "A list of commands admins are not allowed to use.")
        public static List<String> blacklist = Arrays.asList(
                "luckperms",
                "perm",
                "permissions",
                "perm",
                "lp",
                "execute",
                "nameban",
                "nameunban",
                "whitelist",
                "minecraft:",
                "sponge"
        );
    }

    @ConfigSerializable
    public static class Notifier{
        @Setting(value = "Boot-Notifier-Minutes", comment = "Max boot stage time (minutes) before sending DMs")
        public static long maxStageMinutes = 12;

        @Setting(value = "Notify-Users", comment = "Users to DM (Discord ID's)")
        public static List<Long> notify = new ArrayList<>(Arrays.asList(248056002274918400L, 261928443179040768L));
    }

}
