package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.Track;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.context.ContextSet;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.dirtcraft.discord.discordlink.Storage.Permission.PROMOTE_PERMISSION_GROUP_PREFIX;

public class Api4 extends LuckPermissions {
    @NonNull
    private LuckPermsApi api = me.lucko.luckperms.LuckPerms.getApi();
    private ContextSet contexts = api.getContextManager().getStaticContexts().getContexts();

    @Override
    public void printUserGroups(MessageSource source, User player) {
        me.lucko.luckperms.api.manager.UserManager userManager = api.getUserManager();
        StringBuilder local = new StringBuilder("__**Local**__\n");
        StringBuilder remote = new StringBuilder("__**Other Servers**__\n");
        userManager.loadUser(player.getUniqueId()).join().getPermissions().stream()
                .filter(n -> n.getPermission().startsWith("group."))
                .filter(n -> !n.getPermission().endsWith(".default"))
                .forEach(n-> sortToString(n, local, remote, 6));

        if (local.toString().equalsIgnoreCase("__**Local**__\n")) remote.append("None found.\n");
        if (remote.toString().equalsIgnoreCase("__**Other Servers**__\n")) remote.append("None found.");
        else remote.deleteCharAt(remote.length()-1);

        source.sendCommandResponse(player.getName() + "'s Kits:", String.join("\n", local, remote));
    }

    @Override
    public void printUserKits(MessageSource source, User player) {
        me.lucko.luckperms.api.manager.UserManager userManager = api.getUserManager();
        StringBuilder local = new StringBuilder("__**Local**__\n");
        StringBuilder remote = new StringBuilder("__**Other Servers**__\n");
        userManager.loadUser(player.getUniqueId()).join().getPermissions().stream()
                .filter(n -> n.getPermission().startsWith("nucleus.kits."))
                .forEach(n-> sortToString(n, local, remote, 13));

        if (local.toString().equalsIgnoreCase("__**Local**__\n")) remote.append("None found.\n");
        if (remote.toString().equalsIgnoreCase("__**Other Servers**__\n")) remote.append("None found.");
        else remote.deleteCharAt(remote.length()-1);

        source.sendCommandResponse(player.getName() + "'s Kits:", String.join("\n", local, remote));
    }

    public void sortToString(Node n, StringBuilder local, StringBuilder remote, int trim){
        if (n.isServerSpecific() && n.getFullContexts().isSatisfiedBy(contexts)) {
            local.append(n.getPermission().substring(trim))
                    .append("\n");
        } else if (n.appliesGlobally()) {
            local.append(n.getPermission().substring(trim))
                    .append(" *[global]*\n");
        } else {
            remote.append(n.getPermission().substring(trim))
                    .append(" *[")
                    .append(n.getFullContexts().getAnyValue("server").orElse("unknown"))
                    .append("]*\n");
        }
    }

    public Optional<RankUpdate> modifyRank(@Nullable Player source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote){
        Optional<me.lucko.luckperms.api.User> target = Optional.ofNullable(targetUUID)
                .map(api.getUserManager()::loadUser)
                .map(CompletableFuture::join);
        Optional<Track> track = Optional.ofNullable(trackName)
                .map(api.getTrackManager()::getTrack);

        if (source == null || !target.isPresent() || !track.isPresent()) return Optional.empty();
        else if (promote) return promoteTarget(source, target.get(), track.get());
        else return demoteTarget(source, target.get(), track.get());
    }

    private Optional<RankUpdate> demoteTarget(Player source, me.lucko.luckperms.api.User targetUser, Track track) {
        try {
            List<String> groups = track.getGroups();
            SortedSet<? extends Node> targetNodes = targetUser.getPermissions();
            String previousGroup = "default";
            Node previousNode = null;
            for (int i = groups.size(); i > 0; ) {
                final String group = groups.get(--i);
                if (group.equalsIgnoreCase("default")) continue;
                final Node node = api.buildNode("group." + group).setServer(getServerContext()).build();
                if (targetNodes.contains(node)) {
                    previousGroup = group;
                    previousNode = node;
                } else if (previousNode != null && hasPermission(source, previousGroup)) {
                    setRank(targetUser, node, previousNode);
                    return Optional.of(new RankUpdate(targetUser.getUuid(), group, previousGroup));
                }
            }
            if (hasPermission(source, previousGroup)) {
                setRank(targetUser, null, previousNode);
                return Optional.of(new RankUpdate(targetUser.getUuid(), null, previousGroup));
            } else return Optional.empty();
        } catch (Throwable e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<RankUpdate> promoteTarget(Player source, me.lucko.luckperms.api.User targetUser, Track track) {
        List<String> groups = track.getGroups();
        SortedSet<? extends Node> targetNodes = targetUser.getPermissions();
        String previousGroup = "default";
        Node previousNode = null;
        for (int i = groups.size(); i > 0; ) {
            final String group = groups.get(--i);
            if (group.equalsIgnoreCase("default")) continue;
            final Node node = api.buildNode("group." + group).setServer(getServerContext()).build();
            if (!targetNodes.contains(node)) {
                previousGroup = group;
                previousNode = node;
            } else if (previousNode != null && hasPermission(source, previousGroup)) {
                setRank(targetUser, previousNode, node);
                return Optional.of(new RankUpdate(targetUser.getUuid(), previousGroup, group));
            } else return Optional.empty();
        }
        if (hasPermission(source, previousGroup)) {
            setRank(targetUser, previousNode, null);
            return Optional.of(new RankUpdate(targetUser.getUuid(), previousGroup, null));
        } else return Optional.empty();
    }

    private void setRank(me.lucko.luckperms.api.User target, Node add, Node remove){
        if (remove != null) target.unsetPermission(remove);
        if (add != null) target.setPermission(add);
        api.getUserManager().saveUser(target);
    }

    private boolean hasPermission(Player source, String group){
        return source.hasPermission(PROMOTE_PERMISSION_GROUP_PREFIX + group);
    }

    public String getServerContext(){
        return contexts.getAnyValue("server").orElse("global");
    }

    public Optional<String> getPrefix(UUID uuid){
        return Optional.ofNullable(api.getUserManager().getUser(uuid))
                .map(u->u.getCachedData().getMetaData(Contexts.global().setContexts(contexts)))
                .map(MetaData::getPrefix);
    }
}