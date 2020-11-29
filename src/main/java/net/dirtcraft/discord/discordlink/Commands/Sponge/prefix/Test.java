package net.dirtcraft.discord.discordlink.Commands.Sponge.prefix;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Test implements CommandExecutor {

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
        final Optional<String> caratColor = args.<String>getOne("ArrowColor");
        final Optional<String> optBracketColor = args.<String>getOne("BracketColor");

        if (title.length() > 20 && !src.hasPermission(Permission.PREFIX_LONG)) throw new CommandException(Text.of("§cThe prefix specified is too long."));
        if (isNotAllowed(title) && !src.hasPermission(Permission.ROLES_STAFF)) throw new CommandException(Text.of("§cThat prefix is not allowed."));
        if (caratColor.isPresent() && !src.hasPermission(Permission.PREFIX_ARROW)) throw new CommandException(Text.of("§cYou do not have permission to change the arrow colour."));
        if (caratColor.isPresent() && isNonColor(caratColor.get())) throw new CommandException(Text.of("§cInvalid arrow color."));
        if (optBracketColor.isPresent() && !src.hasPermission(Permission.PREFIX_BRACKETS)) throw new CommandException(Text.of("§cYou do not have permission to change the bracket colour."));
        if (optBracketColor.isPresent() && isNonColor(optBracketColor.get())) throw new CommandException(Text.of("§cInvalid bracket color."));

        String bracketColor = optBracketColor.orElse("&7");
        String chevron = getChevron(target, caratColor.orElse("&a&l"));
        String rankPrefix = Settings.STAFF_PREFIXES.entrySet().stream()
                .filter(p->target.hasPermission(p.getKey()))
                .findFirst()
                .map(s->String.format("%s[%s%s]", bracketColor, s.getValue(), bracketColor))
                .orElse(bracketColor);
        String prefix = String.format("%s %s[%s%s]&r", chevron, rankPrefix, title, bracketColor)
                .replaceAll("\\?\"", "");

        Text name = NucleusAPI.getNicknameService().flatMap(u->u.getNickname(target)).orElse(Text.of(target.getName()));
        src.sendMessage(Text.of(TextSerializers.FORMATTING_CODE.deserialize(prefix), Text.of(" "), name));
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
