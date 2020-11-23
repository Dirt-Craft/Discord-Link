package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;

public class SilentSeen implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        String target;
        if (args.isEmpty()){
            Optional<PlatformUser> optUser = source.getPlayerData();
            if (!optUser.isPresent()) throw new DiscordCommandException("You must be verified in order to not specify a player!");
            else target = optUser.flatMap(PlatformUser::getName).get();
        } else {
            target = args.get(0);
        }
        String command = "seen " + target;
        ConsoleSource sender = source.getCommandSource(command);
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(sender, command))
                .submit(DiscordLink.getInstance());
    }
}
