package net.dirtcraft.discordlink.commands.sponge;

import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class Verify implements CommandExecutor {

    private final Database storage;

    public Verify(Database storage) {
        this.storage = storage;
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource source, @Nonnull CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(Utility.format("&cOnly a player can verify their Discord account!"));
        Optional<String> code = args.getOne("code");
        Player player = (Player) source;
        Task.builder()
                .async()
                .execute(code.<Runnable>map(s -> () -> verify(player, s)).orElse(() -> usage(player)))
                .submit(DiscordLink.get());
        return CommandResult.success();
    }

    private void verify(Player player, String code) {
        Optional<Verification.VerificationData> optData = storage.getVerificationData(player.getUniqueId());
        if (optData.isPresent() && !optData.flatMap(Verification.VerificationData::getMember).isPresent()){
            String invite = "&cYour Discord account has already been verified, but it is not in the DirtCraft Discord!";
            invite += "\n&5&nClick Me&7 to &ajoin &7it";
            sendDiscordInvite(player, invite);
            return;
        } else if (optData.isPresent()) {
            sendAlreadyVerifiedError(player, optData);
            return;
        } else if (!(optData = storage.getPendingData(code)).isPresent()) {
            player.sendMessage(Utility.format("&cThe code &e" + code + "&c is not valid!"));
            return;
        }

        DiscordMember member = optData.flatMap(Verification.VerificationData::getGuildMember).orElse(null);
        if (member == null && !optData.flatMap(Verification.VerificationData::getDiscordUser).isPresent()) {
            player.sendMessage(Utility.format("&cCould not verify your Discord account, please contact an Administrator!"));
        } else if (member == null) {
            String invite = "&cYour Discord account has been verified, but it is not in the DirtCraft Discord!";
            invite += "\n&5&nClick Me&7 to &ajoin &7it";
            sendDiscordInvite(player, invite);
        } else {
            storage.updateRecord(code, player.getUniqueId());
            Utility.setRoles(PlatformProvider.getPlayer(player), member);
            String discordName = member.getUser().getName();
            String discordTag = member.getUser().getDiscriminator();
            player.sendMessage(Utility.format("&7Successfully verified &6" + player.getName() + "&7 with &6" + discordName + "&8#&7" + discordTag));
        }
    }

    private void usage(Player player){
        Optional<Verification.VerificationData> optData = storage.getVerificationData(player.getUniqueId());
        if (optData.flatMap(Verification.VerificationData::getGuildMember).isPresent()) {
            sendAlreadyVerifiedError(player, optData);
        } else {
            String invite = "\n&5&nClick Me&7 to link your &9Discord&7 account and unlock additional features!\n";
            sendDiscordInvite(player, invite);
        }
    }

    private void sendDiscordInvite(Player player, String message){
        Text.Builder text = Text.builder().append(Utility.format(message));
        try {
            text.onHover(TextActions.showText(Utility.format("&5&nClick Me&7 to verify your Discord account!")));
            text.onClick(TextActions.openUrl(new URL("http://verify.dirtcraft.gg/")));
        } catch (MalformedURLException exception) {
            text.onHover(TextActions.showText(Utility.format("&cMalformed URL, contact Administrator!")));
        }
        player.sendMessage(text.build());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void sendAlreadyVerifiedError(Player player, Optional<Verification.VerificationData> optData){
        String response = optData.flatMap(Verification.VerificationData::getDiscordUser)
                .map(user -> "&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!")
                .orElse("&cYour account is already verified!");
        player.sendMessage(Utility.format(response));
    }
}
