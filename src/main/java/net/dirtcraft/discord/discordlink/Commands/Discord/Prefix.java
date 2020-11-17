package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Prefix implements DiscordCommandExecutor {
    private final Map<String, String> staffPrefixMap = Stream.of(
            new Pair<>(Permission.ROLES_ADMIN,     "&4&lA"),
            new Pair<>(Permission.ROLES_MODERATOR, "&9&lM"),
            new Pair<>(Permission.ROLES_HELPER,    "&5&lH"),
            new Pair<>(Permission.ROLES_BUILDER,   "&6&lB")
    ).collect(Collectors.toMap(Pair::getKey, Pair::getValue));

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        final User target = source.getPlayerData()
                .map(PlatformUser::getUser)
                .map(u->getTarget(u, args).orElse(u))
                .orElseThrow(()->new DiscordCommandException("No player present for Discord User."));
        String arrow = getChevron(target, args);
        String color = getColor(args);
        if (args.isEmpty()) throw new DiscordCommandException("You must specify a prefix");
        else if (args.size() == 1 && args.get(0).equalsIgnoreCase("none")){
            PermissionUtils.INSTANCE.clearPlayerPrefix(target);
        }
        String rankPrefix = staffPrefixMap.entrySet().stream()
                .filter(p->target.hasPermission(p.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(s->String.format("%s[%s%s]", color, s, color))
                .orElse(color);
        String title = String.join(" ", args);

        String prefix = String.format("%s %s[%s%s]&r", arrow, rankPrefix, title, color).replaceAll("\\?\"", "");
        PermissionUtils.INSTANCE.setPlayerPrefix(target, prefix);
    }

    private Optional<User> getTarget(Subject source, List<String> args) {
        if (args.isEmpty() || !args.get(0).matches("(?i)^[a-z0-9_]{3,16}$")) return Optional.empty();
        Optional<User> target = Sponge.getServiceManager()
                .provide(UserStorageService.class)
                .flatMap(uss->uss.get(args.get(0)));
        target.ifPresent(t->args.remove(0));
        return source.hasPermission(Permission.PREFIX_OTHERS)? target : Optional.empty();
    }

    private String getChevron(User user, List<String> args){
        String carat = !ignoreDonor(args) && user.hasPermission(Permission.ROLES_DONOR) && user.hasPermission(Permission.ROLES_STAFF)? "&l✯" : "&l»";
        ListIterator<String> argsIterator = args.listIterator();
        String chevronColour = "&a";
        while (argsIterator.hasNext()){
            String arg = argsIterator.next();
            if (arg.matches("(?i)--?a(rrow)?=([§&].)*$")) {
                chevronColour = arg.replaceAll("[^=]+=", "");
                argsIterator.remove();
                break;
            }
        }
        return chevronColour + carat;
    }

    private String getColor(List<String> args){
        String defaultColour = "&7";
        ListIterator<String> argsIterator = args.listIterator();
        while (argsIterator.hasNext()){
            String arg = argsIterator.next();
            if (arg.matches("(?i)^--?c(olou?r)?=([§&].)*$")) {
                defaultColour = arg.replaceAll("[^=]+=", "");;
                argsIterator.remove();
                break;
            }
        }
        return defaultColour;
    }

    private boolean ignoreDonor(List<String> args){
        ListIterator<String> argsIterator = args.listIterator();
        while (argsIterator.hasNext()){
            String arg = argsIterator.next();
            if (arg.matches("(?i)^--?i(gnore)?$")) {
                argsIterator.remove();
                return true;
            }
        }
        return false;
    }
}
