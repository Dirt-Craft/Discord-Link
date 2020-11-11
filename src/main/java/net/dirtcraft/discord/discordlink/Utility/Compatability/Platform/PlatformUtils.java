package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlatformUtils {
    public static Optional<PlatformUser> getPlayerOffline(UUID uuid){
        return Sponge.getServiceManager().provide(UserStorageService.class)
                .flatMap(uss->uss.get(uuid))
                .map(PlatformUser::new);
    }

    public static Optional<PlatformPlayer> getPlayer(PlatformUser player){
        return player.getPlayer().map(PlatformPlayer::new);
    }

    public static PlatformPlayer getPlayer(Player player){
        return new PlatformPlayer(player);
    }

    public static List<PlatformPlayer> getPlayers(){
        return Sponge.getServer().getOnlinePlayers().stream()
                .map(PlatformPlayer::new)
                .collect(Collectors.toList());
    }

    public static boolean isGameReady(){
        return Sponge.getGame().getState() == GameState.SERVER_STARTED;
    }
}