package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;

public class Unstuck implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        Optional<? extends PlatformUser> target = args.isEmpty() || !source.hasRole(DiscordRoles.STAFF)? source.getPlayerData() : PlatformProvider.getPlayerOffline(args.get(0));
        String name = target.flatMap(PlatformUser::getNameIfPresent).orElseThrow(()->new DiscordCommandException("Could not locate players UUID."));
        Task.builder()
                .execute(() -> {
                    String command = String.format("spawn other %s", name);
                    Sponge.getCommandManager().process(source.getCommandSource(command), command);
                }).submit(DiscordLink.get());
    }
}
