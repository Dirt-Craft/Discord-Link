package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.exceptions.DiscordPermissionException;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Optional;

public class Kits implements DiscordCommandExecutor {
    private PermissionProvider provider;

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (provider == null) {
            provider = PermissionProvider.INSTANCE;
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
        } else provider.printUserKits(source, player.map(PlatformUser::<User>getOfflinePlayer).get());
    }

    private Optional<PlatformUser> parseIdentifier(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return DiscordLink.get()
                    .getUserManager()
                    .getMember(discordId)
                    .flatMap(DiscordMember::getPlayerData);
        } else {
            return PlatformProvider.getPlayerOffline(s);
        }
    }
}
