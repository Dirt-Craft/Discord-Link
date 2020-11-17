package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms;

import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import org.spongepowered.api.entity.living.player.User;

public abstract class LuckPermissions extends PermissionUtils {

    protected abstract String getServerContext();

    public void setPlayerPrefix(User target, String prefix){
        PlatformUtils.toConsole(String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, getServerContext()));
    }
}
