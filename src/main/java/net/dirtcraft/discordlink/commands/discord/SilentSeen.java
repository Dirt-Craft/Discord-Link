package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;

public class SilentSeen implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        String target;
        if (args.isEmpty()){
            Optional<? extends PlatformUser> optUser = source.getPlayerData();
            if (!optUser.isPresent()) throw new DiscordCommandException("You must be verified in order to not specify a player!");
            else target = optUser.flatMap(PlatformUser::getNameIfPresent).get();
        } else {
            target = args.get(0);
        }
        String command = "seen " + target;
        ConsoleSource sender = source.getCommandSource(command);
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(sender, command))
                .submit(DiscordLink.get());
    }
}
