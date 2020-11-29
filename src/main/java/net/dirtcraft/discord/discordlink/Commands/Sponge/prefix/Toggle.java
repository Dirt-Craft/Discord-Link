package net.dirtcraft.discord.discordlink.Commands.Sponge.prefix;

import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Optional;

public class Toggle implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final User target = args.<User>getOne("Target").orElseThrow(() -> new CommandException(Text.of("§cYou must specify a target.")));
        if (target != src && !src.hasPermission(Permission.PREFIX_OTHERS)) throw new CommandException(Text.of("§cYou do not have permission to set other players prefixes."));
        else if (!target.hasPermission(Permission.ROLES_DONOR)) throw new CommandException(Text.of("Target is not a donor"));
        else {
            Optional<String> prefix = target.getOption("prefix");
            prefix.ifPresent(pre -> {
                if (pre.matches("^(?i)([§&][0-9a-frlonm]){1,5}».*$")) pre = pre.replaceFirst("»", "✯");
                else pre = pre.replaceFirst("✯", "»");
                PermissionUtils.INSTANCE.setPlayerPrefix(getSource(src), target, pre);
            });
            return CommandResult.success();
        }
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
