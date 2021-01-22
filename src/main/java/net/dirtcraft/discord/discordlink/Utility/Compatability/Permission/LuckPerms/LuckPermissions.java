package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;

public abstract class LuckPermissions extends PermissionUtils {

    protected abstract String getServerContext();

    @Override
    public void setPlayerPrefix(ConsoleSource source, PlatformUser target, String prefix){
        PlatformUtils.toConsole(source, String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, getServerContext()));
    }

    @Override
    public void clearPlayerPrefix(ConsoleSource source, PlatformUser target){
        PlatformUtils.toConsole(source, String.format("lp user %s meta clear prefix server=%s", target.getName(), getServerContext()));
    }

    @Override
    public void setPlayerPrefix(MessageSource source, PlatformUser target, String prefix){
        String command = String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, getServerContext());
        ConsoleSource sender = source.getCommandSource(command);
        PlatformUtils.toConsole(sender, command);
    }

    @Override
    public void clearPlayerPrefix(MessageSource source, PlatformUser target){
        String command = String.format("lp user %s meta clear prefix server=%s", target.getName(), getServerContext());
        ConsoleSource sender = source.getCommandSource(command);
        PlatformUtils.toConsole(sender, command);
    }
}
