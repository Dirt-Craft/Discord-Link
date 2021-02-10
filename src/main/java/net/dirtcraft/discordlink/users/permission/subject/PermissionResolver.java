package net.dirtcraft.discordlink.users.permission.subject;

public interface PermissionResolver {
    boolean hasPermission(String permission);
}
