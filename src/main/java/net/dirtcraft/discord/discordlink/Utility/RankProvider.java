package net.dirtcraft.discord.discordlink.Utility;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.context.ContextSet;
import net.dirtcraft.discord.discordlink.API.GameChat;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class RankProvider {

    public final static RankProvider INSTANCE = getRank();

    public abstract void execute(User player);

    private static RankProvider getRank(){
        try {
            Class.forName("me.lucko.luckperms.api.LuckPermsApi");
            return new Api4();
        } catch (ClassNotFoundException ignored){}
        return new Null();
    }

    public static class Null extends RankProvider {
        @Override
        public void execute(User user) {
            GameChat.sendMessage("This version of luckperms is not supported!");
        }
    }

    public static class Api4 extends RankProvider {
        private LuckPermsApi api = me.lucko.luckperms.LuckPerms.getApi();
        private ContextSet contexts = api.getContextManager().getStaticContexts().getContexts();

        @Override
        public void execute(User player) {
            me.lucko.luckperms.api.manager.UserManager userManager = api.getUserManager();
            CompletableFuture<me.lucko.luckperms.api.User> userFuture = userManager.loadUser(player.getUniqueId());
            userFuture.whenComplete((user, throwable)->{
                List<String> perms = user.getAllNodes().stream()
                        .filter(Node::isGroupNode)
                        .filter(n->n.getFullContexts().isSatisfiedBy(contexts))
                        .map(n->n.getPermission() + (n.appliesGlobally()? " [G]" : ""))
                        .map(n->n.substring(6))
                        .collect(Collectors.toList());
                GameChat.sendEmbed(player.getName() + "'s Groups:", String.join("\n", perms));
            });
        }
    }
}
