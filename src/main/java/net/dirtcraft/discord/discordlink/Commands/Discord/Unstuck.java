package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.GamechatSender;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.Optional;

public class Unstuck implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, List<String> args, MessageReceivedEvent event) throws DiscordCommandException {
        Optional<User> target = args.isEmpty() || !source.hasRole(Roles.MOD)? source.getSpongeUser() : Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(args.get(0));
        User player = target.orElseThrow(()->new DiscordCommandException("Could not locate players UUID."));
        Task.builder() //-
                .execute(() -> {
                    String command = String.format("spawn other %s", player.getName());
                    Sponge.getCommandManager().process(new GamechatSender(source, command), command);
                }).submit(DiscordLink.getInstance());
    }
}
