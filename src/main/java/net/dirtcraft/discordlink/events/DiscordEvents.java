package net.dirtcraft.discordlink.events;

import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.channels.MessageIntent;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandManagerImpl;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.utility.PlatformChat;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static net.dirtcraft.discordlink.utility.Utility.logCommand;
import static net.dirtcraft.discordlink.utility.Utility.toConsole;


public class DiscordEvents extends ListenerAdapter {

    private final DiscordCommandManagerImpl commandManager;
    private final ChannelManagerImpl channelManager;
    private final UserManagerImpl userManager;
    private final Database storage;

    public DiscordEvents(DiscordLink discordLink){
        this.commandManager = discordLink.getCommandManager();
        this.channelManager = discordLink.getChannelManager();
        this.userManager = discordLink.getUserManager();
        this.storage = discordLink.getStorage();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        boolean gamechat = channelManager.isGamechat(event.getChannel());
        boolean privateDm = event.getChannelType() == ChannelType.PRIVATE;
        if (!gamechat && !privateDm || event.getAuthor().isBot() || hasAttachment(event)) return;
        CompletableFuture.runAsync(()->{
            try {
                final MessageSourceImpl sender = userManager.getMember(event);
                if (!sender.isVerified() && !sender.isStaff()) processUnverifiedMessage(sender, event);
                else if (privateDm) processPrivateMessage(sender, event);
                else processGuildMessage(sender, event);
            } catch (Exception e){
                DiscordLink.get().getLogger().warn("Exception while processing gamechat message!", e);
                Utility.dmExceptionAsync(e, 248056002274918400L);
            }
        });
    }

    public void processGuildMessage(MessageSourceImpl sender, MessageReceivedEvent event) {
        if (sender.isMuted() && !sender.isStaff()) {
            if (event.getChannelType() != ChannelType.PRIVATE) event.getMessage().delete().queue(s->{},e->{});
            sender.sendPrivateMessage("<@" + sender.getId() + "> You are **not** allowed to talk there! Please open an appeal in <#590388043379376158> to lift your sanction.");
            return;
        }

        final String rawMessage = event.getMessage().getContentRaw();
        final MessageIntent intent = MessageIntent.fromMessageRaw(rawMessage);
        if (intent.isBotCommand()) commandManager.process(sender, intent.getCommand(event));
        else if (PlatformProvider.isGameReady() && intent.isChat()) PlatformChat.discordToMCAsync(sender, event);
        else if (PlatformProvider.isGameReady() && intent.isConsole()) {
            boolean executed = toConsole(intent.getCommand(event), sender, intent);
            if (executed && intent.isPrivate()) Utility.logCommand(sender, "__Executed Private Command__");
            event.getMessage().delete().queue(s->{},e->{});
        }
    }

    public void processPrivateMessage(MessageSourceImpl sender, MessageReceivedEvent event) {
        if (!PlatformProvider.isGameReady()) return;
        final MessageIntent intent = MessageIntent.fromMessageRaw(event.getMessage().getContentRaw()) == MessageIntent.DISCORD_COMMAND? MessageIntent.DISCORD_COMMAND: MessageIntent.PRIVATE_COMMAND;
        final String message = MessageIntent.filterConsolePrefixes(event.getMessage().getContentRaw());
        if (intent == MessageIntent.DISCORD_COMMAND) commandManager.process(sender, intent.getCommand(event));
        else if (toConsole(message, sender, intent)) logCommand(sender, "__Executed Private Command via DM__");
    }

    public void processUnverifiedMessage(MessageSourceImpl sender, MessageReceivedEvent event){
        if (event.getChannelType() != ChannelType.PRIVATE) event.getMessage().delete().queue();
        Database database = storage;
        Verification.VerificationData data = database.getVerificationData(sender.getId()).orElse(null);
        if (data != null && data.getUUID().isPresent()) {
            sender.setRoleIfAbsent(DiscordRoles.VERIFIED);
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
