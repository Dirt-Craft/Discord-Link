package net.dirtcraft.discord.discordlink.Utility.Permission.Luckperms;


import net.dirtcraft.discord.discordlink.Utility.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformUser;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.track.Track;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.dirtcraft.discord.discordlink.Storage.Permission.PROMOTE_PERMISSION_GROUP_PREFIX;

public class Api5 extends PermissionUtils {
    private final LuckPerms api = LuckPermsProvider.get();
    private final ImmutableContextSet contexts = api.getContextManager().getStaticContext();

    @Override
    public void execute(PlatformUser player) {
        UserManager userManager = api.getUserManager();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(player.getUUID());
        userFuture.whenComplete((user, throwable) -> {
            String local = getGroups(user, contexts, false);
            String remote = getGroups(user, contexts, true);
            String perms = "__**Local**__\n" + local + "\n\n__**Other Servers**__\n" + remote;
            //GameChat.sendEmbed(player.getName() + "'s Groups:", perms);
        });
    }

    private String getGroups(User user, ImmutableContextSet contexts, boolean negate){
        return user.getDistinctNodes().parallelStream()
                .filter(node->node.getType() == NodeType.INHERITANCE && !node.isNegated())
                .filter(node -> node.getContexts().isSatisfiedBy(contexts) ^ negate)
                .map(n -> "**" + n.getKey().substring(6) + "**" + n.getContexts()
                        .getAnyValue("server")
                        .map(s->negate?" *["+s+"]*":"")
                        .orElse(" *[global]*"))
                .collect(Collectors.joining("\n"));
    }

    public Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote){
        Optional<User> target = Optional.ofNullable(targetUUID)
                .map(api.getUserManager()::loadUser)
                .map(CompletableFuture::join);
        Optional<Track> track = Optional.ofNullable(trackName)
                .map(api.getTrackManager()::getTrack);

        if (source == null || !target.isPresent() || !track.isPresent()) return Optional.empty();
        else if (promote) return promoteTarget(source, target.get(), track.get());
        else return demoteTarget(source, target.get(), track.get());
    }

    private Optional<RankUpdate> demoteTarget(PlatformPlayer source, User targetUser, Track track) {
        List<String> groups = track.getGroups();
        NodeMap targetNodes = targetUser.data();
        String previousGroup = "default";
        Node previousNode = null;
        for (int i = groups.size(); i > 0; ) {
            final String group = groups.get(--i);
            final Node node = Node.builder("group." + group).build();
            if (targetNodes.contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                previousGroup = group;
                previousNode = node;
            } else if (previousNode != null && hasPermission(source, previousGroup)) {
                setRank(targetUser, node, previousNode);
                return Optional.of(new RankUpdate(targetUser.getUniqueId(), group, previousGroup));
            }
        }
        if (hasPermission(source, previousGroup)) {
            setRank(targetUser, null, previousNode);
            return Optional.of(new RankUpdate(targetUser.getUniqueId(), null, previousGroup));
        } else return Optional.empty();
    }

    private Optional<RankUpdate> promoteTarget(PlatformPlayer source, User targetUser, Track track) {
        List<String> groups = track.getGroups();
        NodeMap targetNodes = targetUser.data();
        String previousGroup = "default";
        Node previousNode = null;
        for (int i = groups.size(); i > 0; ) {
            final String group = groups.get(--i);
            final Node node = Node.builder("group." + group).build();
            if (!targetNodes.contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                previousGroup = group;
                previousNode = node;
            } else if (previousNode != null && hasPermission(source, previousGroup)) {
                setRank(targetUser, previousNode, node);
                return Optional.of(new RankUpdate(targetUser.getUniqueId(), previousGroup, group));
            }
        }
        if (hasPermission(source, previousGroup)) {
            setRank(targetUser, previousNode, null);
            return Optional.of(new RankUpdate(targetUser.getUniqueId(), previousGroup, null));
        } else return Optional.empty();
    }

    private void setRank(User target, Node add, Node remove){
        if (remove != null) target.data().remove(remove);
        if (add != null) target.data().add(add);
        LuckPermsProvider.get().getUserManager().saveUser(target);
    }

    private boolean hasPermission(PlatformPlayer source, String group){
        return source.hasPermission(PROMOTE_PERMISSION_GROUP_PREFIX + group);
    }

    public boolean addRank(UUID target, String group){
        final Node node = Node.builder("group." + group).build();
        User user = Optional.ofNullable(target)
                .map(api.getUserManager()::loadUser)
                .map(CompletableFuture::join)
                .orElse(null);
        if (user == null)  return false;
        user.data().add(node);
        LuckPermsProvider.get().getUserManager().saveUser(user);
        return true;
    }

    public boolean removeRank(UUID target, String group){
        final Node node = Node.builder("group." + group).build();
        User user = Optional.ofNullable(target)
                .map(api.getUserManager()::loadUser)
                .map(CompletableFuture::join)
                .orElse(null);
        if (user == null)  return false;
        user.data().remove(node);
        LuckPermsProvider.get().getUserManager().saveUser(user);
        return true;
    }
}
