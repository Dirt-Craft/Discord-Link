package net.dirtcraft.discordlink.users.permission.dummy;

import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.users.permission.subject.PermissionResolver;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DefaultProvider extends PermissionProvider {
    @Override
    public void printUserGroups(MessageSource source, User user) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public void printUserKits(MessageSource source, User player) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public void setPlayerPrefix(ConsoleSource source, User target, String prefix) {

    }

    @Override
    public void clearPlayerPrefix(ConsoleSource source, User target) {

    }

    @Override
    public void setPlayerPrefix(MessageSource source, User target, String prefix){
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public void clearPlayerPrefix(MessageSource source, User target){
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public Optional<String> getPrefix(UUID uuid) {
        return Optional.empty();
    }

    public Optional<RankUpdate> modifyRank(@Nullable Player source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote) {
        if (source != null) source.sendMessage(Text.of("This version of luckperms is not supported!"));
        return Optional.empty();
    }

    @Override
    public Optional<PermissionResolver> getPermission(UUID uuid) {
        return Optional.empty();
    }
}
