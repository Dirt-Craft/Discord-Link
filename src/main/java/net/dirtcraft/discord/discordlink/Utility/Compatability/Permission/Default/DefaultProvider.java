package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DefaultProvider extends PermissionUtils {
    @Override
    public void printUserGroups(MessageSource source, User user) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public void printUserKits(MessageSource source, User player) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public void setPlayerPrefix(User target, String prefix) {

    }

    @Override
    public void clearPlayerPrefix(User target) {

    }

    @Override
    public Optional<String> getPrefix(UUID uuid) {
        return Optional.empty();
    }

    public Optional<RankUpdate> modifyRank(@Nullable Player source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote) {
        if (source != null) source.sendMessage(Text.of("This version of luckperms is not supported!"));
        return Optional.empty();
    }
}
