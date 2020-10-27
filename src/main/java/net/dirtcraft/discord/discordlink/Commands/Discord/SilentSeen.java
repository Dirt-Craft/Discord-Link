package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.PrivateSender;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;

public class SilentSeen implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        String target;
        if (args.isEmpty()){
            Optional<User> optUser = source.getSpongeUser();
            if (!optUser.isPresent()) throw new DiscordCommandException("You must be verified in order to not specify a player!");
            else target = optUser.get().getName();
        } else {
            target = args.get(0);
        }
        String command = "seen " + target;
        PrivateSender sender = new PrivateSender(source, command);
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(sender, command))
                .submit(DiscordLink.getInstance());
    }
}
