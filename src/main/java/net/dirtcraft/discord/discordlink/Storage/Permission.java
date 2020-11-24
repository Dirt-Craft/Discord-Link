package net.dirtcraft.discord.discordlink.Storage;

import net.md_5.bungee.api.CommandSender;

import java.util.Arrays;
import java.util.Objects;

public class Permission {
    public static final String PROMOTE_PERMISSION_GROUP_PREFIX = "discordlink.promote.";
    public static final String PROMOTE_PERMISSION = "discordlink.promote.use";

    public static final String PREFIX_USE = "discordlink.prefix.use";
    public static final String PREFIX_OTHERS = "discordlink.prefix.others";
    public static final String PREFIX_ARROW = "discordlink.prefix.arrow";
    public static final String PREFIX_BRACKETS = "discordlink.prefix.brackets";
    public static final String PREFIX_LONG = "discordlink.prefix.long";

    public static final String ROLES_MANAGER = "discordlink.roles.manager";
    public static final String ROLES_ADMIN = "discordlink.roles.admin";
    public static final String ROLES_MODERATOR = "discordlink.roles.moderator";
    public static final String ROLES_HELPER = "discordlink.roles.helper";
    public static final String ROLES_BUILDER = "discordlink.roles.builder";
    public static final String ROLES_STAFF = "discordlink.roles.staff";
    public static final String ROLES_DONOR = "discordlink.roles.donor";

    public static final String SETTINGS_MODIFY = "discordlink.settings.modify";

    public static boolean canModify(CommandSender sender, String... group){
        return Arrays.stream(group)
                .filter(Objects::nonNull)
                .map(s->PROMOTE_PERMISSION_GROUP_PREFIX+s)
                .allMatch(sender::hasPermission);
    }
}
