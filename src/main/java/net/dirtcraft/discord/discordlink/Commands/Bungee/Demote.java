package net.dirtcraft.discord.discordlink.Commands.Bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.PermissionUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class Demote extends Command {
    public Demote() {
        super("demote");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length < 1) return;
        if (!sender.hasPermission(Permission.PROMOTE_PERMISSION)) sender.sendMessage(TextComponent.fromLegacyText("§cYou do not have permission to do that."));
        UUID secret = UUID.randomUUID();
        UUID player = ((ProxiedPlayer) sender).getUniqueId();
        String target =args[0];
        String track = args.length < 2 ? "staff" : args[1];
        DiscordLink.getInstance().getChannelHandler().registerCallback(secret, success->responseHandler((ProxiedPlayer) sender, target, success));
        sendPacket(secret, player, target, track, ((ProxiedPlayer) sender).getServer());
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendPacket(UUID secret, UUID source, String target, String track, Server server){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Settings.PROMOTION_CHANNEL);
        out.writeUTF(secret.toString());
        out.writeUTF(source.toString());
        out.writeUTF(target);
        out.writeUTF(track);
        out.writeBoolean(false);
        server.sendData(Settings.ROOT_CHANNEL, out.toByteArray());
    }

    private void responseHandler(ProxiedPlayer sender, String name, PermissionUtils.RankUpdate rankUpdate) {
        if (rankUpdate == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou do not have permission to demote this user."));
            return;
        }
        PermissionUtils perms = PermissionUtils.INSTANCE;
        if (rankUpdate.added != null) perms.addRank(rankUpdate.target, rankUpdate.added);
        if (rankUpdate.removed != null) perms.removeRank(rankUpdate.target, rankUpdate.removed);
        sender.sendMessage(TextComponent.fromLegacyText("§2Successfully §c§ldemoted §6" + name + " §2via Bungee!"));
    }
}
