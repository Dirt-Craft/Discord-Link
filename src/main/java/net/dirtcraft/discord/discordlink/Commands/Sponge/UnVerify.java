package net.dirtcraft.discord.discordlink.Commands.Sponge;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import javax.annotation.Nullable;

public class UnVerify implements CommandExecutor {

    private final Storage storage;

    public UnVerify(Storage storage) {
        this.storage = storage;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(Utility.format("&cOnly a player can unverify their Discord account!"));
        Player player = (Player) source;

        Task.builder()
                .async()
                .execute(() -> {
                    String discordID;
                    if (!storage.isVerified(player.getUniqueId()) || (discordID= storage.getDiscordUser(player.getUniqueId())) == null) {
                        player.sendMessage(Utility.format("&cYour account is not verified!"));
                        return;
                    }

                    User user = SpongeDiscordLib.getJDA().retrieveUserById(discordID).complete();
                    player.sendMessage(Utility.format(user != null ?
                            "&7The account &6" + user.getName() + "&8#&7" + user.getDiscriminator() + " has been &cunverified" :
                            "&7Your account has been &cunverified"));

                    if (user != null) {
                        Guild guild = SpongeDiscordLib.getJDA().getGuildById(PluginConfiguration.Main.discordServerID);
                        Member member = Utility.getMember(user).orElse(null);
                        if (member == null || guild == null) return;
                        Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
                        Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);

                        if (verifiedRole != null && member.getRoles().contains(verifiedRole)) {
                            guild.removeRoleFromMember(member, verifiedRole).queue();
                        }
                        if (donorRole != null && member.getRoles().contains(donorRole)) {
                            guild.removeRoleFromMember(member, donorRole).queue();
                        }
                    }

                })
                .submit(DiscordLink.getInstance());

        return CommandResult.success();
    }
}
