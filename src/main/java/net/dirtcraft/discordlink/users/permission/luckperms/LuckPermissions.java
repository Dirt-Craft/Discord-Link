package net.dirtcraft.discordlink.users.permission.luckperms;

import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.User;

public abstract class LuckPermissions extends PermissionProvider {

    protected abstract String getServerContext();

    @Override
    public void setPlayerPrefix(ConsoleSource source, User target, String prefix){
        PlatformProvider.toConsole(source, String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, getServerContext()));
    }

    @Override
    public void clearPlayerPrefix(ConsoleSource source, User target){
        PlatformProvider.toConsole(source, String.format("lp user %s meta clear prefix server=%s", target.getName(), getServerContext()));
    }

    @Override
    public void setPlayerPrefix(MessageSource source, User target, String prefix){
        String command = String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, getServerContext());
        ConsoleSource sender = source.getCommandSource(command);
        PlatformProvider.toConsole(sender, command);
    }

    @Override
    public void clearPlayerPrefix(MessageSource source, User target){
        String command = String.format("lp user %s meta clear prefix server=%s", target.getName(), getServerContext());
        ConsoleSource sender = source.getCommandSource(command);
        PlatformProvider.toConsole(sender, command);
    }
}
