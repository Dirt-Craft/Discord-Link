package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default.DefaultProvider;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms.Api4;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms.Api5;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class PermissionUtils {

    public static String VERSION;

    public final static PermissionUtils INSTANCE = getRank();

    public abstract void printUserGroups(MessageSource source, User player);

    public abstract void printUserKits(MessageSource source, User player);

    public abstract void setPlayerPrefix(MessageSource source, User target, String prefix);

    public abstract void setPlayerPrefix(ConsoleSource source, User target, String prefix);

    public abstract void clearPlayerPrefix(MessageSource source, User target);

    public abstract void clearPlayerPrefix(ConsoleSource source, User target);

    public abstract Optional<String> getPrefix(UUID uuid);

    public abstract Optional<RankUpdate> modifyRank(@Nullable Player source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote);

    private static PermissionUtils getRank(){
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            VERSION = "LuckPerms API 5";
            return new Api5();
        } catch (ClassNotFoundException ignored){}
        try {
            Class.forName("me.lucko.luckperms.api.LuckPermsApi");
            VERSION = "LuckPerms API 4";
            return new Api4();
        } catch (ClassNotFoundException ignored){}
        VERSION = "None";
        return new DefaultProvider();
    }

    public static class RankUpdate{
        public final String added;
        public final String removed;
        public final UUID target;

        public RankUpdate(UUID target, String added, String removed){
            this.removed = removed != null && removed.equals("null") ? null : removed;
            this.added = added != null && added.equals("null") ? null : added;
            this.target = target;
        }
    }
}
