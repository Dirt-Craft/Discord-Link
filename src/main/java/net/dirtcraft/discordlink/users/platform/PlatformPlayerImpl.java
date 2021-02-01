package net.dirtcraft.discordlink.users.platform;


import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public class PlatformPlayerImpl extends PlatformUserImpl implements PlatformPlayer {
    private final Player player;

    PlatformPlayerImpl(Player player){
        super(player);
        this.player = player;
    }

    @Override
    public String getNameAndPrefix(){
        return getPrefix()
                .map(pre->pre + " " + getName())
                .orElse(getName());
    }

    @Override
    public String getName(){
        return player.getName();
    }

    @Override
    public Optional<String> getPrefix(){
        if (player.getOption("prefix").isPresent()) return player.getOption("prefix");
        else return PermissionProvider.INSTANCE.getPrefix(player.getUniqueId());
    }

    @Override
    public UUID getUUID(){
        return player.getUniqueId();
    }

    @Override
    public boolean isVanished(){
        return player.get(Keys.VANISH).orElse(false);
    }

    @Override
    public boolean notVanished(){
        return !isVanished();
    }

    @Override
    public boolean hasPlayedBefore(){
        return player.hasPlayedBefore();
    }

    @Override
    public boolean hasPermission(String perm){
        return player.hasPermission(perm);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOnlinePlayer(){
        return (T) player;
    }

    public Player getPlayer(){
        return player;
    }
}