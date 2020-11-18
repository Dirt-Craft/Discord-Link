package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import net.dirtcraft.discord.discordlink.DiscordLink;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlatformUtils {
    public static final String VERSION = "Sponge-" + Sponge.getPlatform().getMinecraftVersion().getName();

    public static Optional<PlatformUser> getPlayerOffline(UUID uuid){
        return Sponge.getServiceManager().provide(UserStorageService.class)
                .flatMap(uss->uss.get(uuid))
                .map(PlatformUser::new);
    }

    public static Optional<PlatformUser> getPlayerOffline(String identifier){
        boolean isUUID = identifier.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        return Sponge.getServiceManager().provide(UserStorageService.class)
                .flatMap(uss->isUUID? uss.get(UUID.fromString(identifier)): uss.get(identifier))
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

    public static void toConsole(String command) {
        toConsole(Sponge.getServer().getConsole(), command);
    }

    public static void toConsole(CommandSource source, String command) {
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(source, command))
                .submit(DiscordLink.getInstance());
    }

    public static boolean isGameReady(){
        return Sponge.getGame().getState() == GameState.SERVER_STARTED;
    }

}