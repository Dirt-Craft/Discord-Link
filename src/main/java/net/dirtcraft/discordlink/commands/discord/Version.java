package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.Settings;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.MessageSource;

import java.util.List;

public class Version implements DiscordCommandExecutor {

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        String version = "Discord-Link: " + Settings.VERSION;
        version += "\nPermission System: " + PermissionProvider.VERSION;
        version += "\nPlatform: " + PlatformProvider.VERSION;
        source.sendCommandResponse("Version Data", version);
    }
}
