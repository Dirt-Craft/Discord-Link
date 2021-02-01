package net.dirtcraft.discordlink.events;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.storage.Settings;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Identifiable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PluginMessageHandler implements RawDataListener {
    @Override
    @SuppressWarnings("NullableProblems")
    public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {
            final String type = data.readUTF();
            if (type.equalsIgnoreCase(Settings.PROMOTION_CHANNEL)) handlePromotionPayload(data);
            else if (type.equalsIgnoreCase(Settings.ROLES_CHANNEL)) handleRolesPayload(data);
    }

    private void handlePromotionPayload(ChannelBuf in){
        final String secret = in.readUTF();
        final UUID source = UUID.fromString(in.readUTF());
        final String target = in.readUTF();
        final String track = in.readUTF();
        final boolean promote = in.readBoolean();

        CompletableFuture.runAsync(()->{
            Player sourcePlayer = Sponge.getServer().getPlayer(source).orElse(null);
            if (sourcePlayer == null) return;

            final UUID targetUUID = Sponge.getServiceManager()
                    .getRegistration(UserStorageService.class)
                    .map(ProviderRegistration::getProvider)
                    .flatMap(uss->uss.get(target))
                    .map(Identifiable::getUniqueId)
                    .orElse(null);

            final Optional<PermissionProvider.RankUpdate> success = PermissionProvider.INSTANCE.modifyRank(sourcePlayer, targetUUID, track, promote);
            DiscordLink.get().getChannel().sendTo(sourcePlayer, buff->{
                buff.writeUTF(Settings.PROMOTION_CHANNEL);
                buff.writeUTF(secret);
                buff.writeBoolean(success.isPresent());
                success.ifPresent(result->{
                    buff.writeUTF(result.target.toString());
                    buff.writeUTF(result.added == null? "null" : result.added);
                    buff.writeUTF(result.removed == null? "null" : result.removed);
                });
            });
        });
    }

    private void handleRolesPayload(ChannelBuf in){
        final UUID target = UUID.fromString(in.readUTF());
        CompletableFuture.runAsync(()->{
            Sponge.getServer().getPlayer(target)
                    .map(PlatformProvider::getPlayer)
                    .ifPresent(Utility::setRoles);
        });
    }
}
