package net.dirtcraft.discord.discordlink.Commands.Sponge.prefix;

import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Group implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final User target = args.<User>getOne("Target")
                .orElseThrow(()->new CommandException(Text.of("§cYou must specify a target.")));
        final String group = args.<String>getOne("Group")
                .filter(s->target.hasPermission("group."+s))
                .orElseThrow(()->new CommandException(Text.of("§cYou must specify a group you possess.")));
        final Subject subject = Sponge.getServiceManager().provide(PermissionService.class)
                .map(PermissionService::getGroupSubjects)
                .filter(gs->gs.hasRegistered(group))
                .map(gs->gs.get(group))
                .orElseThrow(()->new CommandException(Text.of("§cInvalid Group.")));
        final String prefix = subject.getOption("prefix")
                .filter(s->!s.equalsIgnoreCase(""))
                .orElseThrow(()->new CommandException(Text.of("§cGroup specified has no prefix.")));
        final Map.Entry<String,String> indicatorSet = Settings.STAFF_PREFIXES.entrySet().stream()
                .filter(p->target.hasPermission(p.getKey()))
                .findFirst()
                .orElse(null);

        if (indicatorSet != null && !subject.hasPermission(indicatorSet.getKey())){
            String indicator = prefix.replaceAll("(?i)^.*?(([§&][0-9a-frlonm])+) *\\[.*", "$1");
            if (indicator.equalsIgnoreCase(prefix)) indicator = "&f";
            indicator = String.format("%s[%s%s]", indicator, indicatorSet.getValue(), indicator);
            List<String> bits = new ArrayList<>(Arrays.asList(prefix.split(" ")));
            String carat = bits.isEmpty()? "" : bits.remove(0) + " ";
            String rest = String.join(" ", bits);
            PermissionUtils.INSTANCE.setPlayerPrefix(getSource(src), target, carat + indicator + rest);
        } else PermissionUtils.INSTANCE.setPlayerPrefix(getSource(src), target, prefix);
        return CommandResult.success();
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
