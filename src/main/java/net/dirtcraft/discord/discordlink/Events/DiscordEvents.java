package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Action;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandManager;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static net.dirtcraft.discord.discordlink.Utility.Utility.*;


public class DiscordEvents extends ListenerAdapter {

    private final DiscordCommandManager commandManager = new DiscordCommandManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        net.dv8tion.jda.api.entities.User author = event.getAuthor();
        boolean fake = author.isFake();
        boolean bot = author.isBot();
        try {
            if (fake || bot) return;
            if (event.getChannelType() == ChannelType.PRIVATE) processPrivateMessage(event);
            else processGuildMessage(event);
        } catch (Exception e){
            Utility.dmExceptionAsync(e, 248056002274918400L);
            throw e;
        }
    }

    public void processGuildMessage(MessageReceivedEvent event) {
        if (hasAttachment(event)) return;
        final MessageSource sender = new MessageSource(event);
        final boolean ready = Sponge.getGame().getState() == GameState.SERVER_STARTED;
        final String message = event.getMessage().getContentDisplay();
        final String rawMessage = event.getMessage().getContentRaw();
        final Action intent = Action.fromMessageRaw(rawMessage);

        if (intent.isChat() && ready) discordToMCAsync(sender, message);
        else if (intent.isBotCommand()) commandManager.process(sender, intent.getCommand(event));
        else if (intent.isConsole() && ready) {
            boolean executed = toConsole(intent.getCommand(event), sender, intent);
            if (executed && intent.isPrivate()) Utility.logCommand(sender, "__Executed Private Command__");
            event.getMessage().delete().queue();
        }
    }

    public void processPrivateMessage(MessageReceivedEvent event) {
        if (Sponge.getGame().getState() != GameState.SERVER_STARTED) return;
        final MessageSource sender = new MessageSource(event);
        final Action intent = Action.PRIVATE_COMMAND;
        final String message = Action.filterConsolePrefixes(event.getMessage().getContentRaw());
        if (toConsole(message, sender, intent)) logCommand(sender, "__Executed Private Command via DM__");
    }

    private static void discordToMCAsync(MessageSource sender, String message){
        Task.builder()
                .async()
                .execute(() -> discordToMc(sender, message))
                .submit(DiscordLink.getInstance());
    }

    private static void discordToMc(MessageSource sender, String message){
        try {
            final Optional<User> optUser = sender.getSpongeUser();
            final String mcUsername = optUser.map(User::getName).orElse(null);
            final Text.Builder toBroadcast = Text.builder();
            final String username;

            if (sender.isStaff() || mcUsername == null) username = sender.getHighestRank().getStyle() + sender.getEffectiveName().replaceAll(STRIP_CODE_REGEX, "");
            else username = sender.getNameStyle() + mcUsername;


            String[] messageElements = PluginConfiguration.Format.discordToServer
                    .replace("{username}", username)
                    .replace("»", sender.getChevron())
                    .split("\\{message}");

            if (messageElements.length == 0) return;
            toBroadcast.append(formatNonContentElements(sender, mcUsername, messageElements[0]));
            toBroadcast.append(formatContentElement(sender.isStaff(), message));
            if (messageElements.length > 1)
                toBroadcast.append(formatNonContentElements(sender, mcUsername, messageElements[1]));

            Sponge.getServer().getBroadcastChannel().send(toBroadcast.build());
        } catch (Exception e){
            Utility.dmExceptionAsync(e, 248056002274918400L);
            e.printStackTrace();
        }
    }

    private static Collection<Text> formatContentElement(boolean isStaff, String message){
        final List<Text> text = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        Arrays.stream(message.split(" ")).forEach(s->{
            if (s.toLowerCase().matches(URL_DETECT_REGEX)){
                s+=" ";
                Text.Builder url = Text.builder(s);
                try{
                    url.color(TextColors.BLUE);
                    url.style(TextStyles.UNDERLINE, TextStyles.ITALIC);
                    url.onClick(TextActions.openUrl(new URL(s)));
                    url.onHover(TextActions.showText(Text.of("§aClick to open link")));
                } catch (MalformedURLException e){
                    url.color(TextColors.BLUE);
                    url.style(TextStyles.UNDERLINE, TextStyles.ITALIC);
                    url.onHover(TextActions.showText(Text.of("§cMalformed URL")));
                }
                text.add(Text.of(sb.toString()));
                text.add(url.build());
                sb.setLength(0);
            } else {
                s+=" ";
                String element = isStaff? s.replaceAll(STRIP_CODE_REGEX, "§$1") : s.replaceAll(STRIP_CODE_REGEX,"");
                sb.append(element);
            }
        });
        if (sb.length() != 0){
            sb.setLength(sb.length()-1);
            text.add(Text.of(sb.toString()));
        }
        return text;
    }

    private static Text formatNonContentElements(MessageSource sender, String mcUsername, String element){
        Text.Builder text = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize(element));
        List<String> tooltip = new ArrayList<>();

        tooltip.add("&5&nClick me&7 to join &cDirtCraft's &9Discord");
        if (mcUsername != null) tooltip.add("&7MC Username&8: &6" + mcUsername);
        tooltip.add("&7Discord Name&8: &6" + sender.getUser().getName() + "&8#&7" + sender.getUser().getDiscriminator());
        tooltip.add("§7Rank§8: §6" + sender.getHighestRank().getName());
        tooltip.add("§7Staff Member§8: §6" + (sender.isStaff() ? "§aYes" : "§cNo"));

        text.onHover(TextActions.showText(format(String.join("\n", tooltip))));
        try{
            text.onClick(TextActions.openUrl(new URL("https://dirtcraft.net")));
        } catch (MalformedURLException ignored){}
        return text.toText();
    }

    private boolean hasAttachment(MessageReceivedEvent event) {
        for (net.dv8tion.jda.api.entities.Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment != null) return true;
        }
        return false;
    }

}
