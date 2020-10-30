package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordPermissionException;
import net.dirtcraft.discord.discordlink.Utility.RankProvider;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.Optional;

public class Ranks implements DiscordCommandExecutor {
    private RankProvider provider;

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (Sponge.getGame().getState() != GameState.SERVER_STARTED){
            GameChat.sendMessage("Sorry, The server has not started yet.");
            return;
        } else if (provider == null) {
            provider = RankProvider.INSTANCE;
        }

        Optional<User> player;

        if (args.isEmpty()){
            player = source.getSpongeUser();
        } else {
            if (!source.isStaff()) throw new DiscordPermissionException();
            player = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(args.get(0));
        }

        if (!player.isPresent()) {
            String response = args.isEmpty()? "You are not correctly verified, or have not played on this server." : "Invalid user. Either the user does not exist or they have never played on this server.";
            GameChat.sendMessage(response, 30);
        } else provider.execute(player.get());
    }
}
