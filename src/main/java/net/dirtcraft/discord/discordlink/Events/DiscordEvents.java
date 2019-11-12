package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordEvents extends ListenerAdapter {

    private Storage storage;

    public DiscordEvents(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(SpongeDiscordLib.getGamechatChannelID())) return;
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if (hasAttachment(event)) return;

        String username = TextSerializers.FORMATTING_CODE.stripCodes(event.getAuthor().getName());
        String effectiveName = TextSerializers.FORMATTING_CODE.stripCodes(event.getMember().getEffectiveName());

        String message = event.getMessage().getContentDisplay();
        String rawMessage = event.getMessage().getContentRaw();

        if (rawMessage.startsWith(PluginConfiguration.Main.botPrefix + "list")) {
            Utility.listCommand(event);
            return;
        }

        if (rawMessage.startsWith(PluginConfiguration.Main.botPrefix + "e-stop")) {
            Utility.emergencyStop(event, false);
            return;
        }

        if (rawMessage.startsWith(PluginConfiguration.Main.botPrefix + "e-stop -h")) {
            Utility.emergencyStop(event, true);
            return;
        }

        if (rawMessage.startsWith(PluginConfiguration.Main.botPrefix + "unstuck")) {
            Utility.unstuck(event, storage);
            return;
        }

        if (rawMessage.startsWith(PluginConfiguration.Main.consolePrefix)) {
            Utility.toConsole(event);
            return;
        }

        Role staffRole = event.getGuild().getRoleById(PluginConfiguration.Roles.staffRoleID);
        Role ownerRole = event.getGuild().getRoleById(PluginConfiguration.Roles.ownerRoleID);

        boolean isStaff = event.getMember().getRoles().contains(staffRole);
        boolean isOwner = event.getMember().getRoles().contains(ownerRole);

        String staff = isStaff ? "&aYes" : "&cNo";

        Task.builder()
                .async()
                .execute(() -> {
                    Text.Builder toBroadcast = Text.builder();
                    String mcUsername = storage.getLastKnownUsername(storage.getUUIDfromDiscordID(event.getMember().getUser().getId()));
                    if (!isStaff) {

                        Role donorRole = event.getGuild().getRoleById(PluginConfiguration.Roles.donatorRoleID);

                        toBroadcast.append(Utility.format(PluginConfiguration.Format.discordToServer
                                .replace("{username}", mcUsername != null ? mcUsername : username)
                                .replace("{message}", TextSerializers.FORMATTING_CODE.stripCodes(message))
                                .replace("»", event.getMember().getRoles().contains(donorRole) ? "&6&l»" : "&9&l»")));

                    } else {
                        toBroadcast.append(
                                Utility.format(PluginConfiguration.Format.discordToServer
                                        .replace("{username}", effectiveName)
                                        .replace("{message}", message)
                                        .replace("»", !isOwner ? "&c&l»" : "&4&l»")
                                ));
                    }

                    ArrayList<String> hover = new ArrayList<>();
                    hover.add("&5&nClick me&7 to join &cDirtCraft's &9Discord");
                    if (mcUsername != null) {
                        hover.add("&7MC Username&8: &6" + mcUsername);
                    }
                    hover.add("&7Discord Name&8: &6" + event.getAuthor().getName() + "&8#&7" + event.getAuthor().getDiscriminator());
                    if (event.getMember().getNickname() != null) {
                        hover.add("&7Nickname&8: &6" + event.getMember().getNickname());
                    }
                    hover.add("&7Staff Member&8: &6" + staff);

                    try {
                        List<String> urls = checkURLs(event.getMessage().getContentRaw());
                        if (!(urls.size() > 0)) {
                            toBroadcast.onClick(TextActions.openUrl(new URL("http://discord.dirtcraft.gg/")));

                            toBroadcast.onHover(TextActions.showText(Utility.format(String.join("\n", hover))));

                        } else {
                            toBroadcast.onClick(TextActions.openUrl(new URL(urls.get(0))));

                            toBroadcast.onHover(TextActions.showText(Utility.format(String.join("\n", hover))));
                        }
                    } catch (MalformedURLException exception) {
                        hover.add("&cMalformed URL, Contact Administrator");

                        toBroadcast.onHover(TextActions.showText(Utility.format(String.join("\n", hover))));

                        exception.printStackTrace();
                    }

                    Sponge.getServer().getBroadcastChannel().send(toBroadcast.build());

                })
                .submit(DiscordLink.getInstance());

    }

    public static List<String> checkURLs(String text)
    {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    private boolean hasAttachment(MessageReceivedEvent event) {
        boolean hasAttachment = false;
        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment != null) {
                hasAttachment = true;
            }
        }
        return hasAttachment;
    }

}
