package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
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

            final Optional<PermissionUtils.RankUpdate> success = PermissionUtils.INSTANCE.modifyRank(sourcePlayer, targetUUID, track, promote);
            DiscordLink.getInstance().getChannel().sendTo(sourcePlayer, buff->{
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
                    .map(PlatformUtils::getPlayer)
                    .ifPresent(Utility::setRoles);
        });
    }
}
