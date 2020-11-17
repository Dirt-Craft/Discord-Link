package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Unstuck implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        Optional<PlatformUser> target = args.isEmpty() || !source.hasRole(Roles.MOD)? source.getPlayerData() : PlatformUtils.getPlayerOffline(UUID.fromString(args.get(0)));
        PlatformUser player = target.orElseThrow(()->new DiscordCommandException("Could not locate players UUID."));
        Task.builder() //-
                .execute(() -> {
                    String command = String.format("spawn other %s", player.getName());
                    Sponge.getCommandManager().process(source.getCommandSource(command), command);
                }).submit(DiscordLink.getInstance());
    }
}
