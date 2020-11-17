package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.*;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandManager;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformChat;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static net.dirtcraft.discord.discordlink.Utility.Utility.logCommand;
import static net.dirtcraft.discord.discordlink.Utility.Utility.toConsole;


public class DiscordEvents extends ListenerAdapter {

    private final DiscordCommandManager commandManager = new DiscordCommandManager();

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        boolean gamechat = Channels.isGamechat(event.getChannel());
        boolean privateDm = event.getChannelType() == ChannelType.PRIVATE;
        if (!gamechat && !privateDm || event.getAuthor().isBot() || hasAttachment(event)) return;
        CompletableFuture.runAsync(()->{
            try {
                final Channel chat = new Channel(event.getChannel().getIdLong());
                final MessageSource sender = new MessageSource(event);
                if (!sender.isVerified() && !sender.isStaff()) processUnverifiedMessage(chat, sender, event);
                else if (privateDm) processPrivateMessage(chat, sender, event);
                else processGuildMessage(chat, sender, event);
            } catch (Exception e){
                DiscordLink.getInstance().getLogger().warn("Exception while processing gamechat message!", e);
                Utility.dmExceptionAsync(e, 248056002274918400L);
            }
        });
    }

    public void processGuildMessage(Channel chat, MessageSource sender, MessageReceivedEvent event) {
        final String rawMessage = event.getMessage().getContentRaw();
        final Action intent = Action.fromMessageRaw(rawMessage);

        if (intent.isBotCommand()) commandManager.process(sender, intent.getCommand(event));
        else if (PlatformUtils.isGameReady() && intent.isChat()) PlatformChat.discordToMCAsync(sender, event);
        else if (PlatformUtils.isGameReady() && intent.isConsole()) {
            boolean executed = toConsole(chat, intent.getCommand(event), sender, intent);
            if (executed && intent.isPrivate()) Utility.logCommand(sender, "__Executed Private Command__");
            event.getMessage().delete().queue(s->{},e->{});
        }
    }

    public void processPrivateMessage(Channel chat, MessageSource sender, MessageReceivedEvent event) {
        if (!PlatformUtils.isGameReady()) return;
        final Action intent = Action.fromMessageRaw(event.getMessage().getContentRaw()) == Action.DISCORD_COMMAND? Action.DISCORD_COMMAND: Action.PRIVATE_COMMAND;
        final String message = Action.filterConsolePrefixes(event.getMessage().getContentRaw());
        if (intent == Action.DISCORD_COMMAND) commandManager.process(sender, intent.getCommand(event));
        else if (toConsole(chat, message, sender, intent)) logCommand(sender, "__Executed Private Command via DM__");
    }

    public void processUnverifiedMessage(Channel chat, MessageSource sender, MessageReceivedEvent event){
        if (event.getChannelType() != ChannelType.PRIVATE) event.getMessage().delete().queue();
        Database database = DiscordLink.getInstance().getStorage();
        Database.VerificationData data = database.getVerificationData(sender.getId()).orElse(null);
        if (data != null && data.getUUID().isPresent()) {
            Utility.setRoleIfAbsent(Channels.getGuild(), sender, Roles.VERIFIED);
            sender.sendCommandResponse("Verified role was missing, But you appear to be verified so it has been added again. Please send message or command again.");
        } else if (data != null && data.getCode().isPresent()) {
            String code = data.getCode().get();
            String message = "You need to be verified in order to use the gamechat or send commands.\n";
            message += "Please enter /verify " + code + " in-game to verify your account.";
            MessageEmbed embed = Utility.embedBuilder().addField("Error", message, false).build();
            sender.sendPrivateMessage(embed);
        } else {
            String code = Utility.getSaltString();
            database.createRecord(sender.getId(), code);
            String message = "You need to be verified in order to use the gamechat or send commands.\n";
            message += "Please enter /verify " + code + " in-game to verify your account.";
            MessageEmbed embed = Utility.embedBuilder().addField("Error", message, false).build();
            sender.sendPrivateMessage(embed);
        }
    }

    private boolean hasAttachment(MessageReceivedEvent event) {
        for (net.dv8tion.jda.api.entities.Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment != null) return true;
        }
        return false;
    }

}
