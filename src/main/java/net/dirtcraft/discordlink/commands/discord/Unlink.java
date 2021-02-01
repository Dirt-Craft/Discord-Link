package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unlink implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        DiscordLink discordLink = DiscordLink.get();
        Guild guild = discordLink.getChannelManager().getGuild();
        Database storage = discordLink.getStorage();
        UserManagerImpl userManager = discordLink.getUserManager();
        if (args.isEmpty()){
            storage.deleteRecord(source.getUser().getId());
            Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
            Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);
            if (verifiedRole != null && source.getRoles().contains(verifiedRole)) {
                guild.removeRoleFromMember(source, verifiedRole).queue();
            }
            if (donorRole !=  null && source.getRoles().contains(donorRole)) {
                guild.removeRoleFromMember(source, donorRole).queue();
            }
            source.sendCommandResponse("Successfully executed command:", "Successfully unlinked " + source.getPlayerData().flatMap(PlatformUser::getNameIfPresent).orElse("your account") + ".");
            return;
        } else if (!source.hasRole(DiscordRoles.ADMIN)) throw new DiscordCommandException("You do not have permission to use this command on other users.");

        final String discordID = args.get(0);
        Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        Matcher matcher = pattern.matcher(discordID);
        Optional<Member> member;
        if (!matcher.matches() || !(member = Utility.getMemberById(matcher.group(1))).isPresent()) throw new DiscordCommandException("Invalid Discord ID");

        final GuildMember player = userManager.getMember(member.get());
        final Optional<PlatformUser> user = player.getPlayerData();
        String response;
        if (user.isPresent()) {
            response = user.flatMap(PlatformUser::getNameIfPresent).get();
        } else {
            response = storage.getLastKnownUsername(matcher.group(1));
            if (response == null) response = storage.getVerificationData(matcher.group(1))
                    .flatMap(Verification.VerificationData::getUUID)
                    .map(UUID::toString)
                    .orElseThrow(()->new DiscordCommandException("The user was not verified!"));
        }
        storage.deleteRecord(matcher.group(1));
        Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);

        if (verifiedRole != null && player.getRoles().contains(verifiedRole)) {
            guild.removeRoleFromMember(player, verifiedRole).queue();
        }
        if (donorRole != null && player.getRoles().contains(donorRole)) {
            guild.removeRoleFromMember(player, donorRole).queue();
        }
        source.sendCommandResponse("Discord-Link Verification:", "Successfully unlinked " + response + ".");
    }
}
