package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordPermissionException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Ranks implements DiscordCommandExecutor {
    private PermissionUtils provider;

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (provider == null) {
            provider = PermissionUtils.INSTANCE;
        }

        Optional<PlatformUser> player;

        if (args.isEmpty()){
            player = source.getPlayerData();
        } else {
            if (!source.isStaff()) throw new DiscordPermissionException();
            player = parseIdentifier(args.get(0));
        }

        if (!player.isPresent()) {
            String response = args.isEmpty()? "You are not correctly verified, or have not played on this server." : "Invalid user. Either the user does not exist or they have never played on this server.";
            source.sendCommandResponse(response, 30);
        } else provider.printUserGroups(source, player.map(PlatformUser::getUser).get());
    }

    private Optional<PlatformUser> parseIdentifier(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return GuildMember.fromDiscordId(discordId)
                    .flatMap(GuildMember::getPlayerData);
        } else {
            return PlatformUtils.getPlayerOffline(s);
        }
    }
}
