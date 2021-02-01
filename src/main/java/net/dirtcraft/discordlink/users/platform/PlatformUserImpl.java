package net.dirtcraft.discordlink.users.platform;

import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.UUID;

public class PlatformUserImpl implements PlatformUser {
    private final User user;

    PlatformUserImpl(User user){
        this.user = user;
    }

    @Override
    public Optional<String> getNameIfPresent(){
        return Optional.of(user.getName());
    }

    @Override
    public UUID getUUID(){
        return user.getUniqueId();
    }

    @Override
    public boolean isOnline(){
        return user.isOnline();
    }

    @Override
    public Optional<PlatformPlayer> getPlatformPlayer(){
        return user.getPlayer().map(PlatformPlayerImpl::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOfflinePlayer(){
        return (T) user;
    }

    public User getUser(){
        return user;
    }

    public Optional<Player> getPlayerIfOnline(){
        return user.getPlayer();
    }
}