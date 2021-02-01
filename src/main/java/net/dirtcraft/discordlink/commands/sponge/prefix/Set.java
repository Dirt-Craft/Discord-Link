package net.dirtcraft.discordlink.commands.sponge.prefix;

import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.storage.Permission;
import net.dirtcraft.discordlink.storage.Settings;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Set implements CommandExecutor {

    private final java.util.Set<String> forbidden = new HashSet<>(Arrays.asList("staff", "helper", "mod", "moderator", "admin", "manager", "owner"));

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        CompletableFuture.runAsync(()->{
            try {
                final User target = args.<User>getOne("Target").orElseThrow(()->new CommandException(Text.of("§cYou must specify a target.")));
                if (target != src && !src.hasPermission(Permission.PREFIX_OTHERS)) throw new CommandException(Text.of("§cYou do not have permission to set other players prefixes."));
                setPrefix(src, target, args);
            } catch (CommandException e){
                if (e.getText() != null) src.sendMessage(e.getText());
                else src.sendMessage(Text.of("§cAn error has occurred during execution."));
            }
        });

        return CommandResult.success();
    }

    private void setPrefix(@Nonnull CommandSource src, User target, @Nonnull CommandContext args) throws CommandException{
        final String title = args.<String>getOne("Prefix").orElseThrow(()->new CommandException(Text.of("§cYou must specify a prefix.")));
        final Optional<Boolean> staff = args.<Boolean>getOne("s").filter(x->src.hasPermission(Permission.PREFIX_INDICATOR));
        final Optional<String> caratColor = args.<String>getOne("ArrowColor").filter(x->src.hasPermission(Permission.PREFIX_ARROW));
        final Optional<String> optBracketColor = args.<String>getOne("BracketColor").filter(x->src.hasPermission(Permission.PREFIX_BRACKETS));

        if (title.length() > 20 && !src.hasPermission(Permission.PREFIX_LONG)) throw new CommandException(Text.of("§cThe prefix specified is too long."));
        if (isNotAllowed(title) && !src.hasPermission(Permission.ROLES_STAFF)) throw new CommandException(Text.of("§cThat prefix is not allowed."));
        if (optBracketColor.isPresent() && isNonColor(optBracketColor.get())) throw new CommandException(Text.of("§cInvalid bracket color."));
        if (caratColor.isPresent() && isNonColor(caratColor.get())) throw new CommandException(Text.of("§cInvalid arrow color."));

        String bracketColor = optBracketColor.orElse("&7");
        String chevron = getChevron(target, caratColor.orElse("&a&l"));
        String rankPrefix = staff.isPresent()? bracketColor: Settings.STAFF_PREFIXES.entrySet().stream()
                .filter(p->target.hasPermission(p.getKey()))
                .findFirst()
                .map(s->String.format("%s[%s%s]", bracketColor, s.getValue(), bracketColor))
                .orElse(bracketColor);
        String prefix = String.format("%s %s[%s%s]&r", chevron, rankPrefix, title, bracketColor)
                .replaceAll("\\?\"", "");

        PermissionProvider.INSTANCE.setPlayerPrefix(getSource(src), target, prefix);
    }

    private String getChevron(User user, String colour){
        final String carat = user.hasPermission(Permission.ROLES_DONOR) ? "&l✯" : "&l»";
        return colour + carat;
    }

    private boolean isNonColor(String input){
        return !input.matches("(?i)([§&][0-9a-frlonm]){1,5}");
    }

    private boolean isNotAllowed(String title){
        String raw = title.replaceAll("(?i)([§&][0-9a-frlonm])", "").toLowerCase();
        return forbidden.contains(raw);
    }

    private ConsoleSource getSource(CommandSource source){
        return new ConsoleSource(){
            @Override
            public void sendMessage(@Nonnull Text message) {
                source.sendMessage(message);
            }
        };
    }
}
