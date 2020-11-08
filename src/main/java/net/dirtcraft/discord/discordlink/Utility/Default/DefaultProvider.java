package net.dirtcraft.discord.discordlink.Utility.Default;

import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.PermissionUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DefaultProvider extends PermissionUtils {
    @Override
    public void execute(PlatformUser user) {
        //GameChat.sendMessage("This version of luckperms is not supported!");
    }

    public Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote) {
        if (source != null) source.sendMessage("This version of luckperms is not supported!");
        return Optional.empty();
    }

    public boolean addRank(UUID target, String group){
        return false;
    }

    public boolean removeRank(UUID target, String group){
        return false;
    }
}
