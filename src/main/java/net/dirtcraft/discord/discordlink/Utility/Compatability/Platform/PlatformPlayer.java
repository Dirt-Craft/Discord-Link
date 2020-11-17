package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;


import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class PlatformPlayer {
    private final Player player;

    PlatformPlayer(Player player){
        this.player = player;
    }

    public String getNameAndPrefix(){
        return getPrefix()
                .map(pre->pre + " " + getName())
                .orElse(getName());
    }

    public String getName(){
        return player.getName();
    }

    public Optional<String> getPrefix(){
        if (player.getOption("prefix").isPresent()) return player.getOption("prefix");
        else return PermissionUtils.INSTANCE.getPrefix(player.getUniqueId());
    }

    public UUID getUUID(){
        return player.getUniqueId();
    }

    public Player getPlayer(){
        return player;
    }

    public boolean isVanished(){
        return player.get(Keys.VANISH).orElse(false);
    }

    public boolean notVanished(){
        return !isVanished();
    }

    public boolean hasPlayedBefore(){
        return player.hasPlayedBefore();
    }

    public boolean hasPermission(String group){
        return player.hasPermission(group);
    }
}