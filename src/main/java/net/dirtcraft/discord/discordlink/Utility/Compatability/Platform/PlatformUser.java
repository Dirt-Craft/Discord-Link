package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.UUID;

public class PlatformUser {
    private final User user;

    PlatformUser(User user){
        this.user = user;
    }

    public Optional<String> getName(){
        return Optional.of(user.getName());
    }

    public UUID getUUID(){
        return user.getUniqueId();
    }

    public User getUser(){
        return user;
    }

    public boolean isOnline(){
        return user.isOnline();
    }

    Optional<Player> getPlayer(){
        return user.getPlayer();
    }
}