package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Utility;
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
        Guild guild = Channels.getGuild();
        Database storage = DiscordLink.getInstance().getStorage();
        if (args.isEmpty()){
            DiscordLink.getInstance().getStorage().deleteRecord(source.getUser().getId());
            Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
            Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);
            if (verifiedRole != null && source.getRoles().contains(verifiedRole)) {
                guild.removeRoleFromMember(source, verifiedRole).queue();
            }
            if (donorRole !=  null && source.getRoles().contains(donorRole)) {
                guild.removeRoleFromMember(source, donorRole).queue();
            }
            source.sendCommandResponse("Successfully executed command:", "Successfully unlinked " + source.getPlayerData().flatMap(PlatformUser::getName).orElse("your account") + ".");
            return;
        }
        if (!source.hasRole(Roles.ADMIN)) throw new DiscordCommandException("You do not have permission to use this command on other users.");
        final String discordID = args.get(0);
        Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        Matcher matcher = pattern.matcher(discordID);
        Optional<Member> member;
        if (!matcher.matches() || !(member = Utility.getMemberById(matcher.group(1))).isPresent()) throw new DiscordCommandException("Invalid Discord ID");

        final GuildMember player = new GuildMember(member.get());
        final Optional<PlatformUser> user = player.getPlayerData();
        String response;
        if (user.isPresent()) {
            response = user.flatMap(PlatformUser::getName).get();
        } else {
            response = storage.getLastKnownUsername(matcher.group(1));
            if (response == null) response = storage.getVerificationData(matcher.group(1))
                    .flatMap(Database.VerificationData::getUUID)
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
