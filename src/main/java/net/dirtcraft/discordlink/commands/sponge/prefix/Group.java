package net.dirtcraft.discordlink.commands.sponge.prefix;

import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.storage.Settings;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.utility.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Group implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final User target = args.<User>getOne("Target")
                .orElseThrow(()->new CommandException(Text.of("§cYou must specify a target.")));
        final String group = args.<String>getOne("Group").orElse(null);
        if (group == null) {
            showGroups(src, target);
            return CommandResult.success();
        } else if (!target.hasPermission("group."+group)) {
            throw new CommandException(Text.of("§cYou must specify a group you possess."));
        }
        final Subject subject = Sponge.getServiceManager().provide(PermissionService.class)
                .map(PermissionService::getGroupSubjects)
                .flatMap(gs->gs.getSubject(group))
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
            PermissionProvider.INSTANCE.setPlayerPrefix(getSource(src), target, carat + indicator + rest);
        } else PermissionProvider.INSTANCE.setPlayerPrefix(getSource(src), target, prefix);
        return CommandResult.success();
    }

    private void showGroups(CommandSource src, User target){
        AtomicBoolean flip = new AtomicBoolean(false);
        List<Text> response = Sponge.getServiceManager().provideUnchecked(PermissionService.class)
                .getGroupSubjects()
                .getLoadedSubjects()
                .stream()
                .map(g->new Pair<>(g.getIdentifier(), g.getOption("prefix")))
                .filter(g->g.getValue().isPresent())
                .filter(g->target.hasPermission("group." + g.getKey()))
                .map(g->{
                    boolean flipper = flip.get();
                    flip.set(!flipper);
                    char color = flipper? '3' : 'b';
                    Text.Builder builder = TextSerializers.FORMATTING_CODE.deserialize("&" + color + g.getKey()).toBuilder();
                    builder.onHover(TextActions.showText(TextSerializers.FORMATTING_CODE.deserialize(g.getValue().get())));
                    builder.onClick(TextActions.runCommand("/prefix group " + target.getName() + " " + g.getKey()));
                    return builder.build();
                }).collect(Collectors.toList());
        PaginationList.builder()
                .contents(response)
                .title(TextSerializers.FORMATTING_CODE.deserialize("&eAvailable Groups"))
                .padding(TextSerializers.FORMATTING_CODE.deserialize("&6="))
                .linesPerPage(16)
                .sendTo(src);
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
